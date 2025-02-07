package org.example;

public class Main {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser(args);
        FileFilter fileFilter = new FileFilter(argumentParser);
        fileFilter.processFiles();
    }
}
