package RealTime.Author;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;



public class DetailsUsingCommits{


	public static List<AuthorIdent> getCommitDetails(String absolutePath) {

		/*
		 * ---Manipulation of Paths----
		 */
		//Assumption - Cloned in to %temp% folder
		int indexUntilClonedFolder=StringUtils.ordinalIndexOf(absolutePath, "\\", 7);
		String pathForLog = absolutePath.substring(indexUntilClonedFolder+1).replaceAll("\\", "/");
		String gitPath = absolutePath.substring(0,indexUntilClonedFolder)+"\\.git";

		/*
		 * ---Link the Local Git Directory---
		 */
		FileRepositoryBuilder fileRepoBuilder = new FileRepositoryBuilder();
		Repository repo = null;
		try {
			repo = fileRepoBuilder.setGitDir(new File(gitPath)).readEnvironment().findGitDir().build();
		} catch (IOException e) {
			//***Error Reading Repository
		}
		Git gitUtils = new Git(repo); 

		/*
		 * ---Getting the Log History of the Particular File---
		 */
		Iterable<RevCommit> commitLogs = null;
		try {
			commitLogs = gitUtils.log().addPath(pathForLog).call();
		} catch (GitAPIException e) {
		// ***Error getting logs of Commit
		}


		List<AuthorIdent> authorsOfCommit=new ArrayList<>();
		commitLogs.forEach(commitLog ->{
			AuthorIdent authorOfCommit=new AuthorIdent();
			authorOfCommit.setNameOfAuthor(commitLog.getAuthorIdent().getName());
			authorOfCommit.setEmailAddress(commitLog.getAuthorIdent().getEmailAddress());
			authorOfCommit.setDateOfCommit(commitLog.getAuthorIdent().getWhen());
		});

		return authorsOfCommit;



	}


}