package ca.uwaterloo.ece.bicer.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class ChangeCollectorWithPatchSize {
	public static void main(String[] args) {
		new ChangeCollectorWithPatchSize().run(args);
	}

	private void run(String[] args) {
		String path = args[0];// System.getProperty("user.home") + "/Documents/ODP/projects/lucene";
		String gitURI = path + "/" + args[1]; //"/git";
		String strStartDate = args[2]; //"2007-09-12"; //"2010-09-16";
		String strEndDate = args[3]; //"2013-01-14"; //"2012-04-10";
		
		try {
			
			Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strStartDate + " 00:00:00 -0000");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strEndDate + " 23:59:59 -0000");
		
			Git git = Git.open( new File(gitURI) );
			Repository repo = git.getRepository();

			Iterable<RevCommit> logs = git.log()
					.call();

			System.out.println("SHA1\tpath\t#DeletedLines\t#AddedLines");
			for (RevCommit rev : logs) {
				Date commitDate = new Date(rev.getCommitTime()* 1000L);

				if(startDate.compareTo(commitDate)<=0 && commitDate.compareTo(endDate)<=0){
					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
					df.setRepository(repo);
					df.setDiffAlgorithm(Utils.diffAlgorithm);
					df.setDiffComparator(Utils.diffComparator);
					df.setDetectRenames(true);
					

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

					// Get diffs from affected files in the commit
					RevCommit preRev = rev.getParent(0);
					List<DiffEntry> diffs;
					
					// Deal with diff and get only deleted lines
					diffs = df.scan(preRev.getTree(), rev.getTree());
					
					for (DiffEntry diff : diffs) {
						
						String oldPath = diff.getOldPath();
						String newPath = diff.getNewPath();
						
						int numDeletedLines = 0;
						int numAddedLines = 0;

						// Skip test case files
						if(!newPath.endsWith(".java")) continue;
						
						// Do diff on files without comments to only consider code lines
						String prevfileSource=Utils.removeComments(Utils.fetchBlob(repo, sha1 +  "~1", oldPath));
						String fileSource=Utils.removeComments(Utils.fetchBlob(repo, sha1, newPath));	
						
						EditList editList = Utils.getEditListFromDiff(prevfileSource, fileSource);
						
						String[] arrPrevfileSource=prevfileSource.split("\n");
						for(Edit edit:editList){
							
							int beginA = edit.getBeginA();
							int endA = edit.getEndA();
							
							// count deleted lines
							for(int lineIdx = beginA; lineIdx < endA; lineIdx++){
								if(arrPrevfileSource.length<=lineIdx) break; // split("\n") ignore last empty lines. So, lineIdx can be greater the array length. Ignore this case
								numDeletedLines++;
							}
							
							// count added lines
							int beginB = edit.getBeginB();
							int endB = edit.getEndB();
							for(int lineIdx = beginB; lineIdx < endB; lineIdx++){
								if(arrPrevfileSource.length<=lineIdx) break; // split("\n") ignore last empty lines. So, lineIdx can be greater the array length. Ignore this case
								numAddedLines++;
							}
						}
						
						System.out.println(sha1 + "\t" + newPath + "\t" + numDeletedLines + "\t" + numAddedLines );
					}
					df.close();
				}
			}             
		} catch (IOException | GitAPIException e) {
			System.err.println("Repository does not exist: " + gitURI);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
	}
}
