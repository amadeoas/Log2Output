package uk.co.bocaditos.log2xlsx.in.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import org.junit.Test;

import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * JUnit tests for class FilesInput.
 */
public class FilesInputTest {

	@SuppressWarnings("resource")
	@Test
	public void processTest() throws FormatException, InputException {
		assertThrows(InputException.class, () -> {
			new FilesInput();
		});
	}

	@Test
	public void validTest() {
		final String[][] filenamess = {
				null, new String[0], {".none.x_xx"}
			};

		for (final String[] filenames : filenamess) {
			assertFalse(FilesInput.isValid(filenames));
		}
	}

	@Test
	public void test() throws InputException, IOException, CmdException {
		final String filename = "src/test/resources/logs/app1.log";

		try (final FilesInput in = (FilesInput) Input.build(null, filename)) {
			assertEquals("FILES", in.getId());
			assertEquals(filename, in.getFilename());
			assertEquals(0, in.getLineNum());
			while (in.readLine() != null) {}
			assertNull(in.readLine());

			in.close();
			assertNull(in.getFilename());
		}

		assertThrows(InputException.class, () -> {
				try (final FilesInput in = new FilesInput("")) {
					// Nothing to do
				}
			});
		assertThrows(InputException.class, () -> {
				try (final FilesInput in = new FilesInput("src/test/resources")) {
					// Nothing to do
				}
			});
	}

	@Test
	public void exceptionTest() throws InputException, IOException {
		final String filename = "src/test/resources/logs/app1.log";

		assertThrows(InputException.class, () -> {
				try (final FilesInput in = (FilesInput) Input.build(null, filename, "")) {
					while (in.readLine() != null) {}
				}
			});
	}

} // end class FilesInputTest
