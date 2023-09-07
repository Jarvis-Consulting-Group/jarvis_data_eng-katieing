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

        if (!unreadable.isEmpty()) {
            logger.info("The following cannot be read: {}", unreadable);
        }

    }

    public Stream<File> listFilesStream(String rootDir) throws IOException {

        Path path = Paths.get(rootDir);
        File rootFile = path.toFile();

        if (rootFile.isFile()) {
            return Stream.of(rootFile);
        } else {
            return Files.list(path)
                    .map(Path::toFile)
                    .flatMap(file -> {
                        try {
                            return listFilesStream(file.toString());
                        } catch (IOException e) {
                            if (!file.canRead()) {
                                unreadable.add(file.toString());
                            } else {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    });
        }
    }

    public Stream<String> readLinesStream(File inputFile) {

        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(inputFile));
            return bufferedReader.lines();
        } catch (FileNotFoundException e) {
            if (!inputFile.canRead()) {
                unreadable.add(inputFile.toString());
                return null;
            } else {
                throw new RuntimeException("File not found", e);
            }
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
        bw.flush();
    }

}
