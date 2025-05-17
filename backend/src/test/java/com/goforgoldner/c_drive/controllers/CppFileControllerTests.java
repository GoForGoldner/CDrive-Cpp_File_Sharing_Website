package com.goforgoldner.c_drive.controllers;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class CppFileControllerTests extends AbstractTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @Autowired
  public CppFileControllerTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
  }

  @Test
  public void testThatCreateCppFileReturnsCorrectJsonAndHTTPStatus() throws Exception {
    String code =
        "#include <iostream>\\n\\nint main() {\\n    // Simple cout demonstration\\n    std::cout << \\\"Hello, World!\\\" << std::endl;\\n    std::cout << \\\"This is a simple C++ program using only cout.\\\" << std::endl;\\n    \\n    // Printing numbers\\n    std::cout << \\\"The value of 5 + 7 is: \\\" << 5 + 7 << std::endl;\\n    \\n    // Printing multiple items on one line\\n    std::cout << \\\"C++ \\\" << \\\"is \\\" << \\\"powerful!\\\" << std::endl;\\n    \\n    return 0;\\n}";
    String name = "test.cpp";
    CppFileDTO cppFileDto = new CppFileDTO(name, code);

    String cppFileEntityJson = objectMapper.writeValueAsString(cppFileDto);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cppFileEntityJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value(name))
        .andExpect(MockMvcResultMatchers.jsonPath("$.source_code").value(code))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void testThatGetCppFileReturnsCorrectJsonAndHttpStatus() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/cpp-files/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value("hello.cpp"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.source_code")
                .value(
                    "// Your First C++ Program\n"
                        + "\n"
                        + "#include <iostream>\n"
                        + "int main() {\n"
                        + "    std::cout << \"Hello World!\";\n"
                        + "    return 0;\n"
                        + "}"));
  }
  

  @Test
  public void testThatDeleteCppFileReturnsCorrectHttpStatusOnCppFile() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/cpp-files/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void testThatDeleteCppFileReturnsCorrectHttpStatusOnNoCppFile() throws Exception {
    mockMvc
            .perform(
                    MockMvcRequestBuilders.delete("/api/cpp-files/999").contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
