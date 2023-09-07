package ca.jrvs.apps.grep;

import org.apache.log4j.BasicConfigurator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JavaGrepLambdaHalfImp extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        BasicConfigurator.configure();

        JavaGrepLambdaHalfImp lambdaHalf = new JavaGrepLambdaHalfImp();
        lambdaHalf.setRegex(args[0]);
        lambdaHalf.setRootPath(args[1]);
        lambdaHalf.setOutFile(args[2]);
        lambdaHalf.unreadable = new ArrayList<>();

        try {
            lambdaHalf.process();
        } catch (Exception ex) {
            lambdaHalf.logger.error("Error: Unable to process", ex);
        }
    }

    public Stream<Path> listFilesStream(String rootDir) throws IOException {

        Path path = Paths.get(rootDir);
        File file = path.toFile();

        if (file.isFile()) {
            return Stream.of(path);
        } else {
            return Files.list(path)
                    .flatMap(infile -> {
                        try {
                            return listFilesStream(infile.toString());
                        } catch (IOException e) {
                            if (!infile.toFile().canRead()) {
                                unreadable.add(infile.toString());
                            } else {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                    });
        }

    }

    @Override
    public List<File> listFiles(String rootDir) throws IOException {

        return listFilesStream(rootDir).map(Path::toFile).collect(Collectors.toList());

    }

    @Override
    public List<String> readLines(File inputFile) throws IOException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            if (!inputFile.canRead()) {
                unreadable.add(inputFile.toString());
                return null;
            } else {
                throw e;
            }
        }
    }

}

