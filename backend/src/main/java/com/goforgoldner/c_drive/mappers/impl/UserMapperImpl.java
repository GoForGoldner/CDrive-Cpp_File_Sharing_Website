package com.goforgoldner.c_drive.mappers.impl;

import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.dto.UserDTO;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDTO> {

    public final ModelMapper modelMapper;
    private final Mapper<CppFileEntity, CppFileDTO> cppFileMapper;

    public UserMapperImpl(ModelMapper modelMapper, Mapper<CppFileEntity, CppFileDTO> cppFileMapper) {
        this.modelMapper = modelMapper;
        this.cppFileMapper = cppFileMapper;
    }

    @Override
    public UserDTO mapTo(UserEntity userEntity) {
        UserDTO userDTO = modelMapper.map(userEntity, UserDTO.class);

        userDTO.getCppFiles().clear();
        for (CppFileEntity cppFileEntity : userEntity.getCppFiles()) {
            userDTO.addCppFile(cppFileMapper.mapTo(cppFileEntity));
        }

        return userDTO;
    }

    @Override
    public UserEntity mapFrom(UserDTO userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        userEntity.getCppFiles().clear();
        for (CppFileDTO cppFileDTO : userDto.getCppFiles()) {
            userEntity.addCppFile(cppFileMapper.mapFrom(cppFileDTO));
        }

        return userEntity;
    }
}
