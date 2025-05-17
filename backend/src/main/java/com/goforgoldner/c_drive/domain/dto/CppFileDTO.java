package com.goforgoldner.c_drive.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Data transfer object for CppFiles. */
public class CppFileDTO {

  private Long id;

  private String filename;

  @JsonProperty("source_code")
  private String code;

  @JsonProperty("user_id")
  private Long userId;

  public CppFileDTO() {
    this(0L, "", "");
  }

  public CppFileDTO(String filename, String code) {
    this(0L, filename, code);
  }

  public CppFileDTO(Long id, String filename, String code) {
    this.id = id;
    this.filename = filename;
    this.code = code;
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

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
