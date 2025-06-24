package com.goforgoldner.c_drive.domain.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

/** Data transfer object for CppFiles. */
public class CppFileDTO {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @Id
  private Long id;

  private String filename;

  @JsonManagedReference
  @JsonProperty("source_code")
  private List<CodeEntryDTO> codeEntries;

  //@JsonBackReference
  @JsonProperty("user_id")
  private Long userId;

  public CppFileDTO() {
    this(null, "", new ArrayList<>());
  }

  public CppFileDTO(String filename, List<CodeEntryDTO> codeEntries) {
    this(null, filename, codeEntries);
  }

  public CppFileDTO(Long id, String filename, List<CodeEntryDTO> codeEntries) {
    this.id = id;
    this.filename = filename;
    this.codeEntries = codeEntries;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<CodeEntryDTO> getCodeEntries() {
    return codeEntries;
  }

  public void setCodeEntries(List<CodeEntryDTO> codeEntries) {
    this.codeEntries = codeEntries;
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

  public void addCodeEntry(CodeEntryDTO codeEntryDTO) {
    codeEntries.add(codeEntryDTO);
    codeEntryDTO.setCppFileDTO(this);
  }
}
