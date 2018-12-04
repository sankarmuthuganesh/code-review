package RealTime.SubSystemEpicSplit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileCategoryFinder {

	public String subSystem="UncategorisedSubsystem";
	public String epicName="UncategorisedEpic";
	public String categoryOfFile="UnknownCategory";
	public String license="UnknownLicense";
	public String licenseGroup="UnknownLicenseGroup";

	public void getFileLGLSEC(String pathOfFile){
		List<String> detailsOfAFile=new ArrayList<>();
		Path path=Paths.get(pathOfFile);
		Iterator<Path> iteratorOfPath = path.iterator();
		while(iteratorOfPath.hasNext()){
			detailsOfAFile.add(iteratorOfPath.next().toString());
		}

		int indexOfService = 0;
		if(detailsOfAFile.size()>3){
			if(detailsOfAFile.get(6).endsWith("-biz")||detailsOfAFile.get(6).endsWith("-front")){
				//Biz Package
				if(detailsOfAFile.get(6).endsWith("-biz")){
					if(detailsOfAFile.contains("biz")){
						license=detailsOfAFile.get(detailsOfAFile.indexOf("biz")+1);
						licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("biz")-1);
					}

					if(detailsOfAFile.contains("entity")||detailsOfAFile.contains("inputfwimpl")||detailsOfAFile.contains("service")||detailsOfAFile.contains("vo")){
						if(detailsOfAFile.contains("entity")){
							categoryOfFile="entity";
							//Because some entities are placed directly inside entity folder-project repo
							//indexOfService=detailsOfAFile.indexOf("entity");
						}
						if(detailsOfAFile.contains("inputfwimpl")){
							categoryOfFile="inputfwimpl";
							indexOfService=detailsOfAFile.indexOf("inputfwimpl");
						}
						if(detailsOfAFile.contains("service")){
							categoryOfFile="service";
							indexOfService=detailsOfAFile.indexOf("service");
						}
						if(detailsOfAFile.contains("vo")){
							categoryOfFile="vo";
							
							indexOfService=detailsOfAFile.indexOf("vo");
						}
						try{
							if(indexOfService!=0){
								subSystem=detailsOfAFile.get(indexOfService+1);
								epicName=detailsOfAFile.get(indexOfService+2);	
							}
						}catch(IndexOutOfBoundsException e){

						}
					}
				}

				if(detailsOfAFile.get(6).endsWith("-front")){
					//Front Package
					if(detailsOfAFile.contains("config")){
						license=detailsOfAFile.get(detailsOfAFile.indexOf("config")+2);
					}
					if(detailsOfAFile.contains("controller")||detailsOfAFile.contains("js")||detailsOfAFile.contains("templates")){
						if(detailsOfAFile.contains("controller")){
							categoryOfFile="controller";
							indexOfService=detailsOfAFile.indexOf("controller");	
							license=detailsOfAFile.get(detailsOfAFile.indexOf("controller")+2);
							licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("controller")+1);
						}
						if(detailsOfAFile.contains("js")){
							categoryOfFile="js";
							indexOfService=detailsOfAFile.indexOf("js");	
							license=detailsOfAFile.get(detailsOfAFile.indexOf("js")+2);
							licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("js")+1);

						}
						if(detailsOfAFile.contains("templates")){
							categoryOfFile="xml";
							//Due to company-input-framework and approval folder in templates
							//indexOfService=detailsOfAFile.indexOf("templates");
							license=detailsOfAFile.get(detailsOfAFile.indexOf("templates")+2);
							//Due to company-input-framework folder in templates
							//	licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("templates")+1);
						}
						try{
							if(indexOfService!=0){
								subSystem=detailsOfAFile.get(indexOfService+3);
								epicName=detailsOfAFile.get(indexOfService+4);	
							}

						}catch(IndexOutOfBoundsException e){

						}
					}
				}	
				if(detailsOfAFile.get(6).endsWith("-dto")){
					if(detailsOfAFile.contains("dto")){
						license=detailsOfAFile.get(detailsOfAFile.indexOf("dto")+1);
						licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("dto")-1);
					}
				}
				if(detailsOfAFile.get(6).endsWith("-bizcore")){
					if(detailsOfAFile.contains("bizcore")){
						license=detailsOfAFile.get(detailsOfAFile.indexOf("bizcore")+1);
						licenseGroup=detailsOfAFile.get(detailsOfAFile.indexOf("bizcore")-1);
					}
				}
			}
		}
	}
}
