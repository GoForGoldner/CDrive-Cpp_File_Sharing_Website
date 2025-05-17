package com.goforgoldner.c_drive.service.impl;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.repositories.CppFileRepository;
import com.goforgoldner.c_drive.service.CppFileService;
import org.springframework.stereotype.Service;

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
    public boolean cppFileExists(Long id) {
        return cppFileRepository.existsById(id);
    }

    @Override
    public CppFileEntity partialUpdateCppFile(Long id, CppFileEntity cppFileEntity) {
        return cppFileRepository.findById(id)
                .map(existingCppFileEntity -> {
                    Optional.ofNullable(cppFileEntity.getFilename()).ifPresent(existingCppFileEntity::setFilename);
                    Optional.ofNullable(cppFileEntity.getCode()).ifPresent(existingCppFileEntity::setCode);
                    return cppFileRepository.save(existingCppFileEntity);
                })
                .orElseThrow(() -> new RuntimeException("CppFile doesn't exist."));
    }
}
