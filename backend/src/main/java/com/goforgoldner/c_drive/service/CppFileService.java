package com.goforgoldner.c_drive.service;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;

import java.util.Optional;

public interface CppFileService {
    CppFileEntity createCppFile(CppFileEntity cppFileEntity);

    Optional<CppFileEntity> getCppFile(Long id);

    void deleteCppFile(Long id);

    CppFileEntity partialUpdateCppFile(Long id, CppFileEntity cppFileEntity);

    public boolean cppFileExists(Long id);
}
