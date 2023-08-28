package ca.jrvs.apps.grep;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class JavaGrepLambdaImp1 extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        //Use default logger config
        //BasicConfigurator.configure();

        JavaGrepLambdaImp1 javaGrepLambdaImp1 = new JavaGrepLambdaImp1();
        javaGrepLambdaImp1.setRegex(args[0]);
        javaGrepLambdaImp1.setRootPath(args[1]);
        javaGrepLambdaImp1.setOutFile(args[2]);

        try {
            javaGrepLambdaImp1.process();
        } catch (Exception ex) {
            javaGrepLambdaImp1.logger.error("Error: Unable to process", ex);
        }
    }

    @Override
    public List<File> listFiles(String rootDir) {

        try (Stream<Path> filesStream = Files.find(Paths.get(rootDir), 99,
                (path, basicFileAttributes) -> path.toFile().isFile())) {
            List<File> result = filesStream.map(Path::toFile).collect(Collectors.toList());
            return result;
        } catch (IOException e) {
            logger.error("I/O Error when accessing " + rootDir, e);
            return null;
        }
    }

    @Override
    public List<String> readLines(File inputFile) {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
            return bufferedReader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error: I/O Exception with file " + inputFile, e);
            return null;
        }
    }

}

