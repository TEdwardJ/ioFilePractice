package edu.ted.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {

    private String sourceDirectoryName;
    private String destDirectoryName;

    private File sourceDir;
    private File destDir;

    private File createTestDirectory(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists()) {
            assertTrue(directory.mkdir());
        }
        return directory;
    }

    @BeforeEach
    public void init() throws IOException {
        sourceDirectoryName = "testSourceDirectory";
        destDirectoryName = "testDestinationDirectory";
        sourceDir = createTestDirectory(sourceDirectoryName);
        destDir = createTestDirectory(destDirectoryName);
        createHierarchy();
    }

    @AfterEach
    public void after() {
        FileManager.remove(sourceDir.getPath());
        FileManager.remove(destDir.getPath());
    }

    @Test
    public void givenSingleDirectoryAndCopyToDestinationDirectory_whenExists_thenCorrect() {
        File destinationDirectory = new File(destDirectoryName + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertFalse(destinationDirectory.exists());
        FileManager.copy(sourceDir.getPath(), destDirectoryName);
        assertTrue(destinationDirectory.exists());
    }

    @Test
    public void givenDestinationDirAndCopyNonExistingDir_whenDestinationDirectoryIsEmpty_thenCorrect() {
        FileManager.copy(sourceDir.getPath() + "/NonExisting.txt", destDirectoryName);
        File copiedDir = new File(destDirectoryName + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertFalse(copiedDir.exists());
    }

    @Test
    public void givenSingleFile_whenCopiedToDestination_thenCorrect() throws IOException {
        String sourceFileContent = "Hello World!!!";
        File testFile = createFile(sourceDirectoryName + "/testFile.txt", sourceFileContent);

        FileManager.copy(testFile.getPath(), destDirectoryName);
        File copiedFile = new File(destDirectoryName + "/" + testFile.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedFile.exists());
        String destinationFileContent = readFile(copiedFile);
        assertEquals(sourceFileContent, destinationFileContent);
        assertEquals(testFile.length(), copiedFile.length());
    }

    @Test
    public void givenSingleEmptyFile_whenCopiedToDestination_thenCorrect() throws IOException {
        String sourceFileContent = "";
        File testFile = createFile(sourceDirectoryName + "/testFile.txt", sourceFileContent);

        FileManager.copy(testFile.getPath(), destDirectoryName);
        File copiedFile = new File(destDirectoryName + "/" + testFile.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedFile.exists());
        String destinationFileContent = readFile(copiedFile);
        assertEquals(sourceFileContent, destinationFileContent);
        assertEquals(testFile.length(), copiedFile.length());
    }

    @Test
    public void givenDirectoryHierarchy_whenCopiedToAnotherDirectory_thenCorrect() {
        FileManager.copy(sourceDirectoryName, destDirectoryName);
        int sourceCountFiles = FileManager.countFiles(sourceDir.getPath());
        int sourceCountDirectories = FileManager.countDirs(sourceDir.getPath());
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath() + File.separator + sourceDir.getName());
        assertEquals(sourceCountFiles, destCountFiles);
        assertEquals(sourceCountDirectories, destCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenMovedToAnotherDirectory_thenCorrect() {
        FileManager.move(sourceDirectoryName + "/dir1", destDirectoryName+"/dir1");
        int sourceCountFiles = FileManager.countFiles(sourceDir.getPath());
        int sourceCountDirectories = FileManager.countDirs(sourceDir.getPath());
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath());
        assertTrue(destCountDirectories > 0);
        assertTrue(destCountFiles > 0);
        assertEquals(0, sourceCountFiles);
        assertEquals(0, sourceCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenMovingToFile_thenNoAction() throws IOException {
        createFile(destDir.getAbsolutePath() + "/dest.txt", "Help me!!!");
        FileManager.move(sourceDirectoryName, destDirectoryName + "/dest.txt");
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath() + File.separator + sourceDir.getName());
        assertEquals(1, destCountFiles);
        assertEquals(0, destCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenRemoved_thenCorrect() {
        assertTrue(FileManager.countDirs(sourceDir.getPath()) > 0);
        assertTrue(FileManager.countDirs(sourceDir.getPath()) > 0);
        assertTrue(sourceDir.exists());
        assertTrue(destDir.exists());
        FileManager.remove(sourceDirectoryName);
        assertTrue(FileManager.countDirs(sourceDir.getPath()) == 0);
        assertTrue(FileManager.countDirs(sourceDir.getPath()) == 0);
        assertFalse(sourceDir.exists());
        FileManager.remove(destDirectoryName);
        assertFalse(destDir.exists());
    }

    private void createHierarchy() throws IOException {
        new File("testSourceDirectory");
        new File("testSourceDirectory/dir1").mkdir();
        new File( "testSourceDirectory/dir1/dir2").mkdir();
        new File( "testSourceDirectory/dir1/dir3").mkdir();
        new File("testSourceDirectory/dir1/dir3/dir4").mkdir();
        createFile("testSourceDirectory/dir1" + "/File1.txt", "Just a text content of test file");
        createFile("testSourceDirectory/dir1/dir2" + "/File2.txt", "Just a text content of test file");
        createFile("testSourceDirectory/dir1/dir3" + "/File3.txt", "Just a text content of test file");
        createFile("testSourceDirectory/dir1/dir3/dir4" + "/File41.txt", "Just a text content of test file");
        createFile("testSourceDirectory/dir1/dir3/dir4" + "/File42.txt", "Just a text content of test file");
    }

    @Test
    void givenDirectoryHierarchyAndCountFiles_whenNonZero_thenCorrect() {
        int filesCount = FileManager.countFiles(sourceDir.getName());
        assertEquals(5, filesCount);
    }

    @Test
    void givenDirectoryHierarchyAndCountDirectories_whenNonZero_thenCorrect() {
        int dirsCount = FileManager.countDirs(sourceDir.getName());
        assertEquals(4, dirsCount);
    }

    @Test
    void givenNonExistingDirectory_whenCountDirs_thenZero() {
        int dirsCount = FileManager.countDirs("C:\\JavaCourse12");
        assertEquals(0, dirsCount);
    }

    private File createFile(String path, String contentText) throws IOException {
        File fileToBeCreated = new File(path);
        if (contentText == null || contentText.length()==0){
            fileToBeCreated.createNewFile();
            return fileToBeCreated;
        }
        try (FileWriter writer = new FileWriter(fileToBeCreated)) {
            writer.write(contentText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileToBeCreated;
    }

    private String readFile(File path) {
        StringBuilder contentText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while((line = reader.readLine())!=null){
                contentText.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentText.toString();
    }
}