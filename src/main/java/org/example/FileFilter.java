package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileFilter {
    private final ArgumentParser argumentParser;
    private List<Long> integers = new ArrayList<>();
    private List<Double> floats = new ArrayList<>();
    private List<String> strings = new ArrayList<>();

    public FileFilter(ArgumentParser argumentParser) {
        this.argumentParser = argumentParser;
    }

    public void processFiles() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List[] inputStrings = new List[argumentParser.getInputFiles().size()];
        for(int i = 0;i<argumentParser.getInputFiles().size();i++){

            int finalI = i;

            futures.add(CompletableFuture.runAsync(() -> inputStrings[finalI] = readFile(argumentParser.getInputFiles().get(finalI)), executor));
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        filter(inputStrings);
        futures.add(CompletableFuture.runAsync(() -> writeOutput(integers, "integers.txt"), executor));
        futures.add(CompletableFuture.runAsync(() -> writeOutput(floats, "floats.txt"), executor));
        futures.add(CompletableFuture.runAsync(() -> writeOutput(strings, "strings.txt"), executor));

        printStatistics();

        executor.shutdown();
    }

    private List<String> readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла " + filePath + ": " + e.getMessage());
        }
        return Collections.emptyList();
    }

    private void filter(List<String>[] inputStrings) {
        boolean isListsNotEmpty = true;


        while(isListsNotEmpty){
            isListsNotEmpty = !inputStrings[1].isEmpty();
            for(List<String> a:inputStrings){
                if(!a.isEmpty()){
                    String line = a.removeFirst();
                    try {
                        if (line.matches("-?\\d+")) {
                            integers.add(Long.parseLong(line));
                        } else if (line.matches("[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?")) {
                            floats.add(Double.parseDouble(line));
                        } else {
                            strings.add(line);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("следующая строка была проигнорирована: "+line+": "+e.getMessage());
                    }
                }
                isListsNotEmpty = isListsNotEmpty||!a.isEmpty();
            }
        }

    }

    private void writeOutput(List<?> data, String fileName) {
        if (data.isEmpty()) return;

        String filePath = Paths.get(argumentParser.getOutputDir(), argumentParser.getPrefix() + fileName).toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, argumentParser.isAppend()))) {
            for (Object item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл " + filePath + ": " + e.getMessage());
        }
    }

    private void printStatistics() {
        if (argumentParser.isFullStats() || argumentParser.isShortStats()) {
            System.out.println("Статистика целых чисел:");
            printIntegerStatistics();
            System.out.println("Статистика вещественных чисел:");
            printFloatStatistics();
            System.out.println("Статистика строк:");
            printStringStatistics();
        }
    }

    private void printIntegerStatistics() {
        if (integers.isEmpty()) {
            System.out.println("Целых чисел: 0");
            return;
        }
        System.out.println("Целых чисел: " + integers.size());
        if (argumentParser.isFullStats()) {
            System.out.println("Минимум: " + Collections.min(integers));
            System.out.println("Максимум: " + Collections.max(integers));
            System.out.println("Сумма: " + integers.stream().mapToLong(Long::longValue).sum());
            System.out.println("Среднее: " + integers.stream().mapToLong(Long::longValue).average().orElse(0.0));
        }
    }

    private void printFloatStatistics() {
        if (floats.isEmpty()) {
            System.out.println("Вещественных чисел: 0");
            return;
        }
        System.out.println("Вещественных чисел: " + floats.size());
        if (argumentParser.isFullStats()) {
            System.out.println("Минимум: " + Collections.min(floats));
            System.out.println("Максимум: " + Collections.max(floats));
            System.out.println("Сумма: " + floats.stream().mapToDouble(Double::doubleValue).sum());
            System.out.println("Среднее: " + floats.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        }
    }

    private void printStringStatistics() {
        if (strings.isEmpty()) {
            System.out.println("Строк: 0");
            return;
        }
        System.out.println("Строк: " + strings.size());
        if (argumentParser.isFullStats()) {
            String shortest = Collections.min(strings, Comparator.comparingInt(String::length));
            String longest = Collections.max(strings, Comparator.comparingInt(String::length));
            System.out.println("Кратчайшая строка: " + shortest + " (" + shortest.length() + " символов)");
            System.out.println("Длиннейшая строка: " + longest + " (" + longest.length() + " символов)");
        }
    }
}
