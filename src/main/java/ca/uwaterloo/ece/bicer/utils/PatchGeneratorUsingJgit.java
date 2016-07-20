package ca.uwaterloo.ece.bicer.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class PatchGeneratorUsingJgit {
	
	String diffAlg = "";
	
	private Git git;
	private Repository repo;


	public static void main(String[] args) {
		new PatchGeneratorUsingJgit().run(args);
	}

	private void run(String[] args) {

		String path = args[0];
		String gitURI = args[1];
		String strStartDate = args[2];
		String strEndDate = args[3];
		
		
		try {

			git = Git.open( new File(path + "/" + gitURI) );
			repo = git.getRepository();

			Iterable<RevCommit> logs = git.log()
					.call();
			
			Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strStartDate + " 00:00:00 -0000");
			Date endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(strEndDate + " 23:59:59 -0000");
			
			
			System.out.println(strStartDate);
			System.out.println(strEndDate);
			

			//SimpleDateFormat ft =  new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss zzz");
			int i=0;
			for (RevCommit rev : logs) {
				Date commitDate = new Date(rev.getCommitTime()* 1000L);
				
				if(startDate.compareTo(commitDate)<=0 && commitDate.compareTo(endDate)<=0){
					
					// generate patch
					String sha1 = rev.getName();
					System.out.println((++i) + ": " + sha1);
					
					
					FileOutputStream fos = new FileOutputStream(path + "/patches/" + sha1);
					
					DataOutputStream dos=new DataOutputStream(fos);
					
					// do diff
					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
					df.setRepository(repo);
					df.setDiffAlgorithm(Utils.diffAlgorithm);
					df.setDiffComparator(Utils.diffComparator);
					df.setDetectRenames(true);
					List<DiffEntry> diffs;
					
					String header = rev.getFullMessage();
					
					dos.write((sha1 + "\n").getBytes());
					dos.write((Utils.getStringDateTimeFromCommitTime(rev.getCommitTime()) + "\n\n").getBytes());
					
					dos.write(header.getBytes());
					
					try {
						
						// get a list of files in the commit
						RevCommit parent = rev.getParent(0);
						if(parent==null){
							System.err.println("WARNING: Parent commit does not exist: " + rev.name() );
							continue;
						}

						// do diff
						diffs = df.scan(parent.getTree(), rev.getTree());
						
						String oldSha1 = parent.getName();
						
						for (DiffEntry diff : diffs) {
							
							String oldPath = diff.getOldPath();
							String newPath = diff.getNewPath();

							// ignore when no previous revision of a file, Test files, and non-java files.
							if(oldPath.equals("/dev/null") || newPath.indexOf("Test")>=0  || newPath.indexOf("/test")>=0 || !newPath.endsWith(".java")) continue;
							
							
							
							//df.format(diff);	
							String sourceAWOComments = Utils.removeComments(Utils.fetchBlob(repo, oldSha1, oldPath));
							String sourceBWOComments = Utils.removeComments(Utils.fetchBlob(repo, sha1, newPath));
							
										
							DiffAlgorithm diffAlgorithm = DiffAlgorithm.getAlgorithm(DiffAlgorithm.SupportedAlgorithm.MYERS);
							RawTextComparator diffComparator = RawTextComparator.WS_IGNORE_ALL;
							
							DiffFormatter df2 = new DiffFormatter(dos);
							df2.setDiffAlgorithm(diffAlgorithm);
							df2.setDiffComparator(diffComparator);
							
							df2.format(Utils.getEditListFromDiff(sourceAWOComments, sourceBWOComments), new RawText(sourceAWOComments.getBytes()), new RawText(sourceBWOComments.getBytes()));

							df2.close();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
					df.close();
					dos.close();
					fos.close();
				}
					
			}             

		} catch (IOException | GitAPIException | ParseException e) {
			e.printStackTrace();
		}

	}

}
