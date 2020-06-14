package edu.ted.manager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static edu.ted.manager.FileSystemItemType.*;

public class FileManager {
    private static final File[] EMPTY_FILE_LIST = new File[]{};
    private static final Map<FileSystemItemType, FileFilter> FILE_FILTERS = new HashMap<>(3, 1);

    static {
        FILE_FILTERS.put(DIRECTORY, File::isDirectory);
        FILE_FILTERS.put(FILE, File::isFile);
        FILE_FILTERS.put(ALL, null);
    }

    /**
     * Принимает путь к папке
     * возвращает количество папок в папке и всех подпапках по пути
     */
    public static int countDirs(String path) {
        File startDirectory = new File(path);
        return countChildFileSystemItems(startDirectory, DIRECTORY);
    }

    /**
     * Метод по перемещению папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование.
    */
    public static void move(String from, String to) {
        File startDirectory = new File(from);
        copyFileSystemItems(startDirectory, to, true);
    }

    /**
     * Принимает путь к папке,
     * возвращает количество файлов в папке и всех подпапках по пути
     */
    public static int countFiles(String path) {
        File startDirectory = new File(path);
        return countChildFileSystemItems(startDirectory, FILE);
    }

    /**
     * метод по удалению папок и файлов.
     * Параметр from - путь к файлу или папке
     */
    public static void remove(String from) {
        File startDirectory = new File(from);
        removeFileSystemItemsRecursively(startDirectory);
    }

    /**
     * метод по копированию папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование
     */
    public static void copy(String from, String to) {
        File startDirectory = new File(from);
        copyFileSystemItems(startDirectory, to, false);
    }

    private static File[] getChildFileSystemItems(File fileSystemItem, FileSystemItemType itemType) {
        File[] list = fileSystemItem.listFiles(FILE_FILTERS.get(itemType));
        if (list == null) {
            return EMPTY_FILE_LIST;
        }
        return list;
    }

    private static int countChildFileSystemItems(File fileSystemItem, FileSystemItemType itemTypeToBeCounted) {
        if (fileSystemItem.isDirectory()) {
            int counter = itemTypeToBeCounted == FILE ? 0 : 1;
            for (File directory : getChildFileSystemItems(fileSystemItem, DIRECTORY)) {
                counter += countChildFileSystemItems(directory, itemTypeToBeCounted);
            }
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
            if (destinationDir == null){
                return;
            }
            for (File directory : getChildDirectories(fileSystemItem)) {
                copyFileSystemItems(directory, destinationDir.getPath(), move);
            }
            for (File file : getChildFiles(fileSystemItem)) {
                copyFile(file, destinationDir.getPath());
                if (move) {
                    file.delete();
                }
            }
        } else if (fileSystemItem.isFile()) {
            copyFile(fileSystemItem, to);
        }
        if (move) {
            fileSystemItem.delete();
        }
    }

    private static void removeFileSystemItemsRecursively(File fileSystemItem) {
        if (fileSystemItem.isDirectory()) {
            for (File directory : getChildDirectories(fileSystemItem)) {
                removeFileSystemItemsRecursively(directory);
            }
            for (File file : getChildFiles(fileSystemItem)) {
                file.delete();
            }
        }
        fileSystemItem.delete();
    }

    private static File[] getChildFiles(File fileSystemItem) {
        return getChildFileSystemItems(fileSystemItem, FILE);
    }

    private static File[] getChildDirectories(File fileSystemItem) {
        return getChildFileSystemItems(fileSystemItem, DIRECTORY);
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
            if (destinationDir.mkdir()){
                return destinationDir;
            } else{
                return null;
            }
        }
        return destinationDir;
    }

}
