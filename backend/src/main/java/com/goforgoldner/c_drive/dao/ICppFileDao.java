package com.goforgoldner.c_drive.dao;

import com.goforgoldner.c_drive.domain.CppFile;

import java.util.Optional;

public interface ICppFileDao {
    void add(CppFile cppFile);

    Optional<CppFile> find(String fileName);

    void update(String fileName, String newSourceCode);

    void delete(String fileName);
}
