package com.goforgoldner.c_drive.service;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;


import java.util.List;
import java.util.Optional;

public interface UserService {
    /**
     * Creates a user from the userDto given and adds it to the database.
     * @param userEntity The json request-body for the user.
     * @return The information about the user that was added to the database.
     */
    UserEntity createUser(UserEntity userEntity);

    /**
     * Finds the information the database has about a user.
     * @param userId The primary key for the user.
     * @return The information about the user from the database, or empty if not present.
     */
    Optional<UserEntity> getUserById(Long userId);

    /**
     * Gets a user by their username from the database.
     * @param username - A String representing the username of a user
     * @return The user from the database, or empty if not found
     */
    Optional<UserEntity> getUserByUsername(String username);

    /**
     * Gets the C++ files the user added to the database.
     * @param userId The primary key for the user.
     * @return The list of files that the user has in their C++ files.
     */
    List<CppFileEntity> getUserFiles(Long userId);

    /**
     * Updates a user's information with any fields that we're given in the request-body.
     * @param id The primary key of the user.
     * @param userEntity The information to update the user with.
     * @return The user with the updated information.
     */
    Optional<UserEntity> partialUpdateUser(Long id, UserEntity userEntity);

    /**
     * Deletes a user from the database.
     * @param userId The id of the user.
     */
    void deleteUser(Long userId);

    /**
     * Adds a C++ file to the user.
     * @param userId The primary key for the user.
     * @param cppFileEntity The file that is being added to the user's files.
     * @return An updated user with the file being added to their list of files.
     */
    Optional<UserEntity> addCppFile(Long userId, CppFileEntity cppFileEntity);

    /**
     * Removes a C++ file from the user, and deletes it from the database.
     * @param userId The primary key for the user.
     * @param cppFileEntity The C++ file that should be removed from the user.
     * @return The updated user with the remove C++ file.
     */
    Optional<UserEntity> removeCppFile(Long userId, CppFileEntity cppFileEntity);

    /**
     * Verifies that a user is registered in the database.
     * @param username - A String representing username of the user
     * @param password - A String representing the password of the user
     * @return Whether the user is in the database
     */
    boolean verifyUser(String username, String password);
}
