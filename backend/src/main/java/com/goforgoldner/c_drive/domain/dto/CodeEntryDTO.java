package com.goforgoldner.c_drive.domain.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;

import java.time.LocalDateTime;

public class CodeEntryDTO {
	private Long id;
	private String code;

	@JsonBackReference
	private CppFileDTO cppFileDTO;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime date;

	public CodeEntryDTO(Long id, String code, LocalDateTime date) {
		this.id = id;
		this.code = code;
		this.date = date;
	}

	public CodeEntryDTO(String code, LocalDateTime date) {
		this(null, code, date);
	}

	public CodeEntryDTO() {
		this(null, "", LocalDateTime.now());
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

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public CppFileDTO getCppFileDTO() {
		return cppFileDTO;
	}

	public void setCppFileDTO(CppFileDTO cppFileDTO) {
		this.cppFileDTO = cppFileDTO;
	}
}
