package com.goforgoldner.c_drive.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goforgoldner.c_drive.domain.dto.CodeEntryDTO;
import com.goforgoldner.c_drive.repositories.CppFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser
public class CppFileControllerTests extends AbstractTest {

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private CppFileRepository cppFileRepository;

  // Attributes inside database values
  private CppFileEntity cppFileEntryInDatabase;
  private final String codeInDatabase;
  private final LocalDateTime dateInDatabase;

  // Attributes for saving and creating a new C++ file
  private final String nameForTest;
  private final LocalDateTime dateForTest;
  private final String codeForTest;
  private final CppFileDTO cppFileDtoForTest;
  private final String cppFileJsonForTest;

  // Attributes for partially updating a C++ file
  private final String newFilenameForTest;
  private final CppFileEntity partialCppFileEntityForTest;
  private final String partialCppFileEntityForTestJson;
  private final CppFileEntity updatedCppFileWithNewName;

  @Autowired
  public CppFileControllerTests(MockMvc mockMvc, CppFileRepository cppFileRepository)
      throws Exception {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.cppFileRepository = cppFileRepository;

    // Inside database attributes
    this.codeInDatabase =
        "// Your First C++ Program\n"
            + "\n"
            + "#include <iostream>\n"
            + "int main() {\n"
            + "    std::cout << \"Hello World!\";\n"
            + "    return 0;\n"
            + "}";
    this.dateInDatabase = LocalDateTime.of(2025, 6, 15, 11, 40, 2);

    // Attributes for saving and creating a new C++ file
    this.nameForTest = "test.cpp";
    this.dateForTest = LocalDateTime.of(2025, 6, 15, 0, 3);
    this.codeForTest =
        "#include <iostream>\\n\\nint main() {\\n    // Simple cout demonstration\\n    std::cout << \\\"Hello, World!\\\" << std::endl;\\n    std::cout << \\\"This is a simple C++ program using only cout.\\\" << std::endl;\\n    \\n    // Printing numbers\\n    std::cout << \\\"The value of 5 + 7 is: \\\" << 5 + 7 << std::endl;\\n    \\n    // Printing multiple items on one line\\n    std::cout << \\\"C++ \\\" << \\\"is \\\" << \\\"powerful!\\\" << std::endl;\\n    \\n    return 0;\\n}";
    this.cppFileDtoForTest =
        new CppFileDTO(
            nameForTest, new ArrayList<>(List.of(new CodeEntryDTO(codeForTest, dateForTest))));
    this.cppFileJsonForTest = objectMapper.writeValueAsString(cppFileDtoForTest);

    // Attributes for partially updating a C++ file
    this.newFilenameForTest = "newFileName.cpp";

    this.partialCppFileEntityForTest = new CppFileEntity();
    this.partialCppFileEntityForTest.setFilename(newFilenameForTest);
    this.partialCppFileEntityForTestJson =
        objectMapper.writeValueAsString(partialCppFileEntityForTest);

    this.updatedCppFileWithNewName = new CppFileEntity();
  }

  @BeforeEach
  public void before() {
    cppFileEntryInDatabase = new CppFileEntity("hello.cpp");

    CodeEntryEntity codeEntry = new CodeEntryEntity(this.codeInDatabase, this.dateInDatabase);
    codeEntry.setCppFileEntity(cppFileEntryInDatabase);
    cppFileEntryInDatabase.getCodeEntries().add(codeEntry);

    cppFileRepository.save(cppFileEntryInDatabase);
  }

  @Test
  public void createCppFile_returnsCorrectJson() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cppFileJsonForTest))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value(nameForTest));
  }

  @Test
  public void createCppFile_returnsCorrectHttpStatus() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cppFileJsonForTest))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void getCppFileById_returnsCorrectJson_whenIdIsValid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/cpp-files/1").contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value("hello.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.source_code[0].code").value(codeInDatabase))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.source_code[0].date")
                .value(dateInDatabase.toString()));
  }

  @Test
  public void getCppFileById_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/cpp-files/1").contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void getCppFileById_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/cpp-files/999")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void deleteCppFile_returnsCorrectHttpStatus_whenFileIsPresent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/cpp-files/1")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void deleteCppFile_returnsCorrectHttpStatus_whenFileIsntPresent() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/api/cpp-files/999")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void partialUpdateCppFile_returnsCorrectJson_whenIdIsValid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/cpp-files/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialCppFileEntityForTestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$.filename").value(newFilenameForTest));
  }

  @Test
  public void partialUpdateCppFile_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/cpp-files/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialCppFileEntityForTestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void partialUpdateCppFile_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/cpp-files/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialCppFileEntityForTestJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void addCppFileCode_returnsCorrectJson_whenIdIsValid() throws Exception {
    String newCode = "This code has been updated!";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files/1/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCode))
        .andDo(MockMvcResultHandlers.print())
        // Adds the newest version to the top of the version history
        .andExpect(MockMvcResultMatchers.jsonPath("$.source_code[0].code").value(newCode))
        .andExpect(MockMvcResultMatchers.jsonPath("$.source_code[0].date").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.source_code[1].code").value(codeInDatabase))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.source_code[1].date")
                .value(dateInDatabase.toString()));
  }

  @Test
  public void addCppFileCode_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    String newCode = "This code has been updated!";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files/1/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCode))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void addCppFileCode_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    String newCode = "This code has been updated!";

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/cpp-files/999/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newCode))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
