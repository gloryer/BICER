package ca.uwaterloo.ece.bicer;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import ca.uwaterloo.ece.bicer.data.BIChange;
import ca.uwaterloo.ece.bicer.data.DeletedLineInCommits;
import ca.uwaterloo.ece.bicer.utils.Utils;

public class BICCollector {

	public static void main(String[] args) {
		new BICCollector().run(args);
	}

	private String gitURI;
	private String pathToBuggyIDs;
	private boolean help;
	private boolean considerTestCases;
	private boolean unTrackDeletedBIlines;
	//private boolean verbose;
	private String strStartDate;
	private String strEndDate;
	private String strLabelEndDate;
	private Date startDate;
	private Date endDate;
	private Date labelEndDate;
	private ArrayList<String> bugIDs;

	private Git git;
	private Repository repo;

	public void run(String[] args) {
		Options options = createOptions();

		if(parseOptions(options, args)){
			if (help){
				printHelp(options);
				return;
			}

			// (1) load BugsIDs
			String bugIDPreFix="";
			if(pathToBuggyIDs!=null){
				bugIDs = Utils.getLines(pathToBuggyIDs, false);
				bugIDPreFix = bugIDs.get(0).split("-")[0] + "-";
			}

			// (2) get commits between the start and end dates
			ArrayList<RevCommit> commits = getRevCommits();

			repo = git.getRepository();

			// (3) get deleted lines from commits. This data are used to identify BI lines that are only deleted and are in INSERT hunks in bug-fixing commits.
			HashMap<String,ArrayList<DeletedLineInCommits>> mapDeletedLines = null;
			if(!unTrackDeletedBIlines)
				mapDeletedLines = getDeletedLinesInCommits(commits);

			// (4) find bug-fixing commits and get BI lines
			ArrayList<BIChange> lstBIChanges = new ArrayList<BIChange>();
			for(RevCommit rev:commits){
				String message = rev.getFullMessage();

				// Create matcher on file
				Pattern pattern=null;
				
				if(pathToBuggyIDs!=null){
					pattern = Pattern.compile(bugIDPreFix + "\\d+");
				}else{
					pattern = Pattern.compile("fix|bug",Pattern.CASE_INSENSITIVE);
				}
				
				Matcher matcher = pattern.matcher(message);

				while(matcher.find()){
					if(pathToBuggyIDs==null || (bugIDs!=null && bugIDs.contains(matcher.group(0)))){

						// Now it's a bug-fixing commit!

						// get a list of files in the commit
						RevCommit parent = rev.getParent(0);
						if(parent==null){
							System.err.println("WARNING: Parent commit does not exist: " + rev.name() );
							continue;
						}

						DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
						df.setRepository(repo);
						df.setDiffAlgorithm(Utils.diffAlgorithm);
						df.setDiffComparator(Utils.diffComparator);
						df.setDetectRenames(true);
						List<DiffEntry> diffs;
						try {

							// do diff
							diffs = df.scan(parent.getTree(), rev.getTree());
							for (DiffEntry diff : diffs) {
								ArrayList<Integer> lstIdxOfDeletedLinesInPrevFixFile = new ArrayList<Integer>();
								ArrayList<Integer> lstIdxOfOnlyInsteredLinesInFixFile = new ArrayList<Integer>();
								String oldPath = diff.getOldPath();
								String newPath = diff.getNewPath();

								// ignore when no previous revision of a file, Test files, and non-java files.
								if(oldPath.equals("/dev/null") || newPath.indexOf("Test")>=0  || !newPath.endsWith(".java")) continue;

								if(!considerTestCases && newPath.indexOf("/test")>=0) continue;
								
								String id =  rev.name() + "";

								// get preFixSource and fixSource without comments
								String prevFileSource=Utils.removeComments(Utils.fetchBlob(repo, id +  "~1", oldPath));
								String fileSource=Utils.removeComments(Utils.fetchBlob(repo, id, newPath));
								
								/*FileWriter fw = new FileWriter("2007.txt");
								PrintWriter pw = new PrintWriter(fw);
								pw.println(prevFileSource);
								pw.close();*/
								System.out.println(prevFileSource + "\n"+ "-|");
								//System.out.println(fileSource + "\n" +"-|");
								
								EditList editList = Utils.getEditListFromDiff(prevFileSource, fileSource);

								// get line indices that are related to BI lines.
								for(Edit edit:editList){

									if(edit.getType()!=Edit.Type.INSERT){

										int beginA = edit.getBeginA();
										int endA = edit.getEndA();

										for(int i=beginA; i < endA ; i++)
											lstIdxOfDeletedLinesInPrevFixFile.add(i);

									}else{
										int beginB = edit.getBeginB();
										int endB = edit.getEndB();

										for(int i=beginB; i < endB ; i++)
											lstIdxOfOnlyInsteredLinesInFixFile.add(i);
									}
								}

								// get BI commit from lines in lstIdxOfOnlyInsteredLines
								lstBIChanges.addAll(getBIChangesFromBILineIndices(id,rev.getCommitTime(), newPath, oldPath, prevFileSource,lstIdxOfDeletedLinesInPrevFixFile));
								if(!unTrackDeletedBIlines)
									lstBIChanges.addAll(getBIChangesFromDeletedBILine(id,rev.getCommitTime(),mapDeletedLines,fileSource,lstIdxOfOnlyInsteredLinesInFixFile,oldPath,newPath));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						df.close();
						break;
					}
				}
			}
			Collections.sort(lstBIChanges);
			/*System.out.println("BISha1\toldPath\tPath\tFixSha1\tBIDate\tFixDate\tLineNumInBI\tLineNumInPreFix\tisAddedLine\tLine");
			for(BIChange biChange:lstBIChanges){
				System.out.println(biChange.getBISha1() + "\t" +
										biChange.getBIPath() + "\t" +
										biChange.getPath() + "\t" +
										biChange.getFixSha1() + "\t" +
										biChange.getBIDate() + "\t" +
										biChange.getFixDate() + "\t" +
										biChange.getLineNum() + "\t" +
										biChange.getLineNumInPrevFixRev() + "\t" +
										biChange.getIsAddedLine() + "\t" +
										biChange.getLine());*/
	//		}
		}
	}

	private ArrayList<BIChange> getBIChangesFromDeletedBILine(String fixSha1, int fixCommitTime,
			HashMap<String, ArrayList<DeletedLineInCommits>> mapDeletedLines, String fileSource,
			ArrayList<Integer> lstIdxOfOnlyInsteredLinesInFixFile, String oldPath, String path) {
		
		ArrayList<BIChange> biChanges = new ArrayList<BIChange>();
		
		ArrayList<Integer> arrIndicesInOriginalFileSource = lstIdxOfOnlyInsteredLinesInFixFile;// getOriginalLineIndices(origFileSource,fileSource,lstIdxOfOnlyInsteredLines);
		
		String[] arrOrigFileSource = fileSource.split("\n");
		for(int lineIdx:arrIndicesInOriginalFileSource){
			if(arrOrigFileSource.length<=lineIdx) continue; // split("\n") ignore last empty lines. So, lineIdx can be greater the array length. Ignore this case
			String addedlineInFixCommit = arrOrigFileSource[lineIdx].trim();
			ArrayList<DeletedLineInCommits> lstDeletedLines = mapDeletedLines.get(addedlineInFixCommit);
			
			if(lstDeletedLines==null)
				continue;
			
			DeletedLineInCommits deletedLineToConsider = null;
			for(DeletedLineInCommits deletedLine:lstDeletedLines){
				if(deletedLine.getBIDate().compareTo(Utils.getStringDateTimeFromCommitTime(fixCommitTime)) < 0
						&& deletedLine.getPath().equals(oldPath)){
					deletedLineToConsider = deletedLine;
				}
			}
			if(deletedLineToConsider==null)
				continue;	// no deleted lines exist in lstDeletedLines. Do process next added line
			else{
				
				// heuristic: line num difference between BI and Fix commits is <= 10, the consider the line is BILine.
				int lineGap = Math.abs(deletedLineToConsider.getLineNum() - (lineIdx+1));
				if(lineGap > 10)
					continue;
				
				// get BIChange from the deleted line
				String BISha1 = deletedLineToConsider.getSha1();
				String biPath = deletedLineToConsider.getPath();
				//String path;
				String FixSha1 = fixSha1;
				String BIDate = deletedLineToConsider.getBIDate();
				String FixDate = Utils.getStringDateTimeFromCommitTime(fixCommitTime);
				int lineNumInPrevFixRev = lineIdx+1; // this info is not important in case of a deleted line.
				 
				BIChange biChange = new BIChange(BISha1,biPath,FixSha1,path,BIDate,FixDate,lineIdx+1,lineNumInPrevFixRev,addedlineInFixCommit,false);
				biChanges.add(biChange);
			}
		}
		
		return biChanges;
	}

	private ArrayList<BIChange> getBIChangesFromBILineIndices(String fixSha1,int fixCommitTime, String path, String prevPath,
			String prevFileSource, ArrayList<Integer> lstIdxOfDeletedLinesInPrevFixFile) {

		ArrayList<BIChange> biChanges = new ArrayList<BIChange>();

		// do Blame
		BlameCommand blamer = new BlameCommand(repo);
		ObjectId commitID;
		try {
			commitID = repo.resolve(fixSha1 + "~1");
			blamer.setStartCommit(commitID);
			blamer.setFilePath(prevPath);
			BlameResult blame = blamer.setDiffAlgorithm(Utils.diffAlgorithm).setTextComparator(Utils.diffComparator).setFollowFileRenames(true).call();

			ArrayList<Integer> arrIndicesInOriginalFileSource = lstIdxOfDeletedLinesInPrevFixFile; //getOriginalLineIndices(origPrvFileSource,prevFileSource,lstIdxOfDeletedLines);
			for(int lineIndex:arrIndicesInOriginalFileSource){
				RevCommit commit = blame.getSourceCommit(lineIndex);

				String BISha1 = commit.name();
				String biPath = blame.getSourcePath(lineIndex);
				//String path;
				String FixSha1 = fixSha1;
				String BIDate = Utils.getStringDateTimeFromCommitTime(commit.getCommitTime());
				if(!(strStartDate.compareTo(BIDate)<=0 && BIDate.compareTo(strEndDate)<=0)) // only consider BISha1 whose date is bewteen startDate and endDate
					continue;
				String FixDate = Utils.getStringDateTimeFromCommitTime(fixCommitTime);
				int lineNum = blame.getSourceLine(lineIndex)+1;
				int lineNumInPrevFixRev = lineIndex+1;
				
				if(prevFileSource.split("\n")[lineIndex].trim().equals("")) // ignore empty line (this happens as comments are removed)
					continue;
				
				BIChange biChange = new BIChange(BISha1,biPath,FixSha1,path,BIDate,FixDate,lineNum,lineNumInPrevFixRev, prevFileSource.split("\n")[lineIndex].trim(),true);
				biChanges.add(biChange);
			}

		} catch (RevisionSyntaxException | IOException | GitAPIException e) {
			e.printStackTrace();
		}

		return biChanges;
	}

	/**
	 * Get all deleted lines in commits. The return object, HashMap, is used to identify a BI commit that induce bug-fixing by a deleted line in the BI commit
	 * @param commits
	 * @return HashMap<String, ArrayList<DeletedLineInCommits>>
	 */
	private HashMap<String, ArrayList<DeletedLineInCommits>> getDeletedLinesInCommits(ArrayList<RevCommit> commits) {

		// deletedLines are order by commit date (DESC, i.e., recent commit first)
		HashMap<String, ArrayList<DeletedLineInCommits>> deletedLines = new HashMap<String, ArrayList<DeletedLineInCommits>>();

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repo);
		df.setDiffAlgorithm(Utils.diffAlgorithm);
		df.setDiffComparator(Utils.diffComparator);
		df.setDetectRenames(true);
		
		// Traverse all commits to collect deleted lines.
		//int i=0;
		//System.out.println(commits.size());
		for(RevCommit rev:commits){

			// Get basic commit info
			String sha1 =  rev.name() + "";
			String date = Utils.getStringDateTimeFromCommitTime(rev.getCommitTime()); // GMT time string
			
			try {
				// Note that CommitTime string (date) in GMT was processed as local time (America/Toronto) in previous tool
				// so parse date string as local time
				if((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)).compareTo(endDate)>0)
					continue;
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			//System.out.println(i++);
			// Get diffs from affected files in the commit
			RevCommit preRev = rev.getParent(0);
			List<DiffEntry> diffs;
			try {
				// Deal with diff and get only deleted lines
				diffs = df.scan(preRev.getTree(), rev.getTree());
				
				for (DiffEntry diff : diffs) {
					
					String oldPath = diff.getOldPath();
					String newPath = diff.getNewPath();

					// Skip test case files
					if(newPath.indexOf("Test")>=0 || !newPath.endsWith(".java")) continue;
					
					if(!considerTestCases && newPath.indexOf("/test")>=0) continue;
					
					// Do diff on files without comments to only consider code lines
					String prevfileSource=Utils.removeComments(Utils.fetchBlob(repo, sha1 +  "~1", oldPath));
					String fileSource=Utils.removeComments(Utils.fetchBlob(repo, sha1, newPath));	
					
					EditList editList = Utils.getEditListFromDiff(prevfileSource, fileSource);
					
					String[] arrPrevfileSource=prevfileSource.split("\n");
					for(Edit edit:editList){
						// Deleted lines are in DELETE and REPLACE types. So, ignore INSERT type.
						if(!edit.getType().equals(Edit.Type.INSERT)){

							int beginA = edit.getBeginA();
							int endA = edit.getEndA();

							// Line num is not that important for deleted lines in BI commits
							for(int lineIdx = beginA; lineIdx < endA; lineIdx++){
								if(arrPrevfileSource.length<=lineIdx) continue; // split("\n") ignore last empty lines. So, lineIdx can be greater the array length. Ignore this case
								String line = arrPrevfileSource[lineIdx].trim();
								if(line.length() <2) continue; // heuristic: ignore "}" or "{". only consider the line whose length >= 2
								DeletedLineInCommits deletedLine = new DeletedLineInCommits(sha1,date,oldPath,newPath,lineIdx+1,line);
								if(!deletedLines.containsKey(line)){
									ArrayList<DeletedLineInCommits> lstDeletedLines = new ArrayList<DeletedLineInCommits>();
									lstDeletedLines.add(deletedLine);
									deletedLines.put(line,lstDeletedLines);
								}else{
									deletedLines.get(line).add(deletedLine);
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		df.close();
		
		return deletedLines;
	}

	private ArrayList<RevCommit> getRevCommits() {
		ArrayList<RevCommit> commits = new ArrayList<RevCommit>();

		try {

			git = Git.open( new File(gitURI) );

			Iterable<RevCommit> logs = git.log()
					.call();

			//SimpleDateFormat ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss zzz");
			for (RevCommit rev : logs) {
				Date commitDate = new Date(rev.getCommitTime()* 1000L);

				if(startDate.compareTo(commitDate)<=0 && commitDate.compareTo(labelEndDate)<=0)
					commits.add(rev);
			}             

		} catch (IOException | GitAPIException e) {
			System.err.println("Repository does not exist: " + gitURI);
		}

		return commits;
	}

	Options createOptions(){

		// create Options object
		Options options = new Options();

		// add options
		options.addOption(Option.builder("g").longOpt("git")
				.desc("Git URI")
				.hasArg()
				.argName("URI")
				.required()
				.build());

		options.addOption(Option.builder("b").longOpt("bugs")
				.desc("A file path for bug report IDs")
				.hasArg()
				.argName("file")
				.build());

		options.addOption(Option.builder("s").longOpt("startdate")
				.desc("Start date for collecting bug-introducing changes")
				.hasArg()
				.argName("Start date")
				.required()
				.build());
		
		options.addOption(Option.builder("e").longOpt("enddate")
				.desc("End date for collecting bug-introducing changes")
				.hasArg()
				.argName("End date")
				.required()
				.build());

		options.addOption(Option.builder("l").longOpt("lenddate")
				.desc("End date for collecting labels")
				.hasArg()
				.argName("Label End date")
				.required()
				.build());
		
		options.addOption(Option.builder("t")
				.desc("Consider test cases")
				.build());
		
		options.addOption(Option.builder("u")
				.desc("Do not track deleted BI lines")
				.build());

		options.addOption(Option.builder("h").longOpt("help")
				.desc("Help")
				.build());


		options.addOption(Option.builder("v").longOpt("verbose")
				.desc("Verbose")
				.build());

		return options;
	}

	boolean parseOptions(Options options,String[] args){

		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);

			gitURI = cmd.getOptionValue("g");
			if(cmd.getOptionValue("b")!=null)
				pathToBuggyIDs = cmd.getOptionValue("b");

			help = cmd.hasOption("h");
			considerTestCases = cmd.hasOption("t");
			unTrackDeletedBIlines = cmd.hasOption("u");
			//verbose = cmd.hasOption("v");

			strStartDate = cmd.getOptionValue("s");
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strStartDate + " -0000");
			strEndDate = cmd.getOptionValue("e");
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strEndDate + " -0000");
			strLabelEndDate = cmd.getOptionValue("l");
			labelEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strLabelEndDate + " -0000");

		} catch (Exception e) {
			printHelp(options);
			return false;
		}

		return true;
	}

	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "Execute BICER to collect bug-introducing changes. On Windows, use BICER.bat instead of ./BICER";
		String footer ="\nPlease report issues at https://github.com/lifove/BICER/issues";
		formatter.printHelp( "./BICER", header, options, footer, true);
	}
}
