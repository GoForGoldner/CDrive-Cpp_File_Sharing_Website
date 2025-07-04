package com.goforgoldner.c_drive.repositories;

import com.goforgoldner.c_drive.config.AbstractTest;
import com.goforgoldner.c_drive.domain.entities.CodeEntryEntity;
import com.goforgoldner.c_drive.domain.entities.CppFileEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CppFileEntityIntegrationTests extends AbstractTest {

	private CppFileRepository underTest;

	@Autowired
	public CppFileEntityIntegrationTests(CppFileRepository underTest) {
		this.underTest = underTest;
	}

	private final String newCppFileContents = "#include <iostream>\n"
			+ "using namespace std;\n"
			+ "\n"
			+ "int main() {\n"
			+ "    cout << \"Keep pushing forward — every bug fixed is a lesson learned!\" << endl;\n"
			+ "    return 0;\n"
			+ "}";

	private final String newCppFileName = "test.cpp";

	private final CppFileEntity newCppFileEntity = new CppFileEntity(newCppFileName);

	private final String newSourceCode = "#include <iostream>\n"
			+ "int main() {\n"
			+ "    std::cout << \"Updated Code!\";\n"
			+ "    return 0;\n"
			+ "}";

	@Test
	public void testIntegration() {
		// Add a file
		underTest.save(newCppFileEntity);
		// Find the file
		assertEquals(newCppFileEntity.getId(), underTest.findById(newCppFileEntity.getId()).get().getId());
		assertEquals(newCppFileEntity.getFilename(), underTest.findById(newCppFileEntity.getId()).get().getFilename());

		// Verify the file history is empty
		assertTrue(underTest.findById(newCppFileEntity.getId()).get().getCodeEntries().isEmpty());

		// Add a version to version history
		CodeEntryEntity toAdd = new CodeEntryEntity(newCppFileContents);
		toAdd.setCppFileEntity(newCppFileEntity);
    	newCppFileEntity.getCodeEntries().add(toAdd);

		// Update the file
		underTest.save(newCppFileEntity);
		// Find the file
		assertEquals(newCppFileEntity.getId(), underTest.findById(newCppFileEntity.getId()).get().getId());
		assertEquals(newCppFileEntity.getFilename(), underTest.findById(newCppFileEntity.getId()).get().getFilename());


		// Verify the file history isn't empty
		assertFalse(underTest.findById(newCppFileEntity.getId()).get().getCodeEntries().isEmpty());

		// Delete the file
		underTest.deleteById(newCppFileEntity.getId());
		// Find the file
		assertFalse(underTest.findById(newCppFileEntity.getId()).isPresent());
	}
}
