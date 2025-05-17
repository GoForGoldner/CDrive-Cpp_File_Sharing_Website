package com.goforgoldner.c_drive.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * A helper class designed to compile C++ files. This class has public methods to both compile and
 * execute C++ files.
 */
public class CppCompilerService {

  /**
   * Compiles C++ code and returns the path to the executable.
   *
   * @param sourceCode The C++ source code to compile.
   * @return Path to the compiled executable, or null if compilation failed.
   */
  public static Path compileCode(String sourceCode) {
    try {
      Path tempDir = Files.createTempDirectory("cpp_compile");
      Path sourceFile = tempDir.resolve("source.cpp");
      Files.writeString(sourceFile, sourceCode);
      Path outputFile = tempDir.resolve("output.exe");

      boolean success = executeCompiler(sourceFile, outputFile);

      return success ? outputFile : null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Generates a thread to call an executable file and return the output into a '.txt' file.
   * Currently, there is a 5-second maximum on the time of the thread to prevent issues with input
   * methods like "cin" causing the thread to wait infinitely.
   *
   * @param executable Path to an exe / executable file.
   * @return A path to a '.txt' file, or null if the executable failed.
   */
  public static Path executeFile(Path executable) {
    // Create a new thread
    try {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      // Add a new thread
      Future<Path> output = executor.submit(new ExecutableCallable(executable));

      // Get the output from the executable and wait a maximum of 5 seconds.
      return output.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Compiles a file using g++.
   *
   * @param sourceFile The path to the C++ source file.
   * @param outputFile The path to the output executable file.
   * @return A boolean that represents if the process exited successfully.
   * @throws IOException An I/O error occurs.
   * @throws InterruptedException This thread was blocked by another thread.
   */
  private static boolean executeCompiler(Path sourceFile, Path outputFile)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = createCompilerProcess(sourceFile, outputFile);
    Process process = processBuilder.start();

    captureProcessOutput(process);

    int exitCode = process.waitFor();
    return exitCode == 0;
  }

  /**
   * Creates a proper ProcessBuilder with the compilation statement and error stream redirected into
   * the standard output.
   *
   * @param sourceFile The path to the C++ source file.
   * @param outputFile The path to the output executable file.
   * @return A ProcessBuilder that can run the compilation process.
   */
  private static ProcessBuilder createCompilerProcess(Path sourceFile, Path outputFile) {
    ProcessBuilder processBuilder =
        new ProcessBuilder("g++", sourceFile.toString(), "-o", outputFile.toString());
    processBuilder.redirectErrorStream(true);
    return processBuilder;
  }

  /**
   * Gets any output information from standard output when the file is being compiled.
   *
   * @param process The process that is handling compilation of a file.
   * @throws IOException An I/O error occurs.
   */
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

  /**
   * A class that contains a Callable function for executing a file. Currently, the class can only
   * handle inputs (no using 'std::cin').
   *
   * @param executable A path to the exe / executable file.
   */
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
