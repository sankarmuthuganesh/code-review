package RealTime.ExcelWriter;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;

import RealTime.Logger.LogUtils;


public class ExcelFileCreation {
    private String filePath;
    private UUID commitId = null;
    private final String SUMMARY_SHEET_NAME = "Error Summary";
    private final Set<String> stagedFilesSet = new HashSet<String>();
    private final Set<String> stagedDeletedFilesSet = new HashSet<String>();
    private final Map<String, List<String>> errorDetailsMap = new LinkedHashMap<String, List<String>>();
    private final Map<String, List<String>> warningDetailsMap = new LinkedHashMap<String, List<String>>();
    private final Map<String, Map<String, Integer>> countInfoDetailsMap = new LinkedHashMap<String, Map<String, Integer>>();
    private final Map<String, String> errorFileSheetNameDetailsMap = new LinkedHashMap<String, String>();
    private final Map<String, String> warningFileSheetNameDetailsMap = new LinkedHashMap<String, String>();
    private final HSSFWorkbook workbook = new HSSFWorkbook();

    public ExcelFileCreation() {

    }

    public void setDefectsFilePath(String path) {
        filePath = path;
    }

    public void setErrorWarningMaps(Map<String, List<String>> errorMap, Map<String, List<String>> warningMap,
            Map<String, Map<String, Integer>> countInfoMap, Set<String> modifiedFileSet, Set<String> deletedFileSet) {
        errorDetailsMap.putAll(errorMap);
        warningDetailsMap.putAll(warningMap);
        countInfoDetailsMap.putAll(countInfoMap);
        stagedFilesSet.addAll(modifiedFileSet);
        stagedDeletedFilesSet.addAll(deletedFileSet);
    }

    public void setCommitId(UUID id) {
        commitId = id;
    }

    public void create() throws IOException {
        LogUtils.print("ExcelCreation::create method starts");
        String currentDate = new Date().toString().replaceAll("( |:)", "_");
        FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/Defects_" + currentDate + ".xls");
        workbook.writeProtectWorkbook("fireextinguisher", "Defects");
        writeContentInExcel();
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        LogUtils.print("ExcelCreation::create method ends");
    }

    private void writeContentInExcel() throws IOException {
        LogUtils.print("ExcelCreation::writeContentInExcel method starts");
        errorFileSheetNameDetailsMap.putAll(createSheetNames(errorDetailsMap, countInfoDetailsMap, true));
        warningFileSheetNameDetailsMap.putAll(createSheetNames(warningDetailsMap, countInfoDetailsMap, false));
        if (writeSummaryDetails()) {
            writeErrorWarningDetails(errorDetailsMap, true);
            writeErrorWarningDetails(warningDetailsMap, false);
        }
        LogUtils.print("ExcelCreation::writeContentInExcel method ends");
    }

    private void writeErrorWarningDetails(Map<String, List<String>> detailsMap, boolean isErrorMap) {
        LogUtils.print("ExcelCreation::writeErrorWarningDetails method starts");
        detailsMap.entrySet().forEach(
                file -> {
                    List<String> detailsList = file.getValue();
                    if (detailsList.size() > 0) {
                        String sheetName = isErrorMap ? errorFileSheetNameDetailsMap.get(file.getKey())
                                : warningFileSheetNameDetailsMap.get(file.getKey());
                        String fileName = file.getKey().substring(file.getKey().lastIndexOf("/") + 1);
                        HSSFSheet sheet = workbook.createSheet(sheetName);
                        HSSFRow firstRow = sheet.createRow(0);
                        HSSFCell fileNameCell = firstRow.createCell(0);
                        fileNameCell.setCellValue(fileName);
                        // firstRow.createCell(1).setCellValue("The no of lines in this file : "+noOfLinesInAFile.get(atomicInteger.get()));
                sheet.setColumnWidth(1, 75 * 100);
                // fileNameCell.setCellStyle(boldWithBorder);
                int width = 0;
                for (int iterator = 1; iterator <= detailsList.size(); iterator++) {
                    HSSFRow fileRowIndex = sheet.createRow(iterator);
                    HSSFCell fileCellIndex = fileRowIndex.createCell(0);
                    // To print no of lines in a file while initially running them
                    if (detailsList.get(iterator - 1).contains("The No of Lines in this file")) {
                        firstRow.createCell(1).setCellValue(detailsList.get(iterator - 1));
                    } else
                    // new adding hyperlink
                    if (detailsList.get(iterator - 1).contains("http://"))
                    {
                        String[] temp = detailsList.get(iterator - 1).split("per");
                        fileCellIndex.setCellValue(temp[0]);
                        CreationHelper createHelper = workbook.getCreationHelper();
                        org.apache.poi.ss.usermodel.Hyperlink hyperlink = createHelper
                                .createHyperlink(HyperlinkType.URL);
                        fileCellIndex = fileRowIndex.createCell(1);
                        fileCellIndex.setCellValue(temp[1].trim());
                        hyperlink.setAddress(temp[1].trim());
                        fileCellIndex.setHyperlink(hyperlink);
                    }// ends here
                    else {
                        fileCellIndex.setCellValue(detailsList.get(iterator - 1));
                    }
                    // fileCellIndex.setCellStyle(commonBorder);
                    if (detailsList.get(iterator - 1).length() > width) {
                        width = detailsList.get(iterator - 1).length();
                        sheet.setColumnWidth(0, 350 * 150);
                    }
                    // sheet.setColumnWidth(0,350*140);
                }
            }
        });
        LogUtils.print("ExcelCreation::writeErrorWarningDetails method ends");
    }

    private boolean writeSummaryDetails() throws IOException {
        LogUtils.print("ExcelCreation::writeSummaryDetails method starts");
        // new version check updated
        AtomicInteger rowIndex = new AtomicInteger(2);
        String userDomain = System.getenv("USERNAME");
        CreationHelper createHelper = workbook.getCreationHelper();
        HSSFSheet summarySheet = workbook.createSheet(SUMMARY_SHEET_NAME);
        workbook.setSheetOrder(SUMMARY_SHEET_NAME, 0);
        HSSFRow headerRowOne = summarySheet.createRow(0);

        URL url = new URL("http://192.168.41.191/mediawiki/index.php/Optimus");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Optional<String> optionalVersion = reader.lines().filter(pageData -> pageData.contains("Optimus_Tool_Version"))
                .findFirst();
        String version = "Optimus_Tool_Version 1.9.1 (Dec 15)";
        if (optionalVersion.isPresent()) {
            version = optionalVersion.get().substring(3).trim();
        }
        String latestVersion = "Optimus_Tool_Version 2.0 (Jan 03)";
        if (!latestVersion.equals(version)) {
            workbook.removeSheetAt(0);
            summarySheet = workbook.createSheet("Version Information");
            headerRowOne = summarySheet.createRow(22);
            HSSFCell tempCell = headerRowOne.createCell(0);
            tempCell.setCellValue("Your are using older version kindly update the tool from mediawiki.");
            HSSFFont font = workbook.createFont();
            font.setColor(HSSFColor.RED.index);
            font.setFontHeight((short)(7.5 * 100));
            HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
            hssfCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            hssfCellStyle.setFont(font);
            hssfCellStyle.setFillPattern(FillPatternType.BIG_SPOTS);
            tempCell.setCellStyle(hssfCellStyle);
            summarySheet.setColumnWidth(0, 350 * 170);
            HSSFFont font1 = workbook.createFont();
            font1.setColor(HSSFColor.BLUE.index);
            font1.setFontHeight((short)(7.5 * 30));
            // HSSFCell newtonDevEngineers = summarySheet.createRow(24).createCell(0);
            // newtonDevEngineers.setCellValue("For Newton Dev Engineers");
            // HSSFCellStyle newtonOptimusPathCellStyle=workbook.createCellStyle();
            // newtonOptimusPathCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            // newtonOptimusPathCellStyle.setFont(font1);
            // newtonOptimusPathCellStyle.setFillPattern(HSSFColor.BLUE.index);
            // HSSFCell newtonOptimusPath = summarySheet.createRow(25).createCell(0);
            // newtonOptimusPath.setCellValue("Optimus Tool Path");
            // org.apache.poi.ss.usermodel.Hyperlink
            // newtonOptimusPathHyperlink=createHelper.createHyperlink(Hyperlink.LINK_URL);
            // newtonOptimusPathHyperlink.setAddress("\\\\192.168.41.250\\scm-v3\\Common\\Optimus");
            // newtonOptimusPath.setHyperlink(newtonOptimusPathHyperlink);
            // newtonOptimusPath.setCellStyle(newtonOptimusPathCellStyle);
            // HSSFCell edisonDevEngineers = summarySheet.createRow(27).createCell(0);
            // edisonDevEngineers.setCellValue("For Edison Dev Engineers");
            // HSSFCellStyle edisonOptimusPathCellStyle=workbook.createCellStyle();
            // edisonOptimusPathCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            // edisonOptimusPathCellStyle.setFont(font1);
            // HSSFCell edisonOptimusPath = summarySheet.createRow(28).createCell(0);
            // edisonOptimusPath.setCellValue("Optimus Tool Path");
            // org.apache.poi.ss.usermodel.Hyperlink
            // EdisonOptimusPathHyperlink=createHelper.createHyperlink(Hyperlink.LINK_URL);
            // EdisonOptimusPathHyperlink.setAddress("\\\\192.168.50.220\\scm-v3\\Common\\Optimus");
            // edisonOptimusPath.setHyperlink(EdisonOptimusPathHyperlink);
            // edisonOptimusPath.setCellStyle(edisonOptimusPathCellStyle);
            return false;
        } else {
            headerRowOne.createCell(0).setCellValue(
                    latestVersion + ".  This defect sheet is generated on " + LocalDateTime.now());
        }
        HSSFRow headerRowTwo = summarySheet.createRow(1);
        if (commitId != null) {
            headerRowTwo.createCell(0).setCellValue(
                    "List of Files Committed (COMMIT-ID : "
                            + commitId.toString()
                            + "  DomainName : "
                            + userDomain
                            + " )");
        } else {
            headerRowTwo.createCell(0).setCellValue("Files List");
        }
        headerRowTwo.createCell(1).setCellValue("Errors");
        headerRowTwo.createCell(2).setCellValue("Warnings");

        Iterator<String> stagedFileIterator = stagedFilesSet.iterator();
        while (stagedFileIterator.hasNext()) {
            String file = stagedFileIterator.next();
            if (countInfoDetailsMap.containsKey(file)) {
                Map<String, Integer> dataMap = countInfoDetailsMap.get(file);
                HSSFRow fileRowIndex = summarySheet.createRow(rowIndex.getAndIncrement());
                HSSFCell cellZero = fileRowIndex.createCell(0);
                HSSFCell cellOne = fileRowIndex.createCell(1);
                HSSFCell cellTwo = fileRowIndex.createCell(2);
                cellZero.setCellValue(file);
                cellOne.setCellValue(dataMap.get("error").toString());

                if (dataMap.get("warning") > 1) {
                    cellTwo.setCellValue(Integer.toString((dataMap.get("warning") - 1)));
                } else {
                    cellTwo.setCellValue(Integer.toString(0));
                }
                if (!dataMap.get("error").equals(0)) {
                    org.apache.poi.ss.usermodel.Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                    String fileSheetName = errorFileSheetNameDetailsMap.get(file);
                    link.setAddress("'" + fileSheetName + "'");
                    cellOne.setHyperlink(link);
                }
                if (!dataMap.get("warning").equals(0)) {
                    org.apache.poi.ss.usermodel.Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
                    String fileSheetName = warningFileSheetNameDetailsMap.get(file);
                    link.setAddress("'" + fileSheetName + "'");
                    cellTwo.setHyperlink(link);
                }
                summarySheet.setColumnWidth(0, 350 * 140);
            } else {
                HSSFRow fileRowIndex = summarySheet.createRow(rowIndex.getAndIncrement());
                fileRowIndex.createCell(0).setCellValue(file);
                fileRowIndex.createCell(1).setCellValue("-");
                fileRowIndex.createCell(2).setCellValue("-");
            }
        }

        // To write the deleted files in the summary
        if (stagedDeletedFilesSet.size() > 0) {
            Iterator<String> stagedDeletedFileIterator = stagedDeletedFilesSet.iterator();
            rowIndex.set(summarySheet.getLastRowNum() + 2);
            summarySheet.createRow(rowIndex.getAndIncrement()).createCell(0).setCellValue("Deleted Files");
            while (stagedDeletedFileIterator.hasNext()) {
                String file = stagedDeletedFileIterator.next();
                HSSFRow fileRowIndex = summarySheet.createRow(rowIndex.getAndIncrement());
                fileRowIndex.createCell(0).setCellValue(file);
            }
        }
        // summarySheet.createRow(9876).createCell(0).setCellValue(LocalDateTime.now().toString());
        LogUtils.print("ExcelCreation::writeSummaryDetails method ends");
        return true;
    }

    private Map<String, String> createSheetNames(Map<String, List<String>> detailsMap,
            Map<String, Map<String, Integer>> countDetailsMap, boolean isErrorMap) {
        LogUtils.print("ExcelCreation::createSheetNames method starts");
        String namePrefix;
        String type;
        Map<String, String> sheetNameDetailsMap = new HashMap<String, String>();
        if (isErrorMap) {
            namePrefix = "E-";
            type = "error";
        } else {
            namePrefix = "W-";
            type = "warning";
        }
        AtomicInteger count = new AtomicInteger(1);
        detailsMap.entrySet().forEach(file -> {
            if (countDetailsMap.containsKey(file.getKey()) && countDetailsMap.get(file.getKey()).get(type) != 0) {
                String keyFileName = file.getKey().substring(file.getKey().lastIndexOf("\\") + 1);
                String sheetName = namePrefix + count.get() + "-" + keyFileName;
                if (keyFileName.length() > 25) {
                    sheetName = namePrefix + count.get() + "-" +
                            keyFileName.substring(0, 10) + "..." + keyFileName.substring(keyFileName.length() - 10);
                }
                sheetNameDetailsMap.put(file.getKey(), sheetName);
                count.incrementAndGet();
            }
        });
        LogUtils.print("ExcelCreation::createSheetNames method ends");
        return sheetNameDetailsMap;
    }
}