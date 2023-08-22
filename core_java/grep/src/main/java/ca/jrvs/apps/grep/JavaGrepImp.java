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
        //File dir = new File(rootDir);
        //return Arrays.asList(dir.listFiles());

        List<File> files = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(rootDir));) {
            for (Path path : directoryStream) {
                File file = path.toFile();
                if (file.isDirectory()) {
                    List<File> subfiles = listFiles(path.toString());
                    files.addAll(subfiles);
                } else {
                    files.add(path.toFile());
                }
            }
        } catch (IOException e) {
            logger.error("Error ", e);
        } return files;
    }

    @Override
    public List<String> readLines(File inputFile) {
        if (inputFile.isDirectory()) {
            return null;
        }
        try (BufferedReader text = new BufferedReader(new FileReader(inputFile))) {
            Stream<String> stream = text.lines();
            Object[] objects = stream.toArray();
            List<String> lines = new ArrayList<>();
            for (Object object : objects) {
                lines.add(object.toString());
            }
            return lines;
        } catch (FileNotFoundException e) {
            logger.error("File " + inputFile + " not found:", e);
        } catch (IOException e) {
            logger.error("Error: ", e);
        } return null;
    }

    @Override
    public boolean containsPattern(String line) {
        Pattern p = Pattern.compile(this.getRegex());
        Matcher m = p.matcher(line);
        return m.find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

    }




}
