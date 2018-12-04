package RealTime.Optimus.GravityConversion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import RealTime.Author.Blames;
import RealTime.Entity.ActualErrorCollectionDetailed;
import RealTime.Entity.ActualJavaDetailed;
import RealTime.Entity.ActualJavaScriptDetailed;
import RealTime.Entity.ActualXMLDetailed;
import RealTime.GitAccess.Progress;

import com.github.javaparser.ParseException;

public class OptimusOutputMoreCategoriezed {
    public static void getOptimusOutput(Map<String, Map<String, ActualErrorCollectionDetailed>> outputForOptimusExcel,
            Progress gitCall)
            throws FileNotFoundException, IOException {

        outputForOptimusExcel
                .entrySet()
                .stream()
                .forEach(
                        repo -> {

                            repo.getValue()
                                    .entrySet()
                                    .stream()
                                    .forEach(
                                            branch -> {
                                                Path repoAndBranchDir = Paths.get(gitCall.getGravityStoragaePath()
                                                        + File.separator
                                                        + "GravityOutput-"
                                                        + "sankraja"
                                                        + File.separator
                                                        + "GravityOutput"
                                                        + File.separator
                                                        + "OptimusCategorizedOutput"
                                                        + File.separator
                                                        + repo.getKey()
                                                        + "\\"
                                                        + branch.getKey());
                                                try {
                                                    Files.createDirectories(repoAndBranchDir);
                                                } catch (Exception e1) {
                                                }
                                                File notepadDir = new File(repoAndBranchDir
                                                        + "\\BugsOfFilesInNotepad\\");
                                                if (notepadDir.exists()) {
                                                    notepadDir.delete();
                                                }
                                                notepadDir.mkdir();
                                                File notepadDirError = new File(repoAndBranchDir
                                                        + "\\BugsOfFilesInNotepad\\Errors");
                                                if (notepadDirError.exists()) {
                                                    notepadDirError.delete();
                                                }
                                                notepadDirError.mkdir();
                                                File notepadDirWarning = new File(repoAndBranchDir
                                                        + "\\BugsOfFilesInNotepad\\Warnings");
                                                if (notepadDirWarning.exists()) {
                                                    notepadDirWarning.delete();
                                                }
                                                notepadDirWarning.mkdir();

                                                File notepadDirA = new File(repoAndBranchDir + "\\Authors\\");
                                                if (notepadDirA.exists()) {
                                                    notepadDirA.delete();
                                                }
                                                notepadDirA.mkdir();
                                                File notepadDirErrorA = new File(repoAndBranchDir + "\\Authors\\Errors");
                                                if (notepadDirErrorA.exists()) {
                                                    notepadDirErrorA.delete();
                                                }
                                                notepadDirErrorA.mkdir();
                                                File notepadDirWarningA = new File(repoAndBranchDir
                                                        + "\\Authors\\Warnings");
                                                if (notepadDirWarningA.exists()) {
                                                    notepadDirWarningA.delete();
                                                }
                                                notepadDirWarningA.mkdir();

                                                ActualErrorCollectionDetailed branchErrorsAndWarnings = branch
                                                        .getValue();

                                                ActualJavaDetailed javaBugs = branchErrorsAndWarnings
                                                        .getTotalCategorisedCollectionJava();
                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaCategories = javaBugs
                                                        .getErrorCategoriesAndItsFilesAndDetails();

                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaCategoriesWarnings = javaBugs
                                                        .getWarningCategoriesAndItsFilesAndDetails();
                                                try {
                                                    XSSFWorkbook javaWorkbook = new XSSFWorkbook();
                                                    makeCategoriesAndFilesList(0, javaCategories, javaWorkbook,
                                                            repoAndBranchDir, "Errors");
                                                    makeCategoriesAndFilesList(4, javaCategoriesWarnings, javaWorkbook,
                                                            repoAndBranchDir, "Warnings");
                                                    writeInToExcelUsingWorkbook(javaWorkbook, repoAndBranchDir, "Java");
                                                } catch (Exception e) {
                                                }

                                                ActualJavaScriptDetailed javaScriptBugs = branchErrorsAndWarnings
                                                        .getTotalCategorisedCollectionJS();
                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaScriptCategories = javaScriptBugs
                                                        .getErrorCategoriesAndItsFilesAndDetails();
                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> javaScriptCategoriesWarnings = javaScriptBugs
                                                        .getWarningCategoriesAndItsFilesAndDetails();

                                                try {
                                                    XSSFWorkbook jsWorkbook = new XSSFWorkbook();
                                                    makeCategoriesAndFilesList(0, javaScriptCategories, jsWorkbook,
                                                            repoAndBranchDir, "Errors");
                                                    makeCategoriesAndFilesList(4, javaScriptCategoriesWarnings,
                                                            jsWorkbook, repoAndBranchDir, "Warnings");
                                                    writeInToExcelUsingWorkbook(jsWorkbook, repoAndBranchDir,
                                                            "JavaScript");
                                                } catch (Exception e) {
                                                }

                                                ActualXMLDetailed xmlBugs = branchErrorsAndWarnings
                                                        .getTotalCategorisedCollectionXML();
                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> xmlCategories = xmlBugs
                                                        .getErrorCategoriesAndItsFilesAndDetails();
                                                Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> xmlCategoriesWarnings = xmlBugs
                                                        .getWarningCategoriesAndItsFilesAndDetails();
                                                try {
                                                    XSSFWorkbook xmlWorkbook = new XSSFWorkbook();
                                                    makeCategoriesAndFilesList(0, xmlCategories, xmlWorkbook,
                                                            repoAndBranchDir, "Errors");
                                                    makeCategoriesAndFilesList(4, xmlCategoriesWarnings, xmlWorkbook,
                                                            repoAndBranchDir, "Warnings");
                                                    writeInToExcelUsingWorkbook(xmlWorkbook, repoAndBranchDir, "XML");
                                                } catch (Exception e) {
                                                }
                                            });
                        });
    }

    public static void writeInNotepadFile(String fileName, List<String> listOfErrorsOfThatFile, Path repoAndBranchDir,
            String bugType) throws IOException {

        File errorFile = new File(repoAndBranchDir + "\\BugsOfFilesInNotepad\\" + bugType + "\\" + fileName + ".txt");
        if (errorFile.exists()) {
            errorFile.delete();
        }
        errorFile.createNewFile();
        FileWriter fw = new FileWriter(repoAndBranchDir
                + "\\BugsOfFilesInNotepad\\"
                + bugType
                + "\\"
                + fileName
                + ".txt");
        BufferedWriter writ = new BufferedWriter(fw);
        for (String lines : listOfErrorsOfThatFile) {
            writ.write(lines);
            writ.newLine();
        }
        if (fw != null || writ != null) {
            writ.close();
            fw.close();
        }
    }

    private static void makeCategoriesAndFilesList(int cellCount,
            Map<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> categories, XSSFWorkbook workbook,
            Path repoAndBranchDir, String bugType) throws IOException, ParseException {
        AtomicInteger rowCount = new AtomicInteger(-1);
        for (Entry<String, Map<String, Map<String, Map<MultiKey, List<String>>>>> fromSubsys : categories.entrySet()) {
            // Creating A Sheet File for TypeOfFile
            XSSFSheet sheet;
            String subsystem = fromSubsys.getKey();
            if (subsystem.length() > 32) {
                subsystem = subsystem.substring(0, 28);
            }
            sheet = workbook.getSheet(subsystem);

            if (sheet == null) {
                sheet = workbook.createSheet(subsystem);
            }

            // if(bugType.equals("error")){
            // sheet=workbook.createSheet(fromSubsys.getKey());
            // }
            // else {
            // sheet=workbook.getSheet(fromSubsys.getKey());
            // }

            // Creating A Font Style for ErrorList
            CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);
            font.setColor(HSSFColor.RED.index);
            font.setFontHeightInPoints((short)32);
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Creating A Font Style for Warning List
            CellStyle cellStyleWarning = sheet.getWorkbook().createCellStyle();
            Font fontWarning = sheet.getWorkbook().createFont();
            fontWarning.setBold(true);
            fontWarning.setColor(HSSFColor.DARK_RED.index);
            fontWarning.setFontHeightInPoints((short)32);
            cellStyleWarning.setFont(fontWarning);
            cellStyleWarning.setAlignment(HorizontalAlignment.CENTER);
            cellStyleWarning.setVerticalAlignment(VerticalAlignment.CENTER);

            // ALL Bugs and Their Explantion
            CellStyle cellStyleForAllBugs = sheet.getWorkbook().createCellStyle();
            Font fontForAllBugs = sheet.getWorkbook().createFont();
            fontForAllBugs.setBold(true);
            fontForAllBugs.setFontHeightInPoints((short)16);
            fontForAllBugs.setColor(HSSFColor.BROWN.index);
            cellStyleForAllBugs.setFont(fontForAllBugs);
            cellStyleForAllBugs.setWrapText(true);
            cellStyleForAllBugs.setAlignment(HorizontalAlignment.CENTER);

            // Creating Rows and Cells
            Row headerRow;
            headerRow = sheet.getRow(rowCount.incrementAndGet());
            if (headerRow == null) {
                headerRow = sheet.createRow(rowCount.get());
            }
            if (bugType.equals("Errors")) {
                sheet.setColumnWidth(0, 150 * 93);
                Cell cellTitle = headerRow.createCell(cellCount);
                cellTitle.setCellStyle(cellStyle);
                cellTitle.setCellValue("Error List");

                Cell totalBugs = headerRow.createCell(cellCount + 1);
                totalBugs.setCellValue("All Bugs of A File And Their Explanation");
                totalBugs.setCellStyle(cellStyleForAllBugs);
                sheet.setColumnWidth(1, 70 * 93);

                Cell cellAuthor = headerRow.createCell(cellCount + 2);
                cellAuthor.setCellValue("Author");
                cellAuthor.setCellStyle(cellStyleForAllBugs);
                sheet.setColumnWidth(2, 70 * 93);
            } else {
                Cell forWarningHeader = headerRow.createCell(cellCount);
                forWarningHeader.setCellValue("Warning List");
                forWarningHeader.setCellStyle(cellStyleWarning);
                sheet.setColumnWidth(4, 150 * 93);

                Cell tbWarnings = headerRow.createCell(cellCount + 1);
                tbWarnings.setCellValue("All Bugs of A File And Their Explanation");
                tbWarnings.setCellStyle(cellStyleForAllBugs);
                sheet.setColumnWidth(5, 70 * 93);

                Cell cellAuthor = headerRow.createCell(cellCount + 2);
                cellAuthor.setCellValue("Author");
                cellAuthor.setCellStyle(cellStyleForAllBugs);
                sheet.setColumnWidth(6, 70 * 93);
            }

            CellStyle styleColor = sheet.getWorkbook().createCellStyle();
            styleColor.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            styleColor.setFillPattern(FillPatternType.BIG_SPOTS);
            // styleColor.setFillPattern(CellStyle.);
            headerRow.setRowStyle(styleColor);
            sheet.createFreezePane(0, 1);

            Map<String, Map<String, Map<MultiKey, List<String>>>> subSystemDetails = fromSubsys.getValue();

            for (Entry<String, Map<String, Map<MultiKey, List<String>>>> fromEpic : subSystemDetails.entrySet()) {
                // Creating A Font Style for Epic
                CellStyle cellStyleEpic = sheet.getWorkbook().createCellStyle();
                Font fontEpic = sheet.getWorkbook().createFont();
                fontEpic.setBold(true);
                fontEpic.setFontHeightInPoints((short)15);
                cellStyleEpic.setFont(fontEpic);

                rowCount.incrementAndGet();
                Row epicRow;
                epicRow = sheet.getRow(rowCount.incrementAndGet());
                if (epicRow == null) {
                    epicRow = sheet.createRow(rowCount.get());
                }
                Cell epicTitle = epicRow.createCell(cellCount);
                epicTitle.setCellStyle(cellStyleEpic);
                epicTitle.setCellValue(fromEpic.getKey().toUpperCase());
                for (Entry<String, Map<MultiKey, List<String>>> fromCategory : fromEpic.getValue().entrySet()) {
                    // Creating A Font Style for Category
                    CellStyle cellStyleCatgory = sheet.getWorkbook().createCellStyle();
                    Font fontCategory = sheet.getWorkbook().createFont();
                    fontCategory.setBold(true);
                    fontCategory.setItalic(true);
                    fontCategory.setFontHeightInPoints((short)13);
                    cellStyleCatgory.setFont(fontCategory);
                    cellStyleCatgory.setAlignment(HorizontalAlignment.CENTER);

                    Row categoryRow;
                    categoryRow = sheet.getRow(rowCount.incrementAndGet());
                    if (categoryRow == null) {
                        categoryRow = sheet.createRow(rowCount.get());
                    }
                    Cell categoryTitle = categoryRow.createCell(cellCount);
                    categoryTitle.setCellStyle(cellStyleCatgory);
                    categoryTitle.setCellValue(fromCategory.getKey());
                    for (Entry<MultiKey, List<String>> fromFile : fromCategory.getValue().entrySet()) {
                        // Writing Files in a Row
                        Row rowsForFiles;
                        rowsForFiles = sheet.getRow(rowCount.incrementAndGet());
                        if (rowsForFiles == null) {
                            rowsForFiles = sheet.createRow(rowCount.get());
                        }
                        Cell cellsForFiles = rowsForFiles.createCell(cellCount);
                        // cellsForFiles.setCellValue(fromFile.getKey());
                        Workbook contentsBook = new XSSFWorkbook();
                        CreationHelper createHelper = contentsBook.getCreationHelper();
                        Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
                        link.setAddress(fromFile.getKey().getKey(0).toString());
                        cellsForFiles.setHyperlink(link);
                        cellsForFiles.setCellValue(fromFile.getKey().getKey(0).toString()
                                .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1));
                        // Link for Notepad
                        Cell cellForItsTotalBugs = rowsForFiles.createCell(cellCount + 1);
                        writeInNotepadFile(
                                fromFile.getKey().getKey(0).toString()
                                        .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1),
                                fromFile.getValue(), repoAndBranchDir, bugType);
                        cellForItsTotalBugs.setCellValue("TB");
                        Hyperlink linkForNotepad = createHelper.createHyperlink(HyperlinkType.FILE);
                        linkForNotepad.setAddress("BugsOfFilesInNotepad/"
                                + bugType
                                + "/"
                                + fromFile.getKey().getKey(0).toString()
                                        .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1)
                                + ".txt");
                        cellForItsTotalBugs.setHyperlink(linkForNotepad);
                        // Author Manipulation
                        // String lineRange;
                        // try{
                        // lineRange = (fromFile.getKey().getKey(0).toString()).substring(fromFile.getKey().getKey(0)
                        // .toString().lastIndexOf("L") + 1);
                        // }catch(Exception e){
                        // System.out.println(e);
                        // lineRange=StringUtils.EMPTY;
                        // }
                        String fileName = fromFile.getKey().getKey(0).toString()
                                .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1);
                        String lineNumber = fileName.substring(fileName.lastIndexOf("#") + 2);

                        Map<String, List<String>> exactAuthor = new Blames().getExactAuthor(lineNumber, fromFile
                                .getKey().getKey(1).toString());

                        // Commented because details is going to be got from blame
                        // new ExactAuthorManipulation().getExactAuthor( fromFile.getKey().getKey(1).toString());

                        //
                        // Cell authorCell=rowsForFiles.createCell(cellCount+2);
                        // authorCell.setCellStyle(cellStyleCatgory);
                        // String author;
                        // if(fromFile.getKey().getKey(1).toString().endsWith(".java")){
                        // JavaAuthor java=new JavaAuthor();
                        // java.getJavaAuthor(fromFile.getKey().getKey(1).toString());
                        // author=java.authorOfFile;
                        // }
                        //
                        // else if(fromFile.getKey().getKey(1).toString().endsWith(".js")){
                        // JsAuthor js=new JsAuthor();
                        // author=js.getJsAuthor(fromFile.getKey().getKey(1).toString());
                        // }
                        // else{
                        // author=StringUtils.EMPTY;
                        // }

                        // Link For Author
                        Cell authorCell = rowsForFiles.createCell(cellCount + 2);
                        writeInNotepadFileAuthor(
                                fromFile.getKey().getKey(0).toString()
                                        .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1),
                                exactAuthor, repoAndBranchDir, bugType);
                        authorCell.setCellValue("Authors");
                        CreationHelper createHelperAuthor = contentsBook.getCreationHelper();
                        Hyperlink linkForAuthorNotepad = createHelperAuthor
                                .createHyperlink(HyperlinkType.FILE);
                        linkForAuthorNotepad.setAddress("Authors/"
                                + bugType
                                + "/"
                                + fromFile.getKey().getKey(0).toString()
                                        .substring(fromFile.getKey().getKey(0).toString().lastIndexOf("/") + 1)
                                + ".txt");
                        authorCell.setHyperlink(linkForAuthorNotepad);

                    }
                }

            }

            CellStyle cellStyles = sheet.getWorkbook().createCellStyle();
            Font fonts = sheet.getWorkbook().createFont();
            fonts.setBold(true);
            fonts.setFontHeightInPoints((short)8);
            cellStyles.setFont(fonts);
            Row rowww = sheet.createRow(rowCount.incrementAndGet());
            Cell cellTitlee = rowww.createCell(0);
            cellTitlee.setCellValue("Facile work through Gravity v1");
            cellTitlee.setCellStyle(cellStyles);

            rowCount.set(-1);
        }
    }

    private static void writeInNotepadFileAuthor(String fileName, Map<String, List<String>> listOfErrorsOfThatFile,
            Path repoAndBranchDir, String bugType) throws IOException {
        File errorFile = new File(repoAndBranchDir + "\\Authors\\" + bugType + "\\" + fileName + ".txt");
        if (errorFile.exists()) {
            errorFile.delete();
        }
        errorFile.createNewFile();
        FileWriter fw = new FileWriter(repoAndBranchDir + "\\Authors\\" + bugType + "\\" + fileName + ".txt");
        BufferedWriter writ = new BufferedWriter(fw);
        writ.write("The Responsible Author Seems to be....");
        writ.newLine();
        writ.newLine();
        Entry<String, List<String>> exactAuthor = listOfErrorsOfThatFile.entrySet().iterator().next();
        writ.write(exactAuthor.getKey());
        writ.newLine();
        writ.write("All Authors Of This File Are...");
        writ.newLine();
        for (String lines : exactAuthor.getValue()) {
            writ.write(lines);
            writ.newLine();
        }
        if (fw != null || writ != null) {
            writ.close();
            fw.close();
        }
    }

    public static void writeInToExcelUsingWorkbook(XSSFWorkbook workbook, Path pathTillBranch, String fileName)
            throws IOException {
        FileOutputStream outputStream = new FileOutputStream(pathTillBranch + "\\" + fileName + ".xlsx");
        workbook.write(outputStream);
        // Desktop desktop=java.awt.Desktop.getDesktop();
        // desktop.open(new File(fileName+".xlsx"));
    }
}
