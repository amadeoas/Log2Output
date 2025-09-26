package uk.co.bocaditos.log2xlsx.out.html;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import freemarker.template.TemplateException;
import uk.co.bocaditos.log2xlsx.Application;
import uk.co.bocaditos.log2xlsx.in.FormatsTest;
import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.log2xlsx.out.OutException;
import uk.co.bocaditos.log2xlsx.out.xlsx.XlsxOutput;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;


/**
 * JUnit tests for class HtmlOutput.
 */
public class HtmlOutputTest {

	@Test
	public void exceptionsTest() throws UtilsException {
		final String[] args = {
				CmdArgs.START + Application.CMD_FORMATS,	 "src/test/resources/formats.txt",
				CmdArgs.START + Application.CMD_ID_FIELD_NAME, "id",
				CmdArgs.START + Input.CMD_LOGS, 			 "xxx.log",
				CmdArgs.START + HtmlOutput.CMD_OUT,			 "testOut.html",
				CmdArgs.START + HtmlOutput.CMD_DIR4TEMPLATE, "src/main/resources" + HtmlOutput.DEFAULT_DIR4TEMPLATE,
				// Table sizes
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Datetime", 	"190px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Computer", 	"120px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Class", 	"auto",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Id", 		"60px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Request", 	"auto",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Headers", 	"190px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Body", 		"450px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Message",	"200px",
				CmdArgs.START + LogOutput.CMD_HEADERS_SORT,				"datetime,computer,class," 
					+ "id,message,request,headers,body",
				CmdArgs.START + HtmlOutput.CMD_HTML_MAX_CELL_LENGTH, "24"
		};
		final CmdArgs cmdArgs = new CmdArgs(args);
		final LogSet set = FormatsTest.load();
		final FieldsSet fields = FormatsTest.load(null, set);
		final HtmlOutput out = new HtmlOutput("Log2Output", "0.00.000") {

				private int count = 0; 


				@Override
				Writer buildWrite(final String filename) throws Exception {
					if (++this.count == 1) {
						throw new IOException("Testing!");
					}

					throw new TemplateException(null);
				}

			};

		assertThrows(UtilsException.class, () -> {
				out.write(cmdArgs, fields);
			});
		assertThrows(UtilsException.class, () -> {
			out.write(cmdArgs, fields);
		});
	}

	@Test
	public void test() throws UtilsException {
		final LogSet set = FormatsTest.load();
		final String[] args = {
				CmdArgs.START + Application.CMD_FORMATS,	 "src/test/resources/formats.txt",
				CmdArgs.START + Application.CMD_ID_FIELD_NAME, "id",
				CmdArgs.START + Input.CMD_LOGS, 			 "xxx.log",
				CmdArgs.START + HtmlOutput.CMD_OUT,			 "testOut.html",
				CmdArgs.START + HtmlOutput.CMD_DIR4TEMPLATE, "src/main/resources" + HtmlOutput.DEFAULT_DIR4TEMPLATE,
				// Table sizes
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Datetime", 	"190px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Computer", 	"120px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Class", 	"auto",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Id", 		"60px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Request", 	"auto",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Headers", 	"190px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Body", 		"450px",
				CmdArgs.START + HtmlOutput.CMD_HTML_SIZE + "Message",	"200px",
				CmdArgs.START + LogOutput.CMD_HEADERS_SORT,				"datetime,computer,class," 
					+ "id,message,request,headers,body",
				CmdArgs.START + HtmlOutput.CMD_HTML_MAX_CELL_LENGTH, "24"
		};
		final HtmlOutput out = build(args);
		final FieldsSet fields = FormatsTest.load(null, set);
		final File file;
		CmdArgs cdmArgs;
		CmdArgs cdmArgs1;
		CmdArgs cdmArgs2;

		cdmArgs = new CmdArgs(args);
		file = new File(cdmArgs.getArgument(XlsxOutput.CMD_OUT));
		if (file.exists()) {
			file.delete();
		}
		out.write(cdmArgs, fields);
		assertTrue(file.delete());

		args[9] = "src/test/resources/formats.txt";
		cdmArgs1 = new CmdArgs(args);
		assertThrows(OutException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				out.write(cdmArgs1, fields);
			}
			
		});
		args[9] = "src/test/resources/.unable.lll";
		cdmArgs2 = new CmdArgs(args);
		assertThrows(OutException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				out.write(cdmArgs2, fields);
			}
			
		});
	}

	private HtmlOutput build(final String... args) {
		return new HtmlOutput(XlsxOutput.DEFAULT_APP_NAME, "1.00.000");
	}

} // end class HtmlOutputTest
