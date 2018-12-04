package RealTime.Author;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Blame {
	public static void main(String args[]) throws GitAPIException{
		FileRepositoryBuilder fileRepoBuilder = new FileRepositoryBuilder();
		Repository repo = null;
		try {
			repo = fileRepoBuilder.setGitDir(new File("C:\\Users\\sankraja\\AppData\\Local\\Temp\\river-1709-476400270321782671\\.git")).readEnvironment().findGitDir().build();
		} catch (IOException e) {
			//***Error Reading Repository
		}
		Git gitUtils = new Git(repo); 
		
		String filePath="/hue-scm-project-biz/src/main/java/com/worksap/company/hue/scm/biz/project/service/projectmonitor/projectmonitor/ProjectMonitorServiceImpl.java";
		String absolutePath="C:\\Users\\sankraja\\AppData\\Local\\Temp\\river-1709-476400270321782671\\hue-scm-project-biz\\src\\main\\java\\com\\worksap\\company\\hue\\scm\\biz\\project\\service\\projectmonitor\\projectmonitor\\ProjectMonitorServiceImpl.java";
		
		final BlameResult result=gitUtils.blame().setFilePath(filePath).call();
		final RawText rawText=result.getResultContents();
		for(int i=0;i<rawText.size();i++){
			final PersonIdent sourceAuthor=result.getSourceAuthor(i);
			final RevCommit sourceCommit=result.getSourceCommit(i);
			System.out.println(sourceAuthor.getName()+(sourceCommit!=null?"/"+sourceCommit.getCommitTime()+"/"+sourceCommit.getName():""+": "+rawText.getString(i)));
		}
		
		
	}
}
