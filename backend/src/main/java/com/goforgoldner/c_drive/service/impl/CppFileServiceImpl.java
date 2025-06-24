package com.goforgoldner.c_drive.service.impl;

import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.repositories.CppFileRepository;
import com.goforgoldner.c_drive.service.CppFileService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CppFileServiceImpl implements CppFileService {

  private final CppFileRepository cppFileRepository;

  public CppFileServiceImpl(CppFileRepository cppFileRepository) {
    this.cppFileRepository = cppFileRepository;
  }

  @Override
  public CppFileEntity createCppFile(CppFileEntity cppFileEntity) {
    return cppFileRepository.save(cppFileEntity);
  }

  @Override
  public Optional<CppFileEntity> getCppFile(Long id) {
    return cppFileRepository.findById(id);
  }

  @Override
  public void deleteCppFile(Long id) {
    cppFileRepository.deleteById(id);
  }

  @Override
  public CppFileEntity partialUpdateCppFile(Long id, CppFileEntity partialCppFileEntity) {
    return cppFileRepository.findById(id)
        .map(existingCppFileEntity -> {
          Optional.ofNullable(partialCppFileEntity.getFilename()).ifPresent(existingCppFileEntity::setFilename);

          // If the partial C++ file had code entries
          if (partialCppFileEntity.getCodeEntries() != null) {
            List<CodeEntryEntity> codeEntries = partialCppFileEntity.getCodeEntries();
            // Remove all the old code entries
            existingCppFileEntity.getCodeEntries().clear();

            for (CodeEntryEntity codeEntry : codeEntries) {
              // Add the new code entry to the existing list
              existingCppFileEntity.addCodeEntry(codeEntry);
            }
          }

          return cppFileRepository.save(existingCppFileEntity);
        })
        .orElseThrow(() -> new RuntimeException("CppFile doesn't exist."));
  }

  @Override
  public boolean cppFileExists(Long id) {
    return cppFileRepository.existsById(id);
  }

  @Override
  public Optional<CppFileEntity> saveCppFile(CppFileEntity cppFileEntity) {
    // Return a empty optional if the C++ file isn't in the database
    if (!cppFileExists(cppFileEntity.getId()))
      return Optional.empty();

    return Optional.of(cppFileRepository.save(cppFileEntity));
  }

}
