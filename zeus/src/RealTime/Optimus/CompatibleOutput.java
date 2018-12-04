package RealTime.Optimus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import RealTime.Entity.BugDetails;

public class CompatibleOutput {
	
	public static void getOptimusOutput(Map<String,Map<MultiKey, Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>>>> groupedOutput,List<String> groupedOrder){

		// The Current User Directory Where the User is Launching the Application. Typically the current directory from where the JVM is invoked.
		//***********Exceptional Case-If user creates a shortcut anywhere, and executes the application, that directory is considered.**************
		String userDirectory=System.getProperty("user.dir");

		groupedOutput.entrySet().stream().forEach(repository ->{
			//Make A Directory for Repository
			String repositoryDirectory = makeDirectory(userDirectory,repository.getKey());

			repository.getValue().entrySet().stream().forEach(branch ->{
				//Make A Directory for Branch
				String branchDirectory = makeDirectory(repositoryDirectory,branch.getKey().getKey(0).toString());

				//Grouped Output For A Branch
				Map<String, Map<String, Map<String, Map<String, Map<String, Map<String, List<BugDetails>>>>>>> groupedForABranch = branch.getValue();

				groupedForABranch.entrySet().stream().forEach(groupOne ->{
					//Typically the natureOfFile
					//String headerOfGroupOne=groupedOrder.get(0);
					String groupOneDirectory = makeDirectory(branchDirectory, groupOne.getKey());

					groupOne.getValue().entrySet().stream().forEach(groupTwo ->{
						//String headerOfGroupTwo=groupedOrder.get(1);
						//Create a ExcelFile
						XSSFWorkbook workBook = new XSSFWorkbook();
						
						groupTwo.getValue().entrySet().stream().forEach(groupThree ->{
							AtomicInteger rowCount=new AtomicInteger(-1);
							AtomicInteger cellCount=new AtomicInteger(0);
							//Write a Sheet in the above Excel File
							XSSFSheet sheet=workBook.createSheet(groupThree.getKey());
							sheet.setColumnWidth(0,70*250);
							groupThree.getValue().entrySet().stream().forEach(groupFour ->{
								//epic
								CellStyle epicStyle = makeACellStyle(workBook,20);
								writeInRowAndCell(sheet,rowCount.incrementAndGet(),cellCount.get(),groupFour.getKey(),epicStyle);
								
								groupFour.getValue().entrySet().stream().forEach(groupFive ->{
									CellStyle severityStyle = makeACellStyle(workBook,16);
									writeInRowAndCell(sheet,rowCount.incrementAndGet(),cellCount.get(),groupFive.getKey(),severityStyle);
									
									groupFive.getValue().entrySet().stream().forEach(groupSix ->{
										CellStyle categoryStyle = makeACellStyle(workBook,12);
										writeInRowAndCell(sheet,rowCount.incrementAndGet(),cellCount.get(),groupSix.getKey(),categoryStyle);
										
										List<BugDetails> valuesList=groupSix.getValue();
										valuesList.stream().forEach(bug -> {
											CellStyle fileLinkStyle = makeACellStyle(workBook,10);
											createAHyperLink(sheet,rowCount.incrementAndGet(),cellCount.get(),bug.getHttpPathOfFile()+"#L"+bug.getLineNumber()
													,bug.getFileName()+" in LineNo:"+bug.getLineNumber(),fileLinkStyle);
										});
									});
								});
							});
						});
						createAnExcel(workBook,groupOneDirectory,groupTwo.getKey());
					});
				});
			});
		});
	}

	/*
	 * Creates a Directory in the specified Path with the specifited name
	 * @param path where directory is to be created
	 * @param directory name
	 * @return the absolute path where the new directory is created
	 */
	private static String makeDirectory(String path,String directory){
		File notepadDir=new File(path+File.separator+directory);
		if(notepadDir.exists()){
			notepadDir.delete();	
		}
		notepadDir.mkdir();
		return notepadDir.getAbsolutePath();
	}
	private static void createAnExcel(XSSFWorkbook workBook,String groupOneDirectory, String fileName) {

		try {
			FileOutputStream outputStream = new FileOutputStream(groupOneDirectory+"\\"+fileName+".xlsx");
			workBook.write(outputStream);
		} catch (IOException e) {
			// File is not Found or Input Output Exception Occurred.
		}

		//Desktop desktop=java.awt.Desktop.getDesktop();
		//desktop.open(new File(fileName+".xlsx"));
	}

	private static void writeInRowAndCell(XSSFSheet sheet,int rowCount,int cellCount,String value,CellStyle style){
		//Writing Value to a Cell of A Specific Style.
		Row headerRow=sheet.createRow(rowCount);
		Cell cellTitle=headerRow.createCell(cellCount);
		cellTitle.setCellValue(value);
		cellTitle.setCellStyle(style);
	}
	
	private static CellStyle makeACellStyle(XSSFWorkbook workBook,int size){	
		//Making a CellStyle of Specific Font Size.
		CellStyle cellStyle=workBook.createCellStyle();
		Font font=workBook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short)size);
		cellStyle.setFont(font);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		return cellStyle;
		}

//	public static void writeInNotepadFile(String fileName, List<BugDetails> listOfErrorsOfThatFile, Path repoAndBranchDir) throws IOException{
//
//		File errorFile=new File(repoAndBranchDir+"\\BugsOfFilesInNotepad\\"+bugType+"\\"+fileName+".txt");
//		if(errorFile.exists()){
//			errorFile.delete();
//		}
//		errorFile.createNewFile();
//		FileWriter fw= new FileWriter(repoAndBranchDir+"\\BugsOfFilesInNotepad\\"+bugType+"\\"+fileName+".txt");
//		BufferedWriter writ=new BufferedWriter(fw);
//		for(String lines:listOfErrorsOfThatFile){
//			writ.write(lines);
//			writ.newLine();
//		}
//		if(fw!=null||writ!=null){
//			writ.close();
//			fw.close();
//		}
//
//	}
	
	private static void createAHyperLink(XSSFSheet sheet,int rowCount,int cellCount,String url, String fileName, CellStyle fileLinkStyle){
		Row headerRow=sheet.createRow(rowCount);
		Cell hyperlinkCell=headerRow.createCell(cellCount);
		hyperlinkCell.setCellStyle(fileLinkStyle);
		Workbook contentsBook=new XSSFWorkbook();
		CreationHelper createHelper=contentsBook.getCreationHelper();
		Hyperlink link=createHelper.createHyperlink(HyperlinkType.URL);
		link.setAddress(url);
		hyperlinkCell.setHyperlink(link);
		hyperlinkCell.setCellValue(fileName);
	}
	
}