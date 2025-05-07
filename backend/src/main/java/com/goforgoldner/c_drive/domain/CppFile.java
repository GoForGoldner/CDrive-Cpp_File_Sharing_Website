package com.goforgoldner.c_drive.domain;

import com.goforgoldner.c_drive.service.CppCompilerService;

import java.nio.file.Path;

public class CppFile {
  private final String fileName;
  private String code;
  private Path compilePath;

  public CppFile(final String fileName, String code) {
    this.fileName = fileName;
    this.code = code;
  }

  public String getFileName() {
    return this.fileName;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Path compile() {
    this.compilePath = CppCompilerService.compileCode(this.code);
    return this.compilePath;
  }

  public Path executeCode() {
    if (compilePath == null) this.compile();

    try {
      return CppCompilerService.executeFile(compilePath);
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    CppFile other = (CppFile) obj;
    return fileName.equals(other.fileName) && code.equals(other.code);
  }
}
