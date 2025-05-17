package com.goforgoldner.c_drive.controller;

import com.goforgoldner.c_drive.domain.dto.CppFileDTO;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import com.goforgoldner.c_drive.mappers.Mapper;
import com.goforgoldner.c_drive.service.CppFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api")
public class CppFileController {

  private final CppFileService cppFileService;
  private final Mapper<CppFileEntity, CppFileDTO> cppFileMapper;

  public CppFileController(
      CppFileService cppFileService, Mapper<CppFileEntity, CppFileDTO> cppFileMapper) {
    this.cppFileService = cppFileService;
    this.cppFileMapper = cppFileMapper;
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
    // Create a new response entity with the object if it was found, or say that it was not found.
    return cppFileService
        .getCppFile(id)
        .map(
            cppFileEntity -> {
              CppFileDTO temp = cppFileMapper.mapTo(cppFileEntity);
              return new ResponseEntity<>(temp, HttpStatus.OK);
            })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  // Prob not working
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
    if (!cppFileService.cppFileExists(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    CppFileEntity cppFileEntity = cppFileMapper.mapFrom(cppFileDto);
    CppFileEntity updatedCppFileEntity = cppFileService.partialUpdateCppFile(id, cppFileEntity);

    System.out.println("Sent updated user back to the frontend");
    return new ResponseEntity<>(cppFileMapper.mapTo(updatedCppFileEntity), HttpStatus.OK);
  }
}
