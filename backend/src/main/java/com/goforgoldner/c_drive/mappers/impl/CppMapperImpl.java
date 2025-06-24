package com.goforgoldner.c_drive.mappers.impl;

import com.goforgoldner.c_drive.domain.dto.CodeEntryDTO;
import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CppMapperImpl implements Mapper<CppFileEntity, CppFileDTO> {
  private final ModelMapper modelMapper;
  private final Mapper<CodeEntryEntity, CodeEntryDTO> codeEntryMapper;

  public CppMapperImpl(ModelMapper modelMapper, Mapper<CodeEntryEntity, CodeEntryDTO> codeEntryMapper) {
    this.modelMapper = modelMapper;
    this.codeEntryMapper = codeEntryMapper;
  }

  @Override
  public CppFileDTO mapTo(CppFileEntity cppFileEntity) {
    CppFileDTO cppFileDTO = modelMapper.map(cppFileEntity, CppFileDTO.class);

    cppFileDTO.getCodeEntries().clear();
    for (CodeEntryEntity codeEntry : cppFileEntity.getCodeEntries()) {
        cppFileDTO.addCodeEntry(codeEntryMapper.mapTo(codeEntry));
    }

    return cppFileDTO;
  }

  @Override
  public CppFileEntity mapFrom(CppFileDTO cppFileDto) {
    CppFileEntity cppFileEntity = modelMapper.map(cppFileDto, CppFileEntity.class);

    cppFileEntity.getCodeEntries().clear();
    for (CodeEntryDTO codeEntryDTO : cppFileDto.getCodeEntries()) {
      cppFileEntity.addCodeEntry(codeEntryMapper.mapFrom(codeEntryDTO));
    }

    return cppFileEntity;
  }
}
