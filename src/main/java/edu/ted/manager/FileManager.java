package edu.ted.manager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static edu.ted.manager.FileSystemItemType.*;

public class FileManager {
    private static final File[] EMPTY_FILE_ARRAY = new File[]{};
    private static final Map<FileSystemItemType, FileFilter> FILE_FILTERS = new HashMap<>(2, 1);

    static {
        FILE_FILTERS.put(DIRECTORY, File::isDirectory);
        FILE_FILTERS.put(FILE, File::isFile);
    }

    /**
     * Принимает путь к папке
     * возвращает количество папок в папке и всех подпапках по пути
     */
    public static int countDirs(String path) {
        File startFileSystemItem = new File(path);
        return countChildFileSystemItems(startFileSystemItem, DIRECTORY);
    }

    /**
     * Метод по перемещению папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование.
     */
    public static void move(String from, String to) {
        File startFileSystemItem = new File(from);
        File destinationDirectory = new File(to);
        if (destinationDirectory.isFile() && startFileSystemItem.isDirectory()) {
            return;
        }
        copyFileSystemItems(startFileSystemItem, to, true);
    }

    /**
     * Принимает путь к папке,
     * возвращает количество файлов в папке и всех подпапках по пути
     */
    public static int countFiles(String path) {
        File startFileSystemItem = new File(path);
        return countChildFileSystemItems(startFileSystemItem, FILE);
    }

    /**
     * метод по удалению папок и файлов.
     * Параметр from - путь к файлу или папке
     */
    public static void remove(String from) {
        File startFileSystemItem = new File(from);
        removeFileSystemItemsRecursively(startFileSystemItem);
    }

    /**
     * метод по копированию папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование
     */
    public static void copy(String from, String to) {
        File startFileSystemItem = new File(from);
        copyFileSystemItems(startFileSystemItem, to, false);
    }

    private static File[] getChildFileSystemItems(File fileSystemItem, FileSystemItemType fileSystemItemType) {
        File[] childFileSystemItemsArray = fileSystemItem.listFiles(FILE_FILTERS.get(fileSystemItemType));
        if (childFileSystemItemsArray == null) {
            return EMPTY_FILE_ARRAY;
        }
        return childFileSystemItemsArray;
    }

    private static int countChildFileSystemItems(File fileSystemItem, FileSystemItemType itemTypeToBeCounted) {
        if (fileSystemItem.isDirectory()) {
            int counter = 0;
            int takeDirectoriesIntoCount = itemTypeToBeCounted == FILE ? 0 : 1;
            //Loop to count recursively in hierarchy
            for (File directory : getChildFileSystemItems(fileSystemItem, DIRECTORY)) {
                counter += takeDirectoriesIntoCount + countChildFileSystemItems(directory, itemTypeToBeCounted);
            }
            //Loop to count files on current level
            if (itemTypeToBeCounted == FILE || itemTypeToBeCounted == ALL) {
                counter += getChildFileSystemItems(fileSystemItem, FILE).length;
            }
            return counter;
        }
        return 0;
    }

    private static void copyFileSystemItems(File fileSystemItem, String to, boolean move) {
        if (fileSystemItem.isDirectory()) {
            File destinationDir = copyDir(fileSystemItem, to);
            if (destinationDir == null) {
                return;
            }
            for (File directory : getChildFileSystemItems(fileSystemItem, ALL)) {
                copyFileSystemItems(directory, destinationDir.getPath(), move);
            }
            fileSystemItem.delete();
        } else if (fileSystemItem.isFile()) {
            if (move) {
                fileSystemItem.renameTo(new File(to + File.separator + fileSystemItem.getName()));
            } else {
                copyFile(fileSystemItem, to);
            }
        }
    }

    private static void removeFileSystemItemsRecursively(File fileSystemItem) {
        for (File directory : getChildFileSystemItems(fileSystemItem, ALL)) {
            removeFileSystemItemsRecursively(directory);
            directory.delete();
        }
        fileSystemItem.delete();
    }

    private static void copyFile(File source, String to) {
        int size;
        byte[] buffer = new byte[1024];
        File destination = new File(to);
        if (destination.isDirectory()) {
            destination = new File(to + File.separator + source.getName());
        }
        try (InputStream sourceFile = new BufferedInputStream(new FileInputStream(source));
             OutputStream destinationStream = new BufferedOutputStream(new FileOutputStream(destination))) {
            while ((size = sourceFile.read(buffer)) > 0) {
                destinationStream.write(buffer, 0, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File copyDir(File sourceDir, String to) {
        File destinationDir = new File(to + File.separator + sourceDir.getName());
        if (!destinationDir.exists()) {
            if (destinationDir.mkdir()) {
                return destinationDir;
            } else {
                return null;
            }
        }
        return destinationDir;
    }
}
