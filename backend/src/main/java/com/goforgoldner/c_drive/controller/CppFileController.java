package com.goforgoldner.c_drive.controller;

import com.goforgoldner.c_drive.domain.dto.CodeEntryDTO;
import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import com.goforgoldner.c_drive.service.CppFileService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class CppFileController {

  private final CppFileService cppFileService;
  private final Mapper<CppFileEntity, CppFileDTO> cppFileMapper;
  private final Mapper<CodeEntryEntity, CodeEntryDTO> codeEntryMapper;

  public CppFileController(
          CppFileService cppFileService, Mapper<CppFileEntity, CppFileDTO> cppFileMapper, Mapper<CodeEntryEntity, CodeEntryDTO> codeEntryMapper) {
    this.cppFileService = cppFileService;
    this.cppFileMapper = cppFileMapper;
    this.codeEntryMapper = codeEntryMapper;
  }

  @PostMapping("/cpp-files")
  public ResponseEntity<CppFileDTO> createCppFile(@RequestBody CppFileDTO cppFileDto) {
    // Convert the data object into a database object
    CppFileEntity cppFileEntity = cppFileMapper.mapFrom(cppFileDto);

    // Save the database object to our database
    CppFileEntity savedCppFileEntity = cppFileService.createCppFile(cppFileEntity);

    // Return the newly saved database object as a data object
    return new ResponseEntity<>(cppFileMapper.mapTo(savedCppFileEntity), HttpStatus.CREATED);
  }

  @GetMapping("/cpp-files/{id}")
  public ResponseEntity<CppFileDTO> getCppFileById(@PathVariable Long id) {
    // Create a new response entity with the object if it was found, or say that it
    // was not found.
    return cppFileService
        .getCppFile(id)
        .map(
            cppFileEntity -> {
              CppFileDTO temp = cppFileMapper.mapTo(cppFileEntity);
              return new ResponseEntity<>(temp, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/cpp-files/{id}")
  public ResponseEntity<?> deleteCppFile(@PathVariable Long id) {

    // Delete the entity cpp file
    cppFileService.deleteCppFile(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PatchMapping("/cpp-files/{id}")
  public ResponseEntity<CppFileDTO> partialUpdateCppFile(
      @PathVariable("id") Long id, @RequestBody CppFileDTO cppFileDto) {
    System.out.println("Partial update called.");

    // Return not found if the CppFile doesn't currently exist
    if (!cppFileService.cppFileExists(id))
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    CppFileEntity cppFileEntity = cppFileMapper.mapFrom(cppFileDto);
    CppFileEntity updatedCppFileEntity = cppFileService.partialUpdateCppFile(id, cppFileEntity);

    System.out.println("Sent updated user back to the frontend");
    return new ResponseEntity<>(cppFileMapper.mapTo(updatedCppFileEntity), HttpStatus.OK);
  }

  @PostMapping("/cpp-files/{id}/code")
  public ResponseEntity<CppFileDTO> addCppFileCode(@PathVariable("id") Long id,
      @RequestBody String updatedCode) {
    Optional<CppFileEntity> optionalCppFileEntity = cppFileService.getCppFile(id);

    // Return not found if the service can't find the C++ file
    if (optionalCppFileEntity.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    // Get the CppFile from the service
    CppFileEntity cppFileEntity = optionalCppFileEntity.get();

    List<CodeEntryEntity> cppFileHistory = cppFileEntity.getCodeEntries();

    // Create a new code entry
    CodeEntryEntity newCodeEntry = new CodeEntryEntity(updatedCode, LocalDateTime.now());
    newCodeEntry.setCppFileEntity(cppFileEntity);
    // Add the new code to the C++ file history with the current date
    cppFileHistory.addFirst(newCodeEntry);

    // Ensure that the file history of the database doesn't exceed 20 versions
    while (cppFileHistory.size() > 20) cppFileHistory.removeLast();

    // Save the file with the updated list to the database
    Optional<CppFileEntity> updatedCppFileEntity = cppFileService.saveCppFile(cppFileEntity);

    if (updatedCppFileEntity.isEmpty())
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    CppFileDTO updatedCppFileDTO = cppFileMapper.mapTo(updatedCppFileEntity.get());

    // Return the Updated CppFile Entity
    return new ResponseEntity<>(updatedCppFileDTO, HttpStatus.OK);
  }
}
