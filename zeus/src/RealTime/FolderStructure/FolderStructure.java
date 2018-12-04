package RealTime.FolderStructure;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class FolderStructure {
    static BufferedWriter notepad;

    public void checkStructure(String localBranchPath, File theDirBranch) throws IOException {
        File[] filesInsideADirectory = new File(localBranchPath).listFiles();
        int directoryCount = 0;
        while (directoryCount < filesInsideADirectory.length) {
            if (filesInsideADirectory[directoryCount].isDirectory()
                    && filesInsideADirectory[directoryCount].getName().startsWith("hue")) {
                File listOfFiles = new File(theDirBranch.getAbsolutePath()
                        + "\\"
                        + filesInsideADirectory[directoryCount].getName()
                        + ".txt");
                if (listOfFiles.exists()) {
                    listOfFiles.delete();
                }
                listOfFiles.createNewFile();
                FileWriter fw = new FileWriter(theDirBranch.getAbsolutePath()
                        + "\\"
                        + filesInsideADirectory[directoryCount].getName()
                        + ".txt");
                BufferedWriter writ = new BufferedWriter(fw);
                notepad = writ;
                notepad.write(filesInsideADirectory[directoryCount].getName());
                printFiles(filesInsideADirectory[directoryCount].getAbsolutePath(),
                        filesInsideADirectory[directoryCount].getName().length() / 2, 0);
                if (fw != null || writ != null) {
                    writ.close();
                    fw.close();
                    listOfFiles.setReadOnly();
                }
            }
            directoryCount++;
        }
Desktop dk=Desktop.getDesktop();
dk.open(theDirBranch);
    }

    static void printFiles(String directory, int lengthOfParentFolder, int i) throws IOException {
        File[] filesInsideADirectory = new File(directory).listFiles();
        int count = 0;
        while (count < filesInsideADirectory.length) {
            if (filesInsideADirectory[count].isFile()
                    && (fileExtensionChecker(filesInsideADirectory[count].getAbsolutePath()))) {
                if (i == 0) {
                    notepad.newLine();
                }
                notepad.write(StringUtils.repeat(" ", lengthOfParentFolder));
                notepad.write("|");
                notepad.write(StringUtils.repeat("-", 5));
                notepad.write("> ");
                notepad.write(filesInsideADirectory[count].getName());
                notepad.newLine();
            }
            else if (filesInsideADirectory[count].isDirectory()) {
                notepad.write(StringUtils.repeat(" ", lengthOfParentFolder));
                notepad.write("|");
                notepad.write(StringUtils.repeat("-", 5));
                notepad.write("> ");
                notepad.write(filesInsideADirectory[count].getName());
                notepad.newLine();
                int printedLength = lengthOfParentFolder + 8 + (filesInsideADirectory[count].getName().length() / 2);
                printFiles(filesInsideADirectory[count].getAbsolutePath(), printedLength, 1);
            }
            count++;
        }
    }

    static boolean fileExtensionChecker(String nameOfFile) {
        if (nameOfFile.endsWith(".xml")
                || nameOfFile.endsWith(".js")
                || nameOfFile.endsWith(".java")
                || nameOfFile.endsWith(".less")) {
            return true;
        }
        return false;
    }

}
