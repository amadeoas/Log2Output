package uk.co.bocaditos.log2xlsx.out.xlsx;

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
				CmdArgs.START + LogOutput.ARG_OUT, 			"testOut.xlsx",
				CmdArgs.START + LogOutput.ARG_HEADERS_SORT,	"datetime,computer,class,id,message," 
				+ "request,headers,body"
			};
		final XlsxOutput out = build(args);
		final CmdArgs cdmArgs = new CmdArgs(args);
		final FieldsSet fields = FormatsTest.load(set);

		final File file = new File(cdmArgs.getArgument(LogOutput.ARG_OUT));

		if (file.exists()) {
			file.delete();
		}
		out.write(cdmArgs, fields);
		if (file.exists()) {
			file.delete();
		}
	}

	private XlsxOutput build(final String... args) {
		return new XlsxOutput(XlsxOutput.DEFAULT_APP_NAME, XlsxOutput.DEFAULT_WORK_SHEET_NAME, 
				XlsxOutput.DEFAULT_WORK_SHEET_VERSION,
				// Headers
				XlsxOutput.DEFAULT_HEADER_FONT_NAME, XlsxOutput.DEFAULT_HEADER_FONT_SIZE, 
				XlsxOutput.DEFAULT_HEADER_FONT_COLOUR, XlsxOutput.DEFAULT_HEADER_FONT_FILL_COLOUR, 
				XlsxOutput.DEFAULT_FONT_WRAP_TXT, XlsxOutput.DEFAULT_HEADER_FONT_BOLD,
				XlsxOutput.DEFAULT_HEADER_BORDER_STYLE,
				// Rows
				XlsxOutput.DEFAULT_FONT_NAME, XlsxOutput.DEFAULT_FONT_SIZE, 
				XlsxOutput.DEFAULT_FONT_COLOUR, XlsxOutput.DEFAULT_FONT_FILL_COLOUR, 
				XlsxOutput.DEFAULT_FONT_WRAP_TXT, XlsxOutput.DEFAULT_FONT_BOLD,
				XlsxOutput.DEFAULT_BORDER_STYLE);
	}

} // end class XlsxOutputTest
