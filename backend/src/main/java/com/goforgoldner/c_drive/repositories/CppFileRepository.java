package com.goforgoldner.c_drive.repositories;

import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CppFileRepository extends CrudRepository<CppFileEntity, Long> {}
