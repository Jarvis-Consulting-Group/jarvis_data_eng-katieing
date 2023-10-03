# Introduction
This app recursively searches a directory for lines of text that match 
a Regex search pattern and writes the matching lines to a file. 
Developed using core Java, the application harnesses Regex and Stream APIs, 
manages its dependencies and build process through Maven, 
and has been containerized with Docker for ease of distribution. 

# Quick Start

Approach 1: JAR file
```bash
#Usage
# regex_pattern: The search pattern
# root_dir: The root directory to search recursively for lines that contain the search pattern
# out_file: Output file path to contain the matched lines   
  
java -jar grep-1.0-SNAPSHOT.jar [regex_pattern] [root_dir] [out_file]
```

Approach 2: Docker
```bash
#Usage
# regex_pattern: The search pattern
# root_dir: The root directory to search recursively for lines that contain the search pattern
# out_path: The path to the directory holding the output file
# out_file: The output file name

docker run --rm \
-v [root_dir]:/data -v [out_path]:/log \
katieing/grep [regex_pattern] /data /log/[out_file]
```

# Implemenation
## Pseudocode
```
matchedlines = []
        
for file in listFiles(rootDir)
    for line in readLines(file)
        if containsPattern(line)
            matchedLines.add(line)
        
writeToFile(matchedLines)
```

## Performance Issue

As seen in the pseudocode, this application reads each line in a file 
before checking if it is a match. Therefore, there is a possibility of a large file 
exceeding the heap memory, causing an error. This issue can be solved by utilizing a single 
Stream to obtain the matched lines from the root directory. 

This implementation is included in the JAR file and can be accessed by referencing 
the `JavaGrepLambdaImp` class in the command line.

```bash
#Usage when processing directories with large files

java -cp grep-1.0-SNAPSHOT.jar \ 
ca.jrvs.apps.grep.JavaGrepLambdaImp \ 
[regex_pattern] [root_directory] [out_file]
```

# Test
This application was tested manually using sample data and comparing 
results with the expected output on various test cases. 
Testin

# Deployment
A [docker image of this app](https://hub.docker.com/repository/docker/katieing/grep/general) 
can be found on Docker Hub.

# Improvement

##### Handling files without read access ([In Progress](https://github.com/Jarvis-Consulting-Group/jarvis_data_eng-katieing/tree/feature/fileaccessreport/core_java/grep/src/main/java/ca/jrvs/apps/grep)) 
The current version of the app will exit with an error message if a file or directory is encountered 
that it does not have permission to read. This feature will allow for these files and directories to be 
skipped, so that the app can continue and a file with matched lines is written. The user 
will be shown a warning that some files were not read, along with a list of the skipped files and directories.

##### Grouping matched lines by file
Instead of writing all matched lines to a file as a whole, 
this feature will write the lines grouped by the file that they originated from, listed under a heading 
containing the file path. This would allow a user to easily locate the line within the directory. 

##### Performance improvements
Speed and memory optimizations may be made with further testing.