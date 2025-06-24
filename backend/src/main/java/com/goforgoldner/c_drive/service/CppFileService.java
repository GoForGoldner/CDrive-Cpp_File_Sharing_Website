package com.goforgoldner.c_drive.service;

import java.util.Optional;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;

/**
 * CppFileService
 */
public interface CppFileService {

  /**
   * Creates a C++ file in the database with the passed in values. Note that the
   * id passed into the method is irrelevant and will be replaced with a valid id
   * from the database.
   * 
   * @param cppFileEntity - A C++ file to save to the database
   * @return The C++ file saved to the database
   */
  CppFileEntity createCppFile(CppFileEntity cppFileEntity);

  /**
   * Finds a C++ file in the database with the corresponding id.
   * 
   * @param id - The id of the C++ file in the database
   * @return The C++ file found, or empty if not present
   */
  Optional<CppFileEntity> getCppFile(Long id);

  /**
   * Deletes a C++ file with the corresponding id from the database.
   * 
   * @param id - The id of the C++ file to delete
   */
  void deleteCppFile(Long id);

  /**
   * Updates a C++ file with any updated or passed in values from the given C++
   * file
   * 
   * @param id            - The id of the file to be updated
   * @param cppFileEntity - The C++ file containing attributes to update
   * @return The updated C++ file from the database
   */
  CppFileEntity partialUpdateCppFile(Long id, CppFileEntity cppFileEntity);

  /**
   * @param id - The id of the C++ file
   * @return - A boolean representing whether the file exists
   */
  public boolean cppFileExists(Long id);

  /**
   * Saves an already present C++ file into the database.
   * 
   * @param cppFileEntity - The C++ file with updated values into the database
   * @return - The saved C++ file from the database, or an empty if id isn't
   *         present in the database
   */
  Optional<CppFileEntity> saveCppFile(CppFileEntity cppFileEntity);
}
