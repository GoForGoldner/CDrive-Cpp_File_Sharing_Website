package com.goforgoldner.c_drive.controller;

import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.dto.LoginCredentialsDTO;
import com.goforgoldner.c_drive.domain.dto.LoginRequestDTO;
import com.goforgoldner.c_drive.domain.dto.UserDTO;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import com.goforgoldner.c_drive.service.CppFileService;
import com.goforgoldner.c_drive.service.UserService;
import com.goforgoldner.c_drive.service.impl.JWTService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api")
public class UserController {
  private final UserService userService;
  private final CppFileService cppFileService;
  private final Mapper<UserEntity, UserDTO> userMapper;
  private final Mapper<CppFileEntity, CppFileDTO> cppFileMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JWTService jwtService;

  public UserController(
      Mapper<UserEntity, UserDTO> userMapper,
      UserService userService,
      CppFileService cppFileService,
      Mapper<CppFileEntity, CppFileDTO> cppFileMapper,
      JWTService jwtService) {
    this.userMapper = userMapper;
    this.cppFileService = cppFileService;
    this.userService = userService;
    this.cppFileMapper = cppFileMapper;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    this.jwtService = jwtService;
  }

  // TODO change to /sign-in
  @PostMapping("/user")
  public ResponseEntity<LoginRequestDTO> addUser(@RequestBody UserDTO userDto) {
    // Map user from the data object
    UserEntity userEntity = userMapper.mapFrom(userDto);

    // Convert encrypt password
    if (userEntity.getPassword() == null || userEntity.getPassword().isEmpty())
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));

    // Clear the current userEntity list (should be empty)
    userEntity.getCppFiles().clear();
    for (CppFileDTO cppFileDTO : userDto.getCppFiles()) {
      // Map the cpp file from the data object
      CppFileEntity cppFileEntity = cppFileMapper.mapFrom(cppFileDTO);
      // Add the new file to the userEntity
      userEntity.addCppFile(cppFileEntity);
    }

    // Save the user to the database
    UserEntity newUserEntity = userService.createUser(userEntity);
    // Convert the saved user to a database object
    UserDTO newUserDTO = userMapper.mapTo(newUserEntity);
    // Create a new JWT Token for the user
    String newJwtToken = jwtService.generateToken(newUserDTO.getUsername());

    return new ResponseEntity<>(new LoginRequestDTO(newUserDTO, newJwtToken), HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<LoginRequestDTO> loginUser(@RequestBody LoginCredentialsDTO loginCredentialsDTO) {
    String username = loginCredentialsDTO.getUsername();
    String rawPassword = loginCredentialsDTO.getPassword();

    // Map user from the data object
    Optional<UserEntity> userEntity = userService.getUserByUsername(username);

    return userEntity
        .map(
            entity -> {
              if (userService.verifyUser(username, rawPassword)) {
                String jwtToken = jwtService.generateToken(entity.getUsername());
                UserDTO foundUserDTO = userMapper.mapTo(entity);
                System.out.println("Returning status OK because the user was found and verified!");
                return new ResponseEntity<>(
                    new LoginRequestDTO(foundUserDTO, jwtToken), HttpStatus.OK);
              } else {
                return new ResponseEntity<LoginRequestDTO>(HttpStatus.NOT_FOUND);
              }
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long id) {
    Optional<UserEntity> userEntity = userService.getUserById(id);
    return userEntity
        .map(entity -> new ResponseEntity<>(userMapper.mapTo(entity), HttpStatus.OK))
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/user/{id}/files")
  public ResponseEntity<List<CppFileDTO>> getUserFiles(@PathVariable("id") Long id) {
    Optional<UserEntity> userEntity = userService.getUserById(id);
    return userEntity
        .map(
            entity -> {
              UserDTO userDto = userMapper.mapTo(entity);
              return new ResponseEntity<>(userDto.getCppFiles(), HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PatchMapping("/user/{id}")
  public ResponseEntity<UserDTO> partialUpdateUser(
      @PathVariable("id") Long id, @RequestBody UserDTO userDto) {
    UserEntity userEntity = userMapper.mapFrom(userDto);
    Optional<UserEntity> updatedUserEntity = userService.partialUpdateUser(id, userEntity);
    return entityToResponse(updatedUserEntity, HttpStatus.OK);
  }

  @DeleteMapping("/user/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/user/{id}/files")
  public ResponseEntity<UserDTO> userAddCppFile(
      @PathVariable("id") Long id, @RequestBody CppFileDTO cppFileDto) {
    CppFileEntity cppFileEntity = cppFileMapper.mapFrom(cppFileDto);
    Optional<UserEntity> userEntity = userService.addCppFile(id, cppFileEntity);
    return entityToResponse(userEntity, HttpStatus.CREATED);
  }

  @DeleteMapping("/user/{userId}/files/{fileId}")
  public ResponseEntity<UserDTO> userRemoveCppFile(
      @PathVariable("userId") Long userId, @PathVariable("fileId") Long fileId) {
    Optional<CppFileEntity> cppFileEntity = cppFileService.getCppFile(fileId);

    if (cppFileEntity.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    Optional<UserEntity> userEntity = userService.removeCppFile(userId, cppFileEntity.get());

    return entityToResponse(userEntity, HttpStatus.NO_CONTENT);
  }

  private ResponseEntity<UserDTO> entityToResponse(
      Optional<UserEntity> userEntity, HttpStatus httpStatus) {
    return userEntity
        .map(
            entity -> {
              UserDTO updatedUserDTO = userMapper.mapTo(entity);
              return new ResponseEntity<>(updatedUserDTO, httpStatus);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
