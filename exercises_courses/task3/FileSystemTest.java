package exercises_courses.task3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

class FileNameExistsException extends Exception {
    public FileNameExistsException(String fileName, String folderName) {
        super(String.format("There is already a file named %s in the folder %s", fileName, folderName));
    }
}

interface IFile {
    String getFileName();
    long getFileSize();
    String getFileInfo();
    void sortBySize();
    double findLargestFile();
}

class File implements IFile {

    private String name;
    private Long size;

    public File(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo() {
        return String.format("File name: %10s File size: %10d", name, size);
    }

    @Override
    public void sortBySize() {
        // Regular files don't need sorting - they contain no other files
    }

    @Override
    public double findLargestFile() {
        return size;
    }
}

class Folder implements IFile {

    private String name;
    private List<IFile> files;

    public Folder(String name) {
        this.name = name;
        this.files = new ArrayList<>();
    }

    public void addFile(IFile file) throws FileNameExistsException {
        // Check if file with same name already exists
        for (IFile f: files) {
            if (f.getFileName().equals(file.getFileName())) {
                throw new FileNameExistsException(file.getFileName(), this.name);
            }
        }
        files.add(file);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        // Sum the sizes of all files (both regular files and subfolders)
        return files.stream()
                .mapToLong(IFile::getFileSize)
                .sum();
    }

    @Override
    public String getFileInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Folder name: %10s Folder size: %10d\n", name, getFileSize()));

        // Print all files/subfolders with indentation
        for (IFile file: files) {
            String fileInfo = file.getFileInfo();
            // Split by lines and add tab to each
            for (String line: fileInfo.split("\n")) {
                sb.append("\t").append(line).append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public void sortBySize() {
        // First, sort all files in this folder by their size
        files.sort(Comparator.comparingLong(IFile::getFileSize));

        // Then, recursively sort all subdirectories
        for (IFile file: files) {
            file.sortBySize();
        }
    }

    @Override
    public double findLargestFile() {
        return files.stream()
                .mapToDouble(IFile::findLargestFile)
                .max()
                .orElse(0.0);
    }
}

class FileSystem {

    private Folder rootDirectory;

    // Default constructor
    public FileSystem() {
        this.rootDirectory = new Folder("root");
    }

    public void addFile(IFile file) throws FileNameExistsException {
        rootDirectory.addFile(file);
    }

    public long findLargestFile() {
        return (long) rootDirectory.findLargestFile();
    }

    public void sortBySize() {
        rootDirectory.sortBySize();
    }

    @Override
    public String toString() {
        return rootDirectory.getFileInfo();
    }
}

public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());




    }
}