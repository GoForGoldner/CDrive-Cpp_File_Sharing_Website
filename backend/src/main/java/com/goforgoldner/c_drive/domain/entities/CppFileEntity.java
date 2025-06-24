package com.goforgoldner.c_drive.domain.entities;

import jakarta.persistence.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain object for a CppFile. This class' purpose is focused on organizing a
 * C++ file and being
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

  @OneToMany(mappedBy = "cppFileEntity", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<CodeEntryEntity> codeEntries;

  @Transient
  private Path compilePath;

  public CppFileEntity() {
    this(null, "", new ArrayList<>());
  }

  public CppFileEntity(String filename) {
    this(null, filename, new ArrayList<>());
  }

  public CppFileEntity(String filename, List<CodeEntryEntity> code) {
    this(null, filename, code);
  }

  public CppFileEntity(Long id, String filename, List<CodeEntryEntity> code) {
    this.id = id;
    this.filename = filename;
    this.codeEntries = code;
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

  public List<CodeEntryEntity> getCodeEntries() {
    return this.codeEntries;
  }

  public void setCodeEntries(List<CodeEntryEntity> code) {
    this.codeEntries = code;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public void addCodeEntry(CodeEntryEntity codeEntry) {
    codeEntries.add(codeEntry);
    codeEntry.setCppFileEntity(this);
  }

  public CodeEntryEntity newestCodeEntry() {
    codeEntries.sort((a, b) -> {
      return b.getDate().compareTo(a.getDate());
    });

    return codeEntries.getFirst();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    CppFileEntity other = (CppFileEntity) obj;
    return filename.equals(other.filename) && codeEntries.equals(other.codeEntries);
  }
}
