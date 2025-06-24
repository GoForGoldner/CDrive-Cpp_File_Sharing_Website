package com.goforgoldner.c_drive.domain.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String username;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<CppFileEntity> cppFiles;

  @Column(nullable = false)
  private String password;

  public UserEntity() {
    this(null, "", new ArrayList<>());
  }

  public UserEntity(String username, List<CppFileEntity> cppFiles) {
    this(null, username, cppFiles);
  }

  public UserEntity(Long id, String username, List<CppFileEntity> cppFiles) {
    this.id = id;
    this.username = username;
    this.cppFiles = cppFiles;
    this.password = "blank_password";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<CppFileEntity> getCppFiles() {
    return cppFiles;
  }

  public void setCppFiles(List<CppFileEntity> cppFiles) {
    this.cppFiles = cppFiles;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void addCppFile(CppFileEntity cppFileEntity) {
    cppFiles.add(cppFileEntity);
    cppFileEntity.setUser(this);
  }

  public void removeCppFile(CppFileEntity cppFileEntity) {
    cppFiles.remove(cppFileEntity);
    cppFileEntity.setUser(null);
  }
}
