package com.goforgoldner.c_drive.dao.impl;

import com.goforgoldner.c_drive.dao.ICppFileDao;
import com.goforgoldner.c_drive.domain.CppFile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class CppFileDaoImpl implements ICppFileDao {

  public static class CppFileRowMapper implements RowMapper<CppFile> {

    @Override
    public CppFile mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new CppFile(rs.getString("filename"), rs.getString("source_code"));
    }
  }

  private final CppFileRowMapper cppFileRowMapperInstance;
  private final JdbcTemplate jdbcTemplate;

  public CppFileDaoImpl(final JdbcTemplate jdbcTemplate) {
    this.cppFileRowMapperInstance = new CppFileRowMapper();
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void add(CppFile cppFile) {
    jdbcTemplate.update(
        "INSERT INTO cpp_source_files (filename, source_code) VALUES (?, ?)",
        cppFile.getFileName(),
        cppFile.getCode());
  }

  @Override
  public Optional<CppFile> find(String fileName) {
    List<CppFile> cppFileList =
        jdbcTemplate.query(
            "SELECT filename, source_code FROM cpp_source_files WHERE filename = ? LIMIT 1",
            cppFileRowMapperInstance,
            fileName);

    return cppFileList.stream().findFirst();
  }

  @Override
  public void update(String fileName, String newSourceCode) {
    jdbcTemplate.update(
        "UPDATE cpp_source_files SET filename = ?, source_code = ? WHERE filename = ?",
        fileName,
        newSourceCode,
        fileName);
  }

  @Override
  public void delete(String fileName) {
    jdbcTemplate.update("DELETE FROM cpp_source_files WHERE filename = ?", fileName);
  }
}
