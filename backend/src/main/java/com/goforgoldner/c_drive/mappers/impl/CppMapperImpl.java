package com.goforgoldner.c_drive.mappers.impl;

import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CppMapperImpl implements Mapper<CppFileEntity, CppFileDTO> {
    private final ModelMapper modelMapper;

    public CppMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CppFileDTO mapTo(CppFileEntity cppFileEntity) {
        return modelMapper.map(cppFileEntity, CppFileDTO.class);
    }

    @Override
    public CppFileEntity mapFrom(CppFileDTO cppFileDto) {
        return modelMapper.map(cppFileDto, CppFileEntity.class);
    }
}
