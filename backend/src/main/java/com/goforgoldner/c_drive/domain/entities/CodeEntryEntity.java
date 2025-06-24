package com.goforgoldner.c_drive.domain.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "code_entries")
public class CodeEntryEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TEXT")
  private String code;

  private LocalDateTime date;

  @ManyToOne(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "cpp_file_id")
  private CppFileEntity cppFileEntity;

  public CodeEntryEntity() {
    this("", LocalDateTime.now());
  }

  public CodeEntryEntity(String code) {
    this(code, LocalDateTime.now());
  }

  public CodeEntryEntity(String code, LocalDateTime date) {
    this.code = code;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime saveDate) {
    this.date = saveDate;
  }

  public CppFileEntity getCppFileEntity() {
    return cppFileEntity;
  }

  public void setCppFileEntity(CppFileEntity cppFileEntity) {
    this.cppFileEntity = cppFileEntity;
  }
}
