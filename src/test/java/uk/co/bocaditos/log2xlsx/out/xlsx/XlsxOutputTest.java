package uk.co.bocaditos.log2xlsx.out.xlsx;

import static org.junit.Assert.assertThrows;

import java.io.File;

import org.junit.Test;

import uk.co.bocaditos.log2xlsx.in.FormatsTest;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;


/**
 * JUnit tests for class XlsxOutput.
 */
public class XlsxOutputTest {

	@Test
	public void writeTest() throws UtilsException {
		final LogSet set = FormatsTest.load();
		final String[] args = {
				CmdArgs.START + LogOutput.CMD_OUT, 			"testOut.xlsx",
				CmdArgs.START + LogOutput.CMD_HEADERS_SORT,	"datetime,computer,class,id,message," 
				+ "request,headers,body"
			};
		final XlsxOutput out = build(args);
		final FieldsSet fields = FormatsTest.load(null, set);
		final CmdArgs cdmArgs = new CmdArgs(args);

		final File file = new File(cdmArgs.getArgument(LogOutput.CMD_OUT));

		if (file.exists()) {
			file.delete();
		}

		out.write(cdmArgs, fields);
		if (file.exists()) {
			file.delete();
		}

		args[1] = ".";
		final CmdArgs cdmArgs1 = new CmdArgs(args);
		assertThrows(UtilsException.class, () -> {
				out.write(cdmArgs1, fields);
			});
	}

	private XlsxOutput build(final String... args) {
		return new XlsxOutput(XlsxOutput.DEFAULT_APP_NAME, XlsxOutput.DEFAULT_WORK_SHEET_NAME, 
				XlsxOutput.DEFAULT_WORK_SHEET_VERSION,
				// Headers
				XlsxOutput.DEFAULT_HEADER_FONT_NAME, XlsxOutput.DEFAULT_HEADER_FONT_SIZE, 
				XlsxOutput.DEFAULT_HEADER_FONT_COLOUR, XlsxOutput.DEFAULT_HEADER_FONT_FILL_COLOUR, 
				XlsxOutput.DEFAULT_FONT_WRAP_TXT, false,
				XlsxOutput.DEFAULT_HEADER_BORDER_STYLE,
				// Rows
				XlsxOutput.DEFAULT_FONT_NAME, XlsxOutput.DEFAULT_FONT_SIZE, 
				XlsxOutput.DEFAULT_FONT_COLOUR, XlsxOutput.DEFAULT_FONT_FILL_COLOUR, 
				XlsxOutput.DEFAULT_FONT_WRAP_TXT, true,
				XlsxOutput.DEFAULT_BORDER_STYLE);
	}

} // end class XlsxOutputTest
