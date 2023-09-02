package ca.jrvs.apps.grep;


import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public List<String> unreadable;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        BasicConfigurator.configure();

        //Constuct JavaGrepImp and set properties
        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);
        javaGrepImp.unreadable = new ArrayList<>();

        try {
            javaGrepImp.process();
        } catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex);
        }

        if (!javaGrepImp.unreadable.isEmpty()) {
            javaGrepImp.logger.info("The following cannot be read: {}", javaGrepImp.unreadable);
        }
    }

    @Override
    public void process() throws IOException {
        List<String> matchedLines = new ArrayList<>();
        //Loop through files in root directory using listFiles() method
        List<File> files = listFiles(rootPath);
        for (File file : files) {
            List<String> lines = readLines(file);
            //Loop through lines in file using readLines() method
            for (String line : lines) {
                //Check if line contains pattern using containsPattern() method
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        //Write matched lines to file
        writeToFile(matchedLines);
    }

    @Override
    public List<File> listFiles(String rootDir) throws IOException {

        //Create output list
        List<File> files = new ArrayList<>();

        //Convert rootDir string to file and "open" if directory, list itself if file, else throw exception.
        File dir = new File(rootDir);
        if (dir.isDirectory()) {
            try {
                File[] openedDirectory = dir.listFiles();
                //if item in directory is another directory, call listFiles to begin recursion. else, add to files list.
                assert openedDirectory != null;
                for (File file : openedDirectory) {
                    if (file.isDirectory()) {
                        List<File> subFiles = listFiles(file.toString());
                        files.addAll(subFiles);
                    } else {
                        files.add(file);
                    }
                }
            } catch (NullPointerException e) {
                if (!dir.canRead()) {
                    unreadable.add("Directory: " + rootDir);
                } else {
                    throw e;
                }
            }
        } else if (dir.isFile()) {
            files.add(dir);
        } else {
            throw new IllegalArgumentException("Directory " + rootDir + " does not exist");
        }
        return files;
    }

    @Override
    public Stream<Path> listFilesStream(String rootDir) throws IOException {
        return null;
    }

    @Override
    public List<String> readLines(File inputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            if (!inputFile.canRead()) {
                unreadable.add("File: " + inputFile);
            } else {
                throw e;
            }
        } return lines;
    }

    @Override
    public Stream<String> readLinesStream(Path inputFilePath) throws IOException {
        return null;
    }

    @Override
    public boolean containsPattern(String line) {
        Pattern p = Pattern.compile(this.getRegex());
        Matcher m = p.matcher(line);
        return m.find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

        try (BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(outFile))))) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    @Override
    public void writeStreamToFile(Stream<String> lines) throws IOException {
    }


    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getOutFile() {
        return outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }


}
