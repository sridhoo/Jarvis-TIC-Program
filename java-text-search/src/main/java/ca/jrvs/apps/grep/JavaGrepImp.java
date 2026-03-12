package ca.jrvs.apps.grep;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outFile;
    private Pattern pattern;


    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        JavaGrepImp app = new JavaGrepImp();
        app.setRegex(args[0]);
        app.setRootPath(args[1]);
        app.setOutFile(args[2]);

        try {
            app.process();
        } catch (Exception e) {
            app.logger.error("Error: Unable to process", e);
        }
    }

    @Override
    public void process() throws IOException {

        // matchedLines is like "filtered records" in ETL
        List<String> results = new java.util.ArrayList<>();

        // 1) get all files under rootPath
        List<File> files = listFiles(getRootPath());

        // 2) read each file, filter lines
        for (File file : files) {
            List<String> lines = readLines(file);

            for (String line : lines) {
                if (containsPattern(line)) {
                    results.add(line);
                }
            }
        }

        // 3) write all matched lines
        Collections.sort(results);
        writeToFile(results);
        logger.info("Total files found: {}", files.size());
        logger.info("Grep started");
        logger.info("Root path: " + getRootPath());
        logger.info("Regex: " + getRegex());
        logger.info("Total files scanned: " + files.size());
        logger.info("Grep finished");



    }
    private void listFilesRecursively(File dir, List<File> result) {
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }

        for (File f : children) {
            if (f.isFile()) {
                result.add(f);
            } else if (f.isDirectory()) {
                listFilesRecursively(f, result);
            }
        }
    }




    @Override
    public List<File> listFiles(String rootDir) {
        File root = new File(rootDir);

        if (!root.exists()) {
            throw new IllegalArgumentException("Root path does not exist: " + rootDir);
        }

        List<File> result = new ArrayList<>();
        listFilesRecursively(root, result);
        return result;
    }



    @Override
    public List<String> readLines(File inputFile) {
        if (inputFile == null || !inputFile.isFile()) {
            throw new IllegalArgumentException("Input is not a file: " + inputFile);
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            // log and continue (dont kill whole job for one bad file)
            logger.error("Failed to read file: " + inputFile.getPath(), e);
        }

        return lines;
    }


    @Override
    public boolean containsPattern(String line) {
        return line != null && pattern.matcher(line).find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {
        if (lines == null) {
            throw new IllegalArgumentException("lines is null");
        }

        File out = new File(getOutFile());
        File parent = out.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }

        logger.info("Total matched lines written: " + lines.size());
        logger.info("Output file: " + out.getPath());
        if (lines.isEmpty()) {
            logger.warn("No matched lines found");
        }

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
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
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
