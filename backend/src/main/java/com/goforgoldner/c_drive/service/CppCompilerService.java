package com.goforgoldner.c_drive.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CppCompilerService {
  /**
   * Compiles C++ code and returns the path to the executable.
   *
   * @param sourceCode The C++ source code to compile
   * @return Path to the compiled executable, or null if compilation failed
   */
  public static Path compileCode(String sourceCode) {
    try {
      Path tempDir = Files.createTempDirectory("cpp_compile");
      Path sourceFile = tempDir.resolve("source.cpp");
      Files.writeString(sourceFile, sourceCode);
      Path outputFile = tempDir.resolve("output.exe");

      boolean success = executeCompiler(sourceFile, outputFile);

      if (success) {
        return outputFile;
      } else {
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Path executeFile(Path executable) throws ExecutionException, InterruptedException, TimeoutException {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<Path> output = executor.submit(new ExecutableCallable(executable));
    return output.get(5, TimeUnit.SECONDS);
  }

  private static boolean executeCompiler(Path sourceFile, Path outputFile)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = createCompilerProcess(sourceFile, outputFile);
    Process process = processBuilder.start();

    captureProcessOutput(process);

    int exitCode = process.waitFor();
    return exitCode == 0;
  }

  private static ProcessBuilder createCompilerProcess(Path sourceFile, Path outputFile) {
    ProcessBuilder processBuilder =
        new ProcessBuilder("g++", sourceFile.toString(), "-o", outputFile.toString());
    processBuilder.redirectErrorStream(true);
    return processBuilder;
  }

  private static void captureProcessOutput(Process process) throws IOException {
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
      }
    }
  }

  private record ExecutableCallable(Path executable) implements Callable<Path> {

    @Override
    public Path call() throws TimeoutException, IOException, InterruptedException {
      ProcessBuilder processBuilder = new ProcessBuilder(executable.toString());
      processBuilder.redirectErrorStream(true);

      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      // Store the output lines
      List<String> outputLines = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        outputLines.add(line);
        System.out.println(line);
      }

      // Wait for the process to complete
      int exitCode = process.waitFor();
      System.out.println("Process exited with code: " + exitCode);

      // Save the output to a file
      Path output = Files.write(Paths.get("output.txt"), outputLines);

      System.out.println("Output saved to output.txt");

      return output;
    }
  }
}
