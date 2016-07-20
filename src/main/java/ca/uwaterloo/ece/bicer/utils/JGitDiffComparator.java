package ca.uwaterloo.ece.bicer.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class JGitDiffComparator {
	private Git git;
	private Repository repo;
	
	int numChanges = 0;
	int numDifferentChanges = 0;
	
	int numDeletedLinesMyers = 0;
	int numMyersDeletedLinesNotInHisgoram = 0;
	int numDeletedLinesHistogram = 0;
	int numHistogramDeletedLinesNotInMyers = 0;
	
	int numAddedLinesMyers = 0;
	int numMyersAddedLinesNotInHisgoram = 0;
	int numAddedLinesHistogram = 0;
	int numHistogramAddedLinesNotInMyers = 0;
	
	public static void main(String[] args) {
		new JGitDiffComparator().run(args);
	}

	private void run(String[] args) {
		String path = System.getProperty("user.home") + "/Documents/ODP/projects/jackrabbit";
		String gitURI = path + "/git";
		String strStartDate = "2007-09-12"; //"2010-09-16";
		String strEndDate = "2013-01-14"; //"2012-04-10";
		
		// (1) load BugsIDs
		ArrayList<String> bugIDs = Utils.getLines(path + "/jackrabbit_bug_reports.txt", false);
		String bugIDPreFix = bugIDs.get(0).split("-")[0] + "-";
		
		try {

			git = Git.open( new File(gitURI) );
			repo = git.getRepository();

			Iterable<RevCommit> logs = git.log()
					.call();
			
			Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strStartDate + " 00:00:00 -0000");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strEndDate + " 23:59:59 -0000");
			
			
			System.out.println(strStartDate);
			System.out.println(strEndDate);
			
			
			ArrayList<String> sha1Redundant = new ArrayList<String>();
			//SimpleDateFormat ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss zzz");
			int i=0;
			for (RevCommit rev : logs) {
				
				String message = rev.getFullMessage();
				String sha1 = rev.getName();
				// Create matcher on file
				Pattern pattern = Pattern.compile(bugIDPreFix + "\\d+");
				Matcher matcher = pattern.matcher(message);

				int j=0;
				while(matcher.find()){
					if(bugIDs.contains(matcher.group(0))){
				
						Date commitDate = new Date(rev.getCommitTime()* 1000L);
						
						if(startDate.compareTo(commitDate)<=0 && commitDate.compareTo(endDate)<=0){
							
							// generate patch
							
							System.out.println(sha1);
								
							// do diff
							DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
							df.setRepository(repo);
							df.setDiffAlgorithm(Utils.diffAlgorithm);
							df.setDiffComparator(Utils.diffComparator);
							df.setDetectRenames(true);
							List<DiffEntry> diffs;
							
							String header = rev.getFullMessage();
							
							try {
								
								// get a list of files in the commit
								RevCommit parent = rev.getParent(0);
								if(parent==null){
									System.err.println("WARNING: Parent commit does not exist: " + rev.name() );
									continue;
								}
		
								j++;
								// do diff
								diffs = df.scan(parent.getTree(), rev.getTree());
								
								String oldSha1 = parent.getName();
								
								for (DiffEntry diff : diffs) {
									
									String oldPath = diff.getOldPath();
									String newPath = diff.getNewPath();
		
									// ignore when no previous revision of a file, Test files, and non-java files.
									if(oldPath.equals("/dev/null") || newPath.indexOf("Test")>=0  || newPath.indexOf("/test")>=0 || !newPath.endsWith(".java")) continue;
									
									
									
									//df.format(diff);	
									String sourceA = Utils.fetchBlob(repo, oldSha1, oldPath);
									String sourceB = Utils.fetchBlob(repo, sha1, newPath);
									
									String sourceAWOComments = Utils.removeComments(Utils.fetchBlob(repo, oldSha1, oldPath));
									String sourceBWOComments = Utils.removeComments(Utils.fetchBlob(repo, sha1, newPath));
									
												
									DiffAlgorithm myers = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
									DiffAlgorithm histogram = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.HISTOGRAM);
									RawTextComparator diffComparator = RawTextComparator.WS_IGNORE_ALL;
									
									
									EditList myersEditList = myers.diff(diffComparator, new RawText(sourceAWOComments.getBytes()), new RawText(sourceBWOComments.getBytes()));
									EditList hitgogramEditList = histogram.diff(diffComparator, new RawText(sourceAWOComments.getBytes()), new RawText(sourceBWOComments.getBytes()));
									//EditList hitgogramEditList = myers.diff(diffComparator, new RawText(sourceA.getBytes()), new RawText(sourceB.getBytes()));
									
									numChanges++;
									
									if(!compareAndIsSame(myersEditList,hitgogramEditList))
										numDifferentChanges++;
									
									System.out.println(numChanges + " " + 
												numDifferentChanges + " " + 
												numDeletedLinesMyers + " " + 
												numMyersDeletedLinesNotInHisgoram + " " + 
												numDeletedLinesHistogram + " " + 
												numHistogramDeletedLinesNotInMyers + " " +
												numAddedLinesMyers + " " + 
												numMyersAddedLinesNotInHisgoram + " " + 
												numAddedLinesHistogram + " " + 
												numHistogramAddedLinesNotInMyers);
								}
		
							} catch (IOException e) {
								e.printStackTrace();
							}
							df.close();
							break;
						}
					}
				}
				if(j>=2)
					sha1Redundant.add(sha1);	
			}
			
			for(String rSha1:sha1Redundant){
				System.out.println(rSha1);
			}

		} catch (IOException | GitAPIException | ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean compareAndIsSame(EditList myersEditList, EditList histogramEditList) {
		boolean isSame = true;
		
		if(myersEditList.size()!=histogramEditList.size())
			isSame = false;
		
		ArrayList<Integer> idxOfDeletedLinesMyers = new ArrayList<Integer>();
		ArrayList<Integer> idxOfAddedLinesMyers = new ArrayList<Integer>();
		for(Edit edit: myersEditList){
			if(edit.getType().equals(Edit.Type.REPLACE)||edit.getType().equals(Edit.Type.DELETE)){
				for(int i=edit.getBeginA();i<edit.getEndA();i++){
					idxOfDeletedLinesMyers.add(i);
				}
			}
			
			if(edit.getType().equals(Edit.Type.REPLACE)||edit.getType().equals(Edit.Type.INSERT)){
				for(int i=edit.getBeginB();i<edit.getEndB();i++){
					idxOfAddedLinesMyers.add(i);
				}
			}
		}
		numDeletedLinesMyers += idxOfDeletedLinesMyers.size();
		numAddedLinesMyers += idxOfAddedLinesMyers.size();
		
		ArrayList<Integer> idxOfDeletedLinesHistogram = new ArrayList<Integer>();
		ArrayList<Integer> idxOfAddedLinesHistogram = new ArrayList<Integer>();
		for(Edit edit: histogramEditList){
			if(edit.getType().equals(Edit.Type.REPLACE)||edit.getType().equals(Edit.Type.DELETE)){
				for(int i=edit.getBeginA();i<edit.getEndA();i++){
					idxOfDeletedLinesHistogram.add(i);
				}
			}
			
			if(edit.getType().equals(Edit.Type.REPLACE)||edit.getType().equals(Edit.Type.INSERT)){
				for(int i=edit.getBeginB();i<edit.getEndB();i++){
					idxOfAddedLinesHistogram.add(i);
				}
			}
		}
		numDeletedLinesHistogram += idxOfDeletedLinesHistogram.size();
		numAddedLinesHistogram += idxOfAddedLinesHistogram.size();
		
		// deleted lines
		for(int idxMyers:idxOfDeletedLinesMyers){
			if(!idxOfDeletedLinesHistogram.contains(idxMyers)){
				numMyersDeletedLinesNotInHisgoram++;
				isSame = false;
			}
		}
		
		for(int idxHistogram:idxOfDeletedLinesHistogram){
			if(!idxOfDeletedLinesMyers.contains(idxHistogram)){
				numHistogramDeletedLinesNotInMyers++;
				isSame = false;
			}
		}
		
		// added lines
		for(int idxMyers:idxOfAddedLinesMyers){
			if(!idxOfAddedLinesHistogram.contains(idxMyers)){
				numMyersAddedLinesNotInHisgoram++;
				isSame = false;
			}
		}
		
		for(int idxHistogram:idxOfAddedLinesHistogram){
			if(!idxOfAddedLinesMyers.contains(idxHistogram)){
				numHistogramAddedLinesNotInMyers++;
				isSame = false;
			}
		}	
		
		return isSame;
	}

}
