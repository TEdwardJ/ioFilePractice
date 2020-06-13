package edu.ted.manager;

import edu.ted.manager.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

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
    public void init(){
        createTestDirectoryStructure();
    }

    @AfterEach
    public void finalize(){
        FileManager.remove(sourceDir.getPath());
        FileManager.remove(destDir.getPath());
    }

    @Test
    public void copyDir() {
        String destDirPath = destDir.getPath();

        FileManager.copy(sourceDir.getPath(), destDirPath);
        File copiedDir = new File(destDirPath + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedDir.exists());
    }

    @Test
    public void copyNonExistingDir() {
        String destDirPath = destDir.getPath();

        FileManager.copy(sourceDir.getPath()+File.separator+"NonExisting", destDirPath);
        File copiedDir = new File(destDirPath + File.separator + sourceDir.getName());
        assertTrue(destDir.exists());
        assertFalse(copiedDir.exists());
    }

    @Test
    public void copyFile() {
        File testFile = new File(getClass().getClassLoader().getResource("FA.txt").getFile());
        String destDirPath = destDir.getPath();

        FileManager.copy(testFile.getPath(), destDirPath);
        File copiedFile = new File(destDirPath + File.separator + testFile.getName());
        assertTrue(destDir.exists());
        assertTrue(copiedFile.exists());
    }

    @Test
    public void copyWithHierarchy() {
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
    public void moveWithHierarchy() {
        createHierarchy();
        FileManager.move(sourceDirectoryName, destDirectoryName);
        int sourceCountFiles = FileManager.countFiles(sourceDir.getPath());
        int destCountFiles = FileManager.countFiles(destDir.getPath());
        int sourceCountDirectories = FileManager.countDirs(sourceDir.getPath());
        int destCountDirectories = FileManager.countDirs(destDir.getPath() + File.separator + sourceDir.getName());
        assertNotEquals(sourceCountFiles, destCountFiles);
        assertNotEquals(sourceCountDirectories, destCountDirectories);
    }

    @Test
    public void removeWithHierarchy() {
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
        String sourceDirPath = sourceDir.getPath();
        File dir1 = new File(sourceDirPath + File.separator + "dir1");
        dir1.mkdir();
        FileManager.copy(dir1.getPath(), sourceDirPath);
        File dir2 = new File(dir1.getPath() + File.separator + "dir2");
        dir2.mkdir();
        File dir3 = new File(dir1.getPath() + File.separator + "dir3");
        dir3.mkdir();
        FileManager.copy(dir2.getPath(), dir1.getPath());
        FileManager.copy(dir3.getPath(), dir1.getPath());
        File dir4 = new File(dir3.getPath() + File.separator + "dir4");
        dir4.mkdir();
        FileManager.copy(dir4.getPath(), dir3.getPath());
        FileManager.copy(testFile.getPath(), dir1.getAbsolutePath());
        FileManager.copy(testFile.getPath(), dir2.getAbsolutePath() + File.separator + "FA2.txt");
        FileManager.copy(testFile.getPath(), dir3.getAbsolutePath() + File.separator + "FA3.txt");
        FileManager.copy(testFile.getPath(), dir4.getAbsolutePath() + File.separator + "FA4_1.txt");
        FileManager.copy(testFile.getPath(), dir4.getAbsolutePath() + File.separator + "FA4_2.txt");
    }

    @Test
    void countFiles() {
        createHierarchy();
        int filesCount = FileManager.countFiles(sourceDir.getName());
        assertTrue(filesCount > 0);
        assertEquals(5, filesCount);
    }

    @Test
    void countDirs() {
        createHierarchy();
        int dirsCount = FileManager.countFiles(sourceDir.getName());
        assertTrue(dirsCount > 0);
        assertEquals(5, dirsCount);
    }

    @Test
    void countDirsStartingFromNonExistingDirectory() {
        int dirsCount = FileManager.countDirs("C:\\JavaCourse12");
        assertEquals(0, dirsCount);
        System.out.println("directories: " + dirsCount);
    }


}