package ca.jrvs.apps.grep;

import org.apache.log4j.BasicConfigurator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        BasicConfigurator.configure();

        JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
        javaGrepLambdaImp.setRegex(args[0]);
        javaGrepLambdaImp.setRootPath(args[1]);
        javaGrepLambdaImp.setOutFile(args[2]);
        javaGrepLambdaImp.unreadable = new ArrayList<>();

        try {
            javaGrepLambdaImp.process();
        } catch (Exception ex) {
            javaGrepLambdaImp.logger.error("Error: Unable to process", ex);
        }
    }

    @Override
    public void process() throws IOException {

        Stream<String> matchedLines = listFilesStream(this.getRootPath())
                .flatMap(this::readLinesStream)
                .filter(this::containsPattern);
        writeToFile(matchedLines);

    }

    public Stream<Path> listFilesStream(String rootDir) throws IOException {

        return Files.find(Paths.get(rootDir), 99,
                (path, basicFileAttributes) -> path.toFile().isFile());
    }

    public Stream<String> readLinesStream(Path inputFilePath) {

        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(inputFilePath.toFile()));
            return bufferedReader.lines();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    public void writeToFile(Stream<String> lines) throws IOException {
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(this.getOutFile()))));

        lines.forEach(line -> {
            try {
                bw.write(line);
                bw.newLine();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write to file", e);
            }
        });
    }

}
