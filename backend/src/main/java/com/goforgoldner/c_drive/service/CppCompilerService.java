package com.goforgoldner.c_drive.service;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.*;

/**
 * A helper class designed to compile C++ files. This class has public methods
 * to both compile and
 * execute C++ files.
 */
@Service
public class CppCompilerService {

	private final Map<String, Process> runningProcesses = new ConcurrentHashMap<>();

	/**
	 * Generates a thread to call an executable file and return the output into a
	 * '.txt' file.
	 * Currently, there is a 5-second maximum on the time of the thread to prevent
	 * issues with input
	 * methods like "cin" causing the thread to wait infinitely.
	 *
	 * @return A path to a '.txt' file, or null if the executable failed.
	 */
	public String executeFile(
			CppFileEntity cppFileEntity, String sessionId, SimpMessagingTemplate message) {

		// Create a new thread
		try {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			// Add a new thread
			Future<String> output = executor.submit(
					new ExecutableAndCompileCallable(
							cppFileEntity, runningProcesses, sessionId, message));

			// Get the output from the executable and wait a maximum of 5 seconds.
			return output.get(50, TimeUnit.SECONDS);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Sends a message to the terminal.
	 * @param sessionId - A long that is an id of a websocket connection
	 * @param line - A string contains a message
	 */
	public void sendMessageToTerminal(String sessionId, String line) {
		if (!runningProcesses.containsKey(sessionId))
			return;

		Process process = runningProcesses.get(sessionId);
		Thread messageThread = new SendMessageToProcessThread(process, line);
		messageThread.start();
	}

	/**
	 * A thread class that writes the output from a process, and sends the message to the session id.
	 */
	private static class ProcessInputThread extends Thread {
		private final Process process;
		private final String sessionId;
		private final SimpMessagingTemplate message;

		public ProcessInputThread(Process process, String sessionId, SimpMessagingTemplate message) {
			this.process = process;
			this.sessionId = sessionId;
			this.message = message;
		}

		@Override
		public void run() {
			sendProcessOutput();
		}

		/**
		 * Reads and converts any output from the process and sends it as a message to the websocket connection with sessionId.
		 */
		private void sendProcessOutput() {
			String translatedCompilerOutputDestination = "/queue/compiler-output-user" + sessionId;
			String translatedErrorDestination = "/queue/errors" + sessionId;

			try {
				InputStream is = process.getInputStream();

				byte[] buffer = new byte[1024];
				int bytesRead;

				while (process.isAlive() || is.available() > 0) {
					if (is.available() > 0) {
						bytesRead = is.read(buffer, 0, Math.min(is.available(), buffer.length));
						if (bytesRead > 0) {
							String output = new String(buffer, 0, bytesRead,
									StandardCharsets.UTF_8);
							System.out.println("Read output: " + output);
							message.convertAndSend(translatedCompilerOutputDestination, output);
						}
					} else {
						// Small sleep to prevent CPU spinning
						Thread.sleep(50);
					}
				}
			} catch (IOException | InterruptedException e) {
				if (e instanceof InterruptedException) {
					Thread.currentThread().interrupt();
				}
				message.convertAndSend(translatedErrorDestination,
						"Error reading process output: " + e.getMessage());
			}
		}
	}

	/**
	 * A thread class that writes any line as input into a process
	 */
	private static class SendMessageToProcessThread extends Thread {
		private final Process process;
		private final String line;

		public SendMessageToProcessThread(Process process, String line) {
			this.process = process;
			this.line = line;
		}

		@Override
		public void run() {
			sendMessage(line);
		}

		/**
		 * Sends a message into a process.
		 * @param line - A string containing any message
		 */
		private void sendMessage(String line) {
			// Write the line to the running process
			try {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(process.getOutputStream()));
				writer.write(line);
				writer.newLine();
				writer.flush();
			} catch (IOException _) {
			}
		}
	}

	/**
	 * A class that contains a Callable function for executing a file.
	 */
	private static class ExecutableAndCompileCallable implements Callable<String> {

		private final Map<String, Process> runningProcesses;
		private final String sessionId;
		private final SimpMessagingTemplate message;

		private final Path tempDir;
		private final Path sourceFile;
		private final Path outputFile;

		// Initialize temporary files and directory
		{
			try {
				// Create a directory for compiling the C++ file
				tempDir = Files.createTempDirectory("cpp_compile");
				// Create a temporary source file
				sourceFile = tempDir.resolve("source.cpp");
				// Create a temporary executable file
				outputFile = tempDir.resolve("output.exe");
			} catch (IOException e) {
				throw new RuntimeException("The temporary files or directory couldn't be created.");
			}
		}

		private ExecutableAndCompileCallable(
				CppFileEntity cppFileEntity,
				Map<String, Process> runningProcesses,
				String sessionId,
				SimpMessagingTemplate message)
				throws RuntimeException {
			this.runningProcesses = runningProcesses;
			this.sessionId = sessionId;
			this.message = message;

			// Write the source_code into the source file
			try {
				Files.writeString(sourceFile, cppFileEntity.newestCodeEntry().getCode());
			} catch (IOException e) {
				throw new RuntimeException("The source code couldn't be written into the file.");
			}
		}

		// TODO figure out what I'm actually returning
		@Override
		public String call() throws Exception {

			// Create a process to compile the C++ file
			Process compilationProcess = compilationProcessBuilder();

			// Create a thread to run the compilation and send its response to the client
			Thread compilationInputThread = new ProcessInputThread(compilationProcess, sessionId, message);
			// Start the thread
			compilationInputThread.start();
			// Wait for the thread to end
			compilationInputThread.join();

			// Create a process to execute the compiled file
			Process executionProcess = executionProcessBuilder();

			// Add the process to the list of currently running processes
			runningProcesses.put(sessionId, executionProcess);

			// Create a thread to run the execution and send its response to the client
			Thread executionInputThread = new ProcessInputThread(executionProcess, sessionId, message);
			executionInputThread.start();

			return "";
		}

		private Process compilationProcessBuilder() throws IOException {
			ProcessBuilder compilationProcessBuilder = new ProcessBuilder("g++", sourceFile.toString(),
					"-o", outputFile.toString());
			compilationProcessBuilder.redirectErrorStream(true);
			compilationProcessBuilder.directory(tempDir.toFile());

			return compilationProcessBuilder.start();
		}

		private Process executionProcessBuilder() throws IOException {
			ProcessBuilder executionProcessBuilder = new ProcessBuilder(outputFile.toString());
			executionProcessBuilder.redirectErrorStream(true);
			executionProcessBuilder.directory(tempDir.toFile());

			return executionProcessBuilder.start();
		}
	}
}
