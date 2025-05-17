package com.goforgoldner.c_drive.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
  private Long id;
  private String username;
  @JsonProperty("cpp_files")
  private List<CppFileDTO> cppFiles;
  private String password;

  public UserDTO() {
    this("", new ArrayList<>());
  }

  public UserDTO(String username, List<CppFileDTO> cppFiles) {
    this.username = username;
    this.cppFiles = cppFiles;
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

  public List<CppFileDTO> getCppFiles() {
    return cppFiles;
  }

  public void setCppFiles(List<CppFileDTO> cppFiles) {
    this.cppFiles = cppFiles;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
