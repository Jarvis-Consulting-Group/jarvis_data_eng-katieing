package ca.jrvs.apps.grep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface JavaGrep {

    /**
     * Top level search workflow
     * @throws IOException
     */
    void process() throws IOException;

    /**
     * Traverse a given directory and return all files
     * @param rootDir input directory
     * @return files under the rootDir
     */
    List<File> listFiles(String rootDir) throws IOException;

    /**
     * Traverse a given directory and return all files
     * @param rootDir input directory
     * @return stream of files
     */
    Stream<Path> listFilesStream(String rootDir) throws IOException;

    /**
     * Read a file and return all of the lines
     *
     * Explain FileReader, BufferedReader, and character encoding ????
     *
     * @param inputFile file to be read
     * @return lines
     * @throws IllegalArgumentException if a given inputFile is not a file
     */
    List<String> readLines(File inputFile) throws IOException;

    /**
     * Read a file and return all of the lines
     *
     * Explain FileReader, BufferedReader, and character encoding ????
     *
     * @param inputFilePath file path to be read
     * @return lines
     * @throws IllegalArgumentException if a given inputFile is not a file
     */
    Stream<String> readLinesStream(Path inputFilePath) throws IOException;

    /**
     * Check if a line contains the regex pattern (passed by user)
     * @param line input string
     * @return true if there is a match
     */
    boolean containsPattern(String line);

    /**
     * Write lines to a file
     *
     * Explore: FileOutputStream, OutputStreamWriter, and BufferedWriter
     *
     * @param lines matched line
     * @throws IOException if write failed
     */
    void writeToFile(List<String> lines) throws IOException;

    /**
     * Write lines to a file
     *
     * Explore: FileOutputStream, OutputStreamWriter, and BufferedWriter
     *
     * @param lines matched line
     * @throws IOException if write failed
     */
    void writeStreamToFile(Stream<String> lines) throws IOException;

    String getRootPath();

    void setRootPath(String rootPath);

    String getRegex();

    void setRegex(String regex);

    String getOutFile();

    void setOutFile(String outFile);

}
