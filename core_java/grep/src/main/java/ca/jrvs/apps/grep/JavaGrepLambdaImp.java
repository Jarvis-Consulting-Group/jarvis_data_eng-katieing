package ca.jrvs.apps.grep;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        //BasicConfigurator.configure();

        JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
        javaGrepLambdaImp.setRegex(args[0]);
        javaGrepLambdaImp.setRootPath(args[1]);
        javaGrepLambdaImp.setOutFile(args[2]);

        try {
            javaGrepLambdaImp.process();
        } catch (Exception ex) {
            javaGrepLambdaImp.logger.error("Error: Unable to process", ex);
        }
    }

    @Override
    public void process() throws IOException {

        try (Stream<String> matchedLines = listFilesStream(this.getRootPath())
                .flatMap(this::readLinesStream)
                .filter(this::containsPattern)) {
            writeStreamToFile(matchedLines);
        } catch (IllegalStateException e) {
            System.out.println("oh no");
        }
    }

    @Override
    public Stream<Path> listFilesStream(String rootDir) {

        try {
            return Files.find(Paths.get(rootDir), 99,
                    (path, basicFileAttributes) -> path.toFile().isFile());
        } catch (IOException e) {
            logger.error("I/O Error when accessing " + rootDir, e);
            return null;
        }
    }

    @Override
    public Stream<String> readLinesStream(Path inputFilePath) {

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFilePath.toFile()));
            return bufferedReader.lines();
        } catch (IOException e) {
            logger.error("Error", e);
            return null;
        }
    }

    @Override
    public void writeStreamToFile(Stream<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(this.getOutFile()))))) {

            lines.forEach(line -> {
                try {
                    bw.write(line);
                    bw.newLine();
                } catch (IOException e) {
                    logger.error("Error writing to file", e);
                } catch (UncheckedIOException e) {
                    logger.error("Error with line " + line);
                }
            });

            // Flush and close the BufferedWriter after writing
            bw.flush();
        } catch (IOException e) {
            logger.error("Error creating BufferedWriter", e);
        }
    }


}
