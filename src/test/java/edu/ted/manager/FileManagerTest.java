package edu.ted.manager;

import edu.ted.manager.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

    private void createTestDirectoryStructure() {
        sourceDirectoryName = "testSourceDirectory";
        destDirectoryName = "testDestinationDirectory";
        sourceDir = createTestDirectory(sourceDirectoryName);
        destDir = createTestDirectory(destDirectoryName);
    }

    @BeforeEach
    public void init() {
        createTestDirectoryStructure();
    }

    @AfterEach
    public void finalize() {
        FileManager.remove(sourceDir.getPath());
        FileManager.remove(destDir.getPath());
    }

    @Test
    public void givenDirectoryAndCopyToDestinationDirectory_whenExists_thenCorrect() {
        String destDirPath = destDir.getPath();
        FileManager.copy(sourceDir.getPath(), destDirPath);
        File copiedDir = new File(destDirPath + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedDir.exists());
    }

    @Test
    public void givenDestinationDirAndCopyNonExistingDir_whenDestinationDirectoryIsEmpty_thenCorrext() {
        String destDirPath = destDir.getPath();

        FileManager.copy(sourceDir.getPath() + File.separator + "NonExisting", destDirPath);
        File copiedDir = new File(destDirPath + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertFalse(copiedDir.exists());
    }

    @Test
    public void givenFile_whenCopiedToDestination_thenCorrect() {
        File testFile = new File(getClass().getClassLoader().getResource("FA.txt").getFile());
        String destDirPath = destDir.getPath();

        FileManager.copy(testFile.getPath(), destDirPath);
        File copiedFile = new File(destDirPath + File.separator + testFile.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedFile.exists());
    }

    @Test
    public void givenDirectoryHierarchy_whenCopiedToAnotherDirectory_thenCorrect() {
        createHierarchy();
        FileManager.copy(sourceDirectoryName, destDirectoryName);
        int sourceCountFiles = FileManager.countFiles(sourceDir.getPath());
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int sourceCountDirectories = FileManager.countDirs(sourceDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath() + File.separator + sourceDir.getName());
        assertEquals(sourceCountFiles, destCountFiles);
        assertEquals(sourceCountDirectories, destCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenMovedToAnotherDirectory_thenCorrect() {
        createHierarchy();
        FileManager.move(sourceDirectoryName + "/dir1", destDirectoryName);
        int sourceCountFiles = FileManager.countFiles(sourceDir.getPath());
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int sourceCountDirectories = FileManager.countDirs(sourceDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath());
        assertTrue(destCountDirectories > 0);
        assertTrue(destCountFiles > 0);
        assertNotEquals(sourceCountFiles, destCountFiles);
        assertNotEquals(sourceCountDirectories, destCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenMovingToFile_thenNoAction() throws IOException {
        createHierarchy();

        new File(destDir.getAbsolutePath() + "/FA.txt").createNewFile();
        FileManager.move(sourceDirectoryName, destDirectoryName + "/FA.txt");
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath() + File.separator + sourceDir.getName());
        assertEquals(1, destCountFiles);
        assertEquals(0, destCountDirectories);
    }

    @Test
    public void givenDirectoryHierarchy_whenRemoved_thenCorrect() {
        createHierarchy();
        assertTrue(sourceDir.exists());
        assertTrue(destDir.exists());
        FileManager.remove(sourceDirectoryName);
        FileManager.remove(destDirectoryName);
        assertFalse(sourceDir.exists());
        assertFalse(destDir.exists());
    }

    private void createHierarchy() {
        File testFile = new File(getClass().getClassLoader().getResource("FA.txt").getFile());
        File directory1 = new File(sourceDirectoryName + "/dir1");
        File directory2 = new File(sourceDirectoryName + "/dir1/dir2");
        File directory3 = new File(sourceDirectoryName + "/dir1/dir3");
        File directory4 = new File(sourceDirectoryName + "/dir1/dir3/dir4");
        String sourceDirPath = sourceDir.getPath();
        directory1.mkdir();
        directory2.mkdir();
        directory3.mkdir();
        directory4.mkdir();
        FileManager.copy(testFile.getPath(), directory1.getAbsolutePath());
        FileManager.copy(testFile.getPath(), directory2.getAbsolutePath());
        FileManager.copy(testFile.getPath(), directory3.getAbsolutePath());
        FileManager.copy(testFile.getPath(), directory4.getAbsolutePath() + File.separator + "FA4_1.txt");
        FileManager.copy(testFile.getPath(), directory4.getAbsolutePath() + File.separator + "FA4_2.txt");
    }

    @Test
    void givenDirectoryHierarchyAndCountFiles_whenNonZero_thenCorrect() {
        createHierarchy();
        int filesCount = FileManager.countFiles(sourceDir.getName());
        assertTrue(filesCount > 0);
        assertEquals(5, filesCount);
    }

    @Test
    void givenDirectoryHierarchyAndCountDirectories_whenNonZero_thenCorrect() {
        createHierarchy();
        int dirsCount = FileManager.countDirs(sourceDir.getName());
        assertTrue(dirsCount > 0);
        assertEquals(4, dirsCount);
    }

    @Test
    void givenNonExistingDirectory_whenCountDirs_thenZero() {
        int dirsCount = FileManager.countDirs("C:\\JavaCourse12");
        assertEquals(0, dirsCount);
        System.out.println("directories: " + dirsCount);
    }


}