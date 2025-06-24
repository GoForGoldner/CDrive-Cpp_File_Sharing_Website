package com.goforgoldner.c_drive.mappers.impl;

import com.goforgoldner.c_drive.domain.dto.CodeEntryDTO;
import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CodeEntryMapperImpl implements Mapper<CodeEntryEntity, CodeEntryDTO> {
    private final ModelMapper modelMapper;

    public CodeEntryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CodeEntryDTO mapTo(CodeEntryEntity codeEntry) {
        return modelMapper.map(codeEntry, CodeEntryDTO.class);
    }

    @Override
    public CodeEntryEntity mapFrom(CodeEntryDTO codeEntryDTO) {
        return modelMapper.map(codeEntryDTO, CodeEntryEntity.class);
    }
}
