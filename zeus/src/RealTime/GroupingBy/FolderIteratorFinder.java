package RealTime.GroupingBy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class FolderIteratorFinder {
	public String getGitHome(String absolutePath){
		Path path = Paths.get(absolutePath);
		StringBuilder parentFolderoFFound=new StringBuilder();
		parentFolderoFFound.append("C:");
		Iterator<Path> iteratorOfPath = path.iterator();
		while (iteratorOfPath.hasNext()) {
			parentFolderoFFound.append(File.separator+iteratorOfPath.next().toString());
			File[] files=new File(parentFolderoFFound.toString()).listFiles();
			if(files!=null){
				for(File file: files){
					if(file.isDirectory()&&file.getName().equals(".git")){
						return parentFolderoFFound.toString()+File.separator;
					}
				}
			}
		}
		return null;
	}
}
