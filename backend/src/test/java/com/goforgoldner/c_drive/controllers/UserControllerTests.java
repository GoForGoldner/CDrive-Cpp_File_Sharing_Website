package com.goforgoldner.c_drive.controllers;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.config.UserDataUtil;
import com.goforgoldner.c_drive.controller.UserController;
import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.dto.UserDTO;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerTests extends AbstractTest {

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;
  private final UserController userController;
  private final Mapper<UserEntity, UserDTO> userMapper;
  private final Mapper<CppFileEntity, CppFileDTO> cppFileMapper;

  @Autowired
  public UserControllerTests(
      MockMvc mockMvc,
      UserController userController,
      Mapper<UserEntity, UserDTO> userMapper,
      Mapper<CppFileEntity, CppFileDTO> cppFileMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    this.userController = userController;
    this.userMapper = userMapper;
    this.cppFileMapper = cppFileMapper;
  }

  private UserDTO testUser;
  private CppFileDTO testFile;
  private UserDTO partialTestUserUpdate;
  private String testUserJson;
  private String partialTestUserJson;
  private String testFileJson;

  // TODO fix broken tests

  @BeforeEach
  void setUp() throws JsonProcessingException {
    testUser = userMapper.mapTo(UserDataUtil.newTestUserEntity());
    testFile = cppFileMapper.mapTo(UserDataUtil.newTestCppFileEntity());
    partialTestUserUpdate = userMapper.mapTo(UserDataUtil.newTestPartialUserEntity());
    testUserJson = objectMapper.writeValueAsString(testUser);
    partialTestUserJson = objectMapper.writeValueAsString(partialTestUserUpdate);
    testFileJson = objectMapper.writeValueAsString(testFile);
  }

  @Test
  public void addUser_returnsCorrectJson() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJson))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value(testUser.getUsername()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.user.cpp_files").isArray());
  }

  @Test
  public void addUser_returnsCorrectHttpStatus() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void getUser_returnsCorrectUser_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}", addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(addedEntity.getUsername()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files.length()").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[1].id").isNumber())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].filename").value("helloWorld.cpp"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[1].filename").value("numberPrinter.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].source_code").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[1].source_code").exists())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].user_id").value(addedEntity.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[1].user_id").value(addedEntity.getId()));
  }

  @Test
  public void getUser_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}", addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void getUser_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}", 999L))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void getUserFiles_returnsCorrectUserFiles_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}/files", addedEntity.getId()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].filename").value("helloWorld.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].source_code").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$[0].user_id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].filename").value("numberPrinter.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].source_code").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$[1].user_id").value(1));
  }

  @Test
  public void getUserFiles_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}/files", addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void getUserFiles_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/user/{id}/files", 999L))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void partialUpdateUser_returnsCorrectUser_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/user/{id}", addedEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialTestUserJson))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.username")
                .value(partialTestUserUpdate.getUsername()));
  }

  @Test
  public void partialUpdateUser_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/user/{id}", addedEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialTestUserJson))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void partialUpdateUser_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/user/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(partialTestUserJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void deleteUser_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/user/{id}", addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void deleteUser_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/user/{id}", 999L))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void userAddCppFile_returnsCorrectUser_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/user/{id}/files", addedEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFileJson))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(addedEntity.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(addedEntity.getUsername()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files.length()").value(3))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[1].id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[2].id").isNumber())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].filename").value("helloWorld.cpp"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[1].filename").value("numberPrinter.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[2].filename").value("test.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].source_code").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[1].source_code").exists())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[2].source_code").exists())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].user_id").value(addedEntity.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[1].user_id").value(addedEntity.getId()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[2].user_id").value(addedEntity.getId()));
  }

  @Test
  public void userAddCppFile_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/user/{id}/files", addedEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFileJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void userAddCppFile_returnsCorrectHttpStatus_whenIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/user/{id}/files", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFileJson))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void userRemoveCppFile_returnsCorrectUser_whenIdIsForUserIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                "/api/user/{userId}/files/{fileId}",
                addedEntity.getId(),
                addedEntity.getCppFiles().getFirst().getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(addedEntity.getUsername()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files.length()").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].id").isNumber())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].filename").value("numberPrinter.cpp"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.cpp_files[0].source_code").exists())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.cpp_files[0].user_id").value(addedEntity.getId()));
  }

  @Test
  public void userRemoveCppFile_returnsCorrectHttpStatus_whenIdIsValid() throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                "/api/user/{userId}/files/{fileId}",
                addedEntity.getId(),
                addedEntity.getCppFiles().getFirst().getId()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void userRemoveCppFile_returnsCorrectHttpStatus_whenIdIsForUserIsInvalid()
      throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                "/api/user/{userId}/files/{fileId}",
                999L,
                addedEntity.getCppFiles().getFirst().getId()))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void userRemoveCppFile_returnsCorrectHttpStatus_whenIdIsForFileIsInvalid()
      throws Exception {
    UserDTO addedEntity = userController.addUser(testUser).getBody().getUserDTO();
    assertNotNull(addedEntity);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(
                "/api/user/{userId}/files/{fileId}", addedEntity.getId(), 999L))
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }
}
