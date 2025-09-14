package uk.co.bocaditos.log2xlsx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import uk.co.bocaditos.log2xlsx.in.filter.FieldFilter;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.log2xlsx.out.html.HtmlOutput;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CdmHelp;
import uk.co.bocaditos.utils.cmd.CmdArgs;


/**
 * JUnit tests for class Application.
 */
@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
@ActiveProfiles("test")
public class ApplicationTest {

	private final String OUT_FILE = "testOut.html";


	@Test
	public void mainTest() throws UtilsException {
		final String[] args = {
				CmdArgs.START + Application.ARG_FORMATS,		"src/test/resources/formats.txt",
				CmdArgs.START + LogOutput.ARG_OUT,				OUT_FILE,
				CmdArgs.START + HtmlOutput.ARG_DIR4TEMPLATE,	"src/main/resources" + HtmlOutput.DEFAULT_DIR4TEMPLATE,
				CmdArgs.START + Application.ARG_ID_FIELD_NAME,	"id",
				CmdArgs.START + Application.ARG_LOGS,			"src/test/resources/logs"
		};

		Application.main(args);

		args[3] = "testOut.xlsx";
		Application.main(args);
		assertTrue(new File(OUT_FILE).delete());
	}

	@Test
	public void mainFilterTest() throws IOException {
		final String[] args = {
				CmdArgs.START + Application.ARG_FORMATS,		"src/test/resources/formats.txt",
				CmdArgs.START + LogOutput.ARG_OUT,				OUT_FILE,
				CmdArgs.START + HtmlOutput.ARG_DIR4TEMPLATE,	"src/main/resources" + HtmlOutput.DEFAULT_DIR4TEMPLATE,
				CmdArgs.START + Application.ARG_ID_FIELD_NAME,	"id",
				CmdArgs.START + Application.ARG_LOGS,			"src/test/resources/logs",
				CmdArgs.START + FieldFilter.ARG_FILTER,			"datetime", 
																"20241223 10:11:10.206", 
																"20241223 10:11:10.212"
		};

		Application.main(args);
		assertEquals(421, readFileLines(OUT_FILE));

		args[args.length - 1] = "99991231 23:59:59.999";
		Application.main(args);
		assertEquals(461, readFileLines(OUT_FILE));

		final String[] args1 = new String[args.length - 1];

		for (int index = 0; index < args1.length; ++index) {
			args1[index] = args[index];
		}
		Application.main(args1);
		assertEquals(361, readFileLines(OUT_FILE));
	}

	@Test
	public void helpTest() {
		final String[] args = {
				CmdArgs.START + CdmHelp.PARAM_HELP
			};

		Application.main(args);
	
		final SpringApplication app = new SpringApplication(TApplication.class);
	    final ConfigurableApplicationContext ctx = app.run(args);

	    SpringApplication.exit(ctx, new ExitCodeGenerator() {

			@Override
			public int getExitCode() {
				return 0;
		    }

		});

//	    System.exit(exitCode);
	}

	private int readFileLines(final String filename) throws IOException {
		try (final BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			int numLines = 0;

			while (reader.readLine() != null) {
				++numLines;
			}

			assertTrue(new File(filename).delete());

			return numLines;
		}
	}

} // end class ApplicationTest


/**
 * Test class to allow checking the built help.
 */
class TApplication extends Application {

	@Override
	public void run(final String... args) throws UtilsException {
		final CmdArgs cmdArgs = new CmdArgs(this.env, args);

		assertTrue(help_(cmdArgs).startsWith("java -jar Log2Output-1."));
	}

} // end class TApplication
