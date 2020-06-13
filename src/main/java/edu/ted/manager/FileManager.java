package edu.ted.manager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static edu.ted.manager.FileSystemItemType.*;

public class FileManager {
    private static final File[] EMPTY_FILE_LIST = new File[]{};
    private static final Map<FileSystemItemType, FileFilter> fileFilters = new HashMap<>(4, 1);

    static {
        fileFilters.put(DIRECTORY, File::isDirectory);
        fileFilters.put(FILE, File::isFile);
        fileFilters.put(ALL, null);
    }

    /**
     * Принимает путь к папке
     * возвращает количество папок в папке и всех подпапках по пути
     */
    public static int countDirs(String path) {
        File startDirectory = new File(path);
        return countItems(startDirectory, DIRECTORY);
    }

    /**
     * Метод по перемещению папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование.
    */
    public static void move(String from, String to) {
        File startDirectory = new File(from);
        copyItems(startDirectory, to, true);
    }


    /**
     * Принимает путь к папке,
     * возвращает количество файлов в папке и всех подпапках по пути
     */
    public static int countFiles(String path) {
        File startDirectory = new File(path);
        return countItems(startDirectory, FILE);
    }

    /**
     * метод по удалению папок и файлов.
     * Параметр from - путь к файлу или папке
     */
    public static void remove(String from) {
        File startDirectory = new File(from);
        removeItems(startDirectory);
    }

    /**
     * метод по копированию папок и файлов.
     * Параметр from - путь к файлу или папке, параметр to - путь к папке куда будет производиться копирование
     */
    public static void copy(String from, String to) {
        File startDirectory = new File(from);
        copyItems(startDirectory, to, false);
    }

    private static File[] getChildFileSystemItems(File fileSystemItem, FileSystemItemType itemType) {
        FileFilter actualFilter = fileFilters.get(itemType);
        File[] list = fileSystemItem.listFiles(actualFilter);
        if (list == null) {
            return EMPTY_FILE_LIST;
        }
        return list;
    }

    private static int countItems(File fileSystemItem, FileSystemItemType itemType) {
        if (fileSystemItem.isDirectory()) {
            int counter = itemType.equals(FILE) ? 0 : 1;
            for (File item : getChildFileSystemItems(fileSystemItem, DIRECTORY)) {
                counter += countItems(item, itemType);
            }
            if (itemType.equals(FILE) || itemType.equals(ALL)) {
                counter += getChildFileSystemItems(fileSystemItem, FILE).length;
            }
            return counter;
        }
        return 0;
    }

    private static void copyItems(File fileSystemItem, String to, boolean move) {
        if (fileSystemItem.isDirectory()) {
            File destinationDir = copyDir(fileSystemItem, to);
            if (destinationDir == null){
                return;
            }
            for (File directory : getChildDirectories(fileSystemItem)) {
                copyItems(directory, destinationDir.getPath(), move);
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

    private static void removeItems(File fileSystemItem) {
        if (fileSystemItem.isDirectory()) {
            for (File directory : getChildDirectories(fileSystemItem)) {
                removeItems(directory);
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
        try (InputStream sourceFile = new FileInputStream(source);
             OutputStream destinationFile = new FileOutputStream(destination);
             OutputStream destinationStream = new BufferedOutputStream(destinationFile)) {
            while ((size = sourceFile.read(buffer)) > 0) {
                destinationStream.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
