package com.goforgoldner.c_drive.service.impl;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import com.goforgoldner.c_drive.repositories.UserRepository;
import com.goforgoldner.c_drive.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;

  public UserServiceImpl(
      UserRepository userRepository, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
  }

  @Override
  public UserEntity createUser(UserEntity userEntity) {
    // Save the user to the database
    return userRepository.save(userEntity);
  }

  @Override
  public Optional<UserEntity> getUserById(Long userId) {
    // Find the user by their id
    return userRepository.findById(userId);
  }

  @Override
  public Optional<UserEntity> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public List<CppFileEntity> getUserFiles(Long userId) {
    // Get the user by their id
    Optional<UserEntity> entity = getUserById(userId);
    // Get the C++ files from the user, or return an empty list
    return entity.map(UserEntity::getCppFiles).orElse(new ArrayList<>());
  }

  @Override
  public Optional<UserEntity> partialUpdateUser(Long id, UserEntity userEntityInfo) {
    // Get the user by their id
    Optional<UserEntity> entity = userRepository.findById(id);

    return entity
        .map(
            userEntity -> {
              // Update the field in userEntity if userEntityInfo has updated information
              Optional.ofNullable(userEntityInfo.getUsername()).ifPresent(userEntity::setUsername);

              if (userEntityInfo.getCppFiles() != null) {
                // Add the C++ files to the user
                List<CppFileEntity> cppFiles = userEntityInfo.getCppFiles();
                for (CppFileEntity file : cppFiles) {
                  // Set the user to the current user
                  file.setUser(userEntity);

                  userEntity.getCppFiles().add(file);
                }
              }

              // Save the userEntity back into the database
              return Optional.of(userRepository.save(userEntity));
            })
        .orElse(Optional.empty());
  }

  @Override
  public void deleteUser(Long userId) {
    // Delete the user
    userRepository.deleteById(userId);
  }

  @Override
  public Optional<UserEntity> addCppFile(Long userId, CppFileEntity cppFileEntity) {
    // Get the user
    Optional<UserEntity> entity = getUserById(userId);

    return entity
        .map(
            userEntity -> {
              // Add the C++ file to the user
              userEntity.addCppFile(cppFileEntity);
              // Return the user saved by the database
              return Optional.of(userRepository.save(userEntity));
            })
        .orElse(Optional.empty());
  }

  @Override
  public Optional<UserEntity> removeCppFile(Long userId, CppFileEntity cppFileEntity) {
    // Get the user
    Optional<UserEntity> entity = getUserById(userId);

    return entity
        .map(
            userEntity -> {
              // Remove the C++ file to the user
              userEntity.removeCppFile(cppFileEntity);
              // Return the user saved by the database
              return Optional.of(userRepository.save(userEntity));
            })
        .orElse(Optional.empty());
  }

  @Override
  public boolean verifyUser(String username, String password) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));

    return authentication.isAuthenticated();
  }
}
