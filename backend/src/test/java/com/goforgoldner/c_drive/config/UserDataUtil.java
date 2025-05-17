package com.goforgoldner.c_drive.config;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

public class UserDataUtil {
  public static UserEntity newTestUserEntity() {
    return new UserEntity("testUser", newTestUserFiles());
  }

  public static CppFileEntity newTestCppFileEntity() {

    return new CppFileEntity(
        2L,
        "test.cpp",
        "#include <iostream>\n"
            + "\n"
            + "int main() {\n"
            + "    std::cout << \"Minimal C++ program with cout!\" << std::endl;\n"
            + "    return 0;\n"
            + "}");
  }

  public static UserEntity newTestPartialUserEntity() {
    UserEntity partialTestUserUpdate = new UserEntity();
    partialTestUserUpdate.setUsername("Brad");
    return partialTestUserUpdate;
  }

  public static List<CppFileEntity> newTestUserFiles() {
    List<CppFileEntity> testUserFiles = new ArrayList<>();
    testUserFiles.add(
        new CppFileEntity(
            "helloWorld.cpp",
            "\n"
                + "#include <iostream>\n"
                + "\n"
                + "int main() {\n"
                + "    std::cout << \"Hello, World!\" << std::endl;\n"
                + "    std::cout << \"This is a simple C++ program using cout.\" << std::endl;\n"
                + "    std::cout << \"cout is used for standard output in C++.\" << std::endl;\n"
                + "    \n"
                + "    return 0;\n"
                + "}"));

    testUserFiles.add(cppFileEntityToRemove());
    return testUserFiles;
  }

  public static CppFileEntity cppFileEntityToRemove() {
    return new CppFileEntity(
        "numberPrinter.cpp",
        "#include <iostream>\n"
            + "\n"
            + "int main() {\n"
            + "    // Print numbers from 1 to 5\n"
            + "    std::cout << \"Printing numbers from 1 to 5:\" << std::endl;\n"
            + "    \n"
            + "    for (int i = 1; i <= 5; i++) {\n"
            + "        std::cout << \"Number: \" << i << std::endl;\n"
            + "    }\n"
            + "    \n"
            + "    // Print a simple calculation\n"
            + "    int sum = 10 + 15;\n"
            + "    std::cout << \"The sum of 10 and 15 is: \" << sum << std::endl;\n"
            + "    \n"
            + "    return 0;\n"
            + "}");
  }
}
