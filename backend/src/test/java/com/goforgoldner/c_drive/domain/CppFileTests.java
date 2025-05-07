package com.goforgoldner.c_drive.domain;

import com.goforgoldner.c_drive.service.CppCompilerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;


public class CppFileTests {

  @Test
  public void testCompileJustInput() {
    CppFile cppFile =
        new CppFile(
            "hello.cpp",
            "// Your First C++ Program\n"
                + "\n"
                + "#include <iostream>\n"
                + "int main() {\n"
                + "    std::cout << \"Hello World!\" << std::endl;\n"
                + "    return 0;\n"
                + "}");

    Path testOutputFile = cppFile.executeCode();

    try {
      assertNotNull(testOutputFile);
      assertEquals("Hello World!" + System.lineSeparator(), Files.readString(testOutputFile));
    } catch (IOException e) {
      fail("IO exception with Files.readString()");
    }
  }

  @Test
  public void testCompileInputAndOutput() {
    CppFile cppFile =
        new CppFile(
            "cinTest.cpp",
            "#include <iostream>\n"
                + "\n"
                + "int main() {\n"
                + "    int number;\n"
                + "    \n"
                + "    std::cout << \"Enter a number: \";\n"
                + "    std::cin >> number;\n"
                + "    \n"
                + "    std::cout << \"You entered: \" << number << std::endl;\n"
                + "    std::cout << \"Double of your number is: \" << number * 2 << std::endl;\n"
                + "    \n"
                + "    return 0;\n"
                + "}");

    Path testOutputFile = cppFile.executeCode();

    assertNull(testOutputFile);
  }
}
