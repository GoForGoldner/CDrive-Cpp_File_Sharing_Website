package com.goforgoldner.c_drive.dao.impl;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.domain.CppFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CppFileDaoImplTests extends AbstractTest {

  // For mock tests
  private JdbcTemplate mockJdbcTemplate;
  private CppFileDaoImpl underTestWithMock;

  // For integration tests with real database
  @Autowired
  private CppFileDaoImpl underTestNoMock;

  private final String newCppFileContents =
          "#include <iostream>\n"
                  + "using namespace std;\n"
                  + "\n"
                  + "int main() {\n"
                  + "    cout << \"Keep pushing forward — every bug fixed is a lesson learned!\" << endl;\n"
                  + "    return 0;\n"
                  + "}";

  private final String newCppFileName = "test.cpp";

  private final CppFile newCppFile = new CppFile(newCppFileName, newCppFileContents);

  private final String newSourceCode =
          "#include <iostream>\n"
                  + "int main() {\n"
                  + "    std::cout << \"Updated Code!\";\n"
                  + "    return 0;\n"
                  + "}";

  @BeforeEach
  public void setUp() {
    // Create mock objects manually
    mockJdbcTemplate = mock(JdbcTemplate.class);
    underTestWithMock = new CppFileDaoImpl(mockJdbcTemplate);
  }

  @Test
  public void testAddGeneratesCorrectSQL() {
    underTestWithMock.add(newCppFile);

    verify(mockJdbcTemplate)
            .update(
                    eq("INSERT INTO cpp_source_files (filename, source_code) VALUES (?, ?)"),
                    eq("test.cpp"),
                    eq(newCppFileContents));
  }

  @Test
  public void testFindGeneratesCorrectSQL() {
    underTestWithMock.find("hello.cpp");

    verify(mockJdbcTemplate)
            .query(
                    eq("SELECT filename, source_code FROM cpp_source_files WHERE filename = ? LIMIT 1"),
                    any(CppFileDaoImpl.CppFileRowMapper.class),
                    eq("hello.cpp"));
  }

  @Test
  public void testUpdateGeneratesCorrectSQL() {
    underTestWithMock.update("hello.cpp", newSourceCode);

    verify(mockJdbcTemplate)
            .update(
                    eq("UPDATE cpp_source_files SET filename = ?, source_code = ? WHERE filename = ?"),
                    eq("hello.cpp"),
                    eq(newSourceCode),
                    eq("hello.cpp"));
  }

  @Test
  public void testDeleteGeneratesCorrectSQL() {
    underTestWithMock.delete("hello.cpp");

    verify(mockJdbcTemplate).update("DELETE FROM cpp_source_files WHERE filename = ?", "hello.cpp");
  }

  @Test
  public void testIntegration() {
    // Add a file
    underTestNoMock.add(newCppFile);
    // Find the file
    assertEquals(newCppFile, underTestNoMock.find(newCppFileName).get());

    // Update the file
    underTestNoMock.update(newCppFileName, newCppFileContents);
    // Find the file
    assertEquals(new CppFile(newCppFileName, newCppFileContents), underTestNoMock.find(newCppFileName).get());

    // Delete the file
    underTestNoMock.delete(newCppFileName);
    // Find the file
    assertFalse(underTestNoMock.find(newCppFileName).isPresent());
  }
}