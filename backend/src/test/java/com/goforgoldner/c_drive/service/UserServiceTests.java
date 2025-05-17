package com.goforgoldner.c_drive.service;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.config.UserDataUtil;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTests extends AbstractTest {

  private final UserService underTest;

  @Autowired
  public UserServiceTests(UserService userService) {
    this.underTest = userService;
  }

  private UserEntity testUser;
  private CppFileEntity testFile;
  private CppFileEntity testFileToRemove;
  private List<CppFileEntity> testUserFiles;
  private UserEntity partialTestUserUpdate;

  @BeforeEach
  void setUp() {
    testUser = UserDataUtil.newTestUserEntity();
    testFile = UserDataUtil.newTestCppFileEntity();
    testFileToRemove = UserDataUtil.cppFileEntityToRemove();
    testUserFiles = UserDataUtil.newTestUserFiles();
    partialTestUserUpdate = UserDataUtil.newTestPartialUserEntity();
  }

  @Test
  public void createUser_returnsCorrectUserEntity() {

    UserEntity result = underTest.createUser(testUser);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals(testUser.getUsername(), result.getUsername());
    assertEquals(testUser.getCppFiles(), result.getCppFiles());
  }

  @Test
  @Transactional
  public void getUser_returnsCorrectUserByIdEntity_whenIdIsValid() {
    underTest.createUser(testUser);

    Optional<UserEntity> result = underTest.getUserById(1L);

    result.ifPresentOrElse(
        userEntity -> {
          assertEquals(1L, userEntity.getId());
          assertEquals(testUser.getUsername(), userEntity.getUsername());
          assertEquals(testUser.getCppFiles(), userEntity.getCppFiles());
        },
        () -> fail("User couldn't be found."));
  }

  @Test
  public void getUser_ById_returnsEmptyOptional_whenIdIsInvalid() {
    Optional<UserEntity> result = underTest.getUserById(999L);

    result.ifPresent(userEntity -> fail("User entity was found"));
  }

  @Test
  @Transactional
  public void getUserFiles_returnsCorrectUserByIdFiles_whenIdIsValid() {
    underTest.createUser(testUser);

    List<CppFileEntity> result = underTest.getUserFiles(1L);

    assertEquals(testUserFiles, result);
  }

  @Test
  public void getUserByIdFiles_returnsEmptyList_whenIdIsInvalid() {
    List<CppFileEntity> result = underTest.getUserFiles(999L);

    assertTrue(result.isEmpty());
  }

  @Test
  public void partialUpdateUser_returnsUpdatedUser_whenIdIsValid() {
    underTest.createUser(testUser);
    assertNotEquals(partialTestUserUpdate.getUsername(), testUser.getUsername());

    Optional<UserEntity> userEntity = underTest.partialUpdateUser(1L, partialTestUserUpdate);

    userEntity.ifPresentOrElse(
        entity -> {
          assertEquals(partialTestUserUpdate.getUsername(), entity.getUsername());
        },
        () -> fail("User wasn't found."));
  }

  @Test
  public void partialUpdateUser_returnsEmptyOptional_whenIdIsInvalid() {
    Optional<UserEntity> userEntity = underTest.partialUpdateUser(999L, partialTestUserUpdate);

    assertFalse(userEntity.isPresent());
  }

  @Test
  public void deleteUser_whenIdIsValid() {
    underTest.createUser(testUser);

    assertTrue(underTest.getUserById(1L).isPresent());

    underTest.deleteUser(1L);

    assertFalse(underTest.getUserById(1L).isPresent());
  }

  @Test
  public void deleteUser_whenIdIsInvalid() {
    underTest.deleteUser(999L);

    assertFalse(underTest.getUserById(999L).isPresent());
  }

  @Test
  @Transactional
  public void addCppFile_addsCppFileToUser_whenIdIsValid_andFileIsPresent() {
    underTest.createUser(testUser);
    Optional<UserEntity> userEntity = underTest.addCppFile(1L, testFile);

    userEntity.ifPresentOrElse(
        entity -> {
          assertTrue(entity.getCppFiles().contains(testFile));
        },
        () -> fail("User wasn't found."));
  }

  @Test
  @Transactional
  public void addCppFile_returnsEmptyOptional_whenIdIsInvalid() {
    Optional<UserEntity> userEntity = underTest.addCppFile(999L, testFile);

    assertFalse(userEntity.isPresent());
  }


  @Test
  @Transactional
  public void removeCppFile_removesCppFileToUser_whenIdIsValid_andFileIsPresent() {
    underTest.createUser(testUser);
    Optional<UserEntity> userEntity = underTest.removeCppFile(1L, testFileToRemove);

    userEntity.ifPresentOrElse(
        entity -> {
          assertFalse(entity.getCppFiles().contains(testFileToRemove));
        },
        () -> fail("User wasn't found."));
  }

  @Test
  @Transactional
  public void removeCppFile_returnsEmptyOptional_whenIdIsInvalid() {
    Optional<UserEntity> userEntity = underTest.removeCppFile(999L, testFileToRemove);

    assertFalse(userEntity.isPresent());
  }

  @Test
  @Transactional
  public void removeCppFile_returnsEmptyOptional_whenFileNotPresent() {
    underTest.createUser(testUser);
    Optional<UserEntity> userEntity = underTest.removeCppFile(1L, testFile);

    userEntity.ifPresentOrElse(
            entity -> {
              assertFalse(entity.getCppFiles().contains(testFile));
            },
            () -> fail("User wasn't found."));
  }
}
