package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outFile;

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        //BasicConfigurator.configure();

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception ex) {
            javaGrepImp.logger.error("Error: Unable to process", ex);
        }
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
    public List<File> listFiles(String rootDir) {

        //option 1: files.list
        try (Stream<Path> stream = Files.list(Paths.get(rootDir))) {
            List<Path> test = stream.filter(path -> path.toFile().isFile()).collect(Collectors.toList());
            List<Path> test2 = test;
        } catch (IOException e) {
            logger.error("Error IO Exception", e);
        }


        //for each file in stream, if dir enter dir to find files
        //if file add to stream

        //add exception for if dir does not exist?
        List<File> files = new ArrayList<>();
        File[] openedDirectory = new File(rootDir).listFiles();
        for (File file : openedDirectory) {
            if (file.isDirectory()) {
                List<File> subFiles = listFiles(file.toString());
                files.addAll(subFiles);
            } else {
                files.add(file);
            }
        }
            return files;
    }

    @Override
    public List<String> readLines(File inputFile) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                lines.add(line);
                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            logger.error("Error: " + inputFile + " is directory", e);
        } catch (IOException e) {
            logger.error("Error reading lines in " + inputFile, e);
        } return lines;
    }

    @Override
    public boolean containsPattern(String line) {
        Pattern p = Pattern.compile(this.getRegex());
        Matcher m = p.matcher(line);
        return m.find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outFile))))) {
            for (String line : lines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw e;
        }

    }




}
