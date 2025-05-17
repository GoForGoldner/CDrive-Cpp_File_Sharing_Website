package com.goforgoldner.c_drive.domain.entities;

import com.goforgoldner.c_drive.service.CppCompilerService;
import jakarta.persistence.*;

import java.nio.file.Path;

/**
 * Domain object for a CppFile. This class' purpose is focused on organizing a C++ file and being
 * able to easily compile and execute a file.
 */
@Entity
@Table(name = "cpp_source_files")
public class CppFileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  private String filename;

  @Column(name = "source_code", columnDefinition = "TEXT")
  private String code;

  @Transient private Path compilePath;

  public CppFileEntity() {
    this(0L, "", "");
  }

  public CppFileEntity(String filename, String code) {
    this(0L, filename, code);
  }

  public CppFileEntity(Long id, String filename, String code) {
    this.id = id;
    this.filename = filename;
    this.code = code;
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFilename() {
    return this.filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  /**
   * Compiles the source-code into a executable file using g++.
   *
   * @return A path to an executable file
   */
  public Path compile() {
    this.compilePath = CppCompilerService.compileCode(this.code);
    return this.compilePath;
  }

  /**
   * Compiles the C++ code from the source code if it's not been compiled, then runs the executable
   * file created.
   *
   * @return A path to a txt file that has the outputted data.
   */
  public Path executeCode() {
    // Compile the file if it's never been compiled successfully
    if (compilePath == null) this.compile();
    // If the compilation still wasn't successful return.
    if (compilePath == null) return null;

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

    CppFileEntity other = (CppFileEntity) obj;
    return filename.equals(other.filename) && code.equals(other.code);
  }
}
