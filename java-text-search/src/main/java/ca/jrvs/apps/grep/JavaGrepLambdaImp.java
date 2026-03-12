package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class JavaGrepLambdaImp extends JavaGrepImp {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }

        JavaGrepLambdaImp app = new JavaGrepLambdaImp();
        app.setRegex(args[0]);
        app.setRootPath(args[1]);
        app.setOutFile(args[2]);

        try {
            app.process();
        } catch (Exception e) {
            app.logger.error("Error: Unable to process", e);
        }
    }

    /**
     * Stream/Lambda version of the ETL pipeline
     */
    @Override
    public void process() throws IOException {

        logger.info("Grep(Stream) started");
        logger.info("Root path: {}", getRootPath());
        logger.info("Regex: {}", getRegex());

        // E: extract files -> extract lines (stream style)
        // T: filter matched lines
        // L: write output
        List<String> matchedLines = listFiles(getRootPath()).stream()
                .map(File::toPath)
                // read each file lines as a stream (safe, avoids open-stream leak)
                .flatMap(path -> readLines(path).stream())
                .filter(this::containsPattern)
                .sorted()
                .toList();

        writeToFile(matchedLines);

        logger.info("Total files scanned: {}", listFiles(getRootPath()).size());
        logger.info("Total matched lines written: {}", matchedLines.size());
        logger.info("Output file: {}", getOutFile());
        logger.info("Grep(Stream) finished");
    }

    /**
     * Stream-based recursive file listing using Files. Walk
     */
    @Override
    public List<File> listFiles(String rootDir) {
        Path root = new File(rootDir).toPath();

        if (!Files.exists(root)) {
            throw new IllegalArgumentException("Root path does not exist: " + rootDir);
        }

        try (Stream<Path> paths = Files.walk(root)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
            logger.error("Failed to list files under: {}", rootDir, e);
            return List.of();
        }
    }

    /**
     * Read file lines (returns List<String> because JavaGrep interface requires it)
     * Internally works nicely with stream pipeline in process().
     */
    private List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", path, e);
            return List.of();
        }
    }

    /**
     * Stream-friendly writing (no explicit loop here)
     */
    @Override
    public void writeToFile(List<String> lines) throws IOException {
        if (lines == null) {
            throw new IllegalArgumentException("lines is null");
        }

        Path outPath = new File(getOutFile()).toPath();
        Path parent = outPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        Files.write(outPath, lines);

        if (lines.isEmpty()) {
            logger.warn("No matched lines found");
        }
    }
}
