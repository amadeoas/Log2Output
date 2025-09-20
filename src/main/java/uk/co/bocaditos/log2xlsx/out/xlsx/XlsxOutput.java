package uk.co.bocaditos.log2xlsx.out.xlsx;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.dhatim.fastexcel.BorderStyle;
import org.dhatim.fastexcel.StyleSetter;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Value;

import uk.co.bocaditos.log2xlsx.model.Field;
import uk.co.bocaditos.log2xlsx.model.FieldNames;
import uk.co.bocaditos.log2xlsx.model.FieldsGroup;
import uk.co.bocaditos.log2xlsx.model.FieldsLine;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.log2xlsx.out.LogOutput;
import uk.co.bocaditos.log2xlsx.out.OutException;
import uk.co.bocaditos.utils.UtilsException;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to output fields into a XLSX.
 */
public class XlsxOutput implements LogOutput {

	public static final String ARG_APP_NAME = "appName";
	public static final String ARG_WORK_SHEET_VERSION = "workSheetVersion";
	public static final String ARG_WORK_SHEET_NAME = "workSheetName";
	// Headers
	public static final String ARG_HEADER_BORDER_STYLE = "hBorderStyle";
	public static final String ARG_HEADER_FONT_NAME = "hFontName";
	public static final String ARG_HEADER_FONT_SIZE = "hFontSize";
	public static final String ARG_HEADER_FONT_COLOUR = "hFontColour";
	public static final String ARG_HEADER_FONT_WRAP_TXT = "hFontWrapTxt";
	public static final String ARG_HEADER_FONT_FILL_COLOUR = "hFontFillColuor";
	public static final String ARG_HEADER_FONT_BOLD = "hFontBold";
	// Rows
	public static final String ARG_BORDER_STYLE = "borderStyle";
	public static final String ARG_FONT_NAME = "fontName";
	public static final String ARG_FONT_SIZE = "fontSize";
	public static final String ARG_FONT_COLOUR = "fontColour";
	public static final String ARG_FONT_WRAP_TXT = "fontWrapTxt";
	public static final String ARG_FONT_FILL_COLOUR = "fontFillColuor";
	public static final String ARG_FONT_BOLD = "fontBold";

	// Defaults
	public static final String DEFAULT_APP_NAME = "Log2Xlsx";
	public static final String DEFAULT_WORK_SHEET_VERSION = "1.0";
	public static final String DEFAULT_WORK_SHEET_NAME = "Sheet 1";
	// Headers
	public static final String DEFAULT_HEADER_BORDER_STYLE = "THIN";
	public static final String DEFAULT_HEADER_FONT_NAME = "Arial";
	public static final int DEFAULT_HEADER_FONT_SIZE = 12;
	public static final String DEFAULT_HEADER_FONT_COLOUR = "000000";
	public static final boolean DEFAULT_HEADER_FONT_WRAP_TXT = false;
	public static final String DEFAULT_HEADER_FONT_FILL_COLOUR = "C1CDCD";
	public static final boolean DEFAULT_HEADER_FONT_BOLD = true;
	// Rows
	public static final String DEFAULT_BORDER_STYLE = "THIN";
	public static final String DEFAULT_FONT_NAME = "Arial";
	public static final int DEFAULT_FONT_SIZE = 12;
	public static final String DEFAULT_FONT_COLOUR = "000000";
	public static final boolean DEFAULT_FONT_WRAP_TXT = false;
	public static final String DEFAULT_FONT_FILL_COLOUR = "FFFFFF";
	public static final boolean DEFAULT_FONT_BOLD = false;

	private final String appName;
	private final String workSheetName;
	private final String workSheetVersion;

	private final String headerFontName;
	private final int headerFontSize;
	private final String headerFontColour;
	private final String headerFontFillColour;
	private final boolean headerFontWrapTxt;
	private final boolean headerFontBold;
	private final BorderStyle headerBorderStyle;

	private final String fontName;
	private final int fontSize;
	private final String fontColour;
	private final String fontFillColour;
	private final boolean fontWrapTxt;
	private final boolean fontBold;
	private final BorderStyle borderStyle;


	public XlsxOutput(
			@Value("${xlsx.app.name:" + DEFAULT_APP_NAME + "}")
			final String appName, 
			@Value("${xlsx.work.sheet.name:" + DEFAULT_WORK_SHEET_NAME + "}")
			final String workSheetName, 
			@Value("${xlsx.work.sheet.version:" + DEFAULT_WORK_SHEET_VERSION + "}")
			final String workSheetVersion, 
			// Headers
			@Value("${xlsx.header.font.name:" + DEFAULT_HEADER_FONT_NAME + "}")
			final String headerFontName,
			@Value("${xlsx.header.font.size:" + DEFAULT_HEADER_FONT_SIZE + "}")
			final int headerFontSize, 
			@Value("${xlsx.header.font.colour:" + DEFAULT_HEADER_FONT_COLOUR + "}")
			final String headerFontColour, 
			@Value("${xlsx.header.font.fill.colour:" + DEFAULT_HEADER_FONT_FILL_COLOUR + "}")
			final String headerFontFillColour, 
			@Value("${xlsx.header.font.wrap.txt:" + DEFAULT_HEADER_FONT_WRAP_TXT + "}")
			final boolean headerFontWrapTxt, 
			@Value("${xlsx.header.font.bold:" + DEFAULT_HEADER_FONT_BOLD + "}")
			final boolean headerFontBold, 
			@Value("${xlsx.header.border.style:" + DEFAULT_HEADER_BORDER_STYLE + "}")
			final String headerBorderStyle,
			// Rows
			@Value("${xlsx.font.name:" + DEFAULT_FONT_NAME + "}")
			final String fontName, 
			@Value("${xlsx.font.size:" + DEFAULT_FONT_SIZE + "}")
			final int fontSize, 
			@Value("${xlsx.font.colour:" + DEFAULT_FONT_COLOUR + "}")
			final String fontColour, 
			@Value("${xlsx.font.fill.colour:" + DEFAULT_FONT_FILL_COLOUR + "}")
			final String fontFillColour, 
			@Value("${xlsx.font.fill.wrap.txt:" + DEFAULT_FONT_WRAP_TXT + "}")
			final boolean fontWrapTxt, 
			@Value("${xlsx.font.bold:" + DEFAULT_FONT_BOLD + "}")
			final boolean fontBold,
			@Value("${xlsx.border.style:" + DEFAULT_BORDER_STYLE + "}")
			final String borderStyle) {
		this.appName = appName;
		this.workSheetName = workSheetName;
		this.workSheetVersion = workSheetVersion;
		// Headers
		this.headerBorderStyle = BorderStyle.valueOf(headerBorderStyle.toUpperCase());
		this.headerFontName = headerFontName;
		this.headerFontSize = headerFontSize;
		this.headerFontColour = headerFontColour;
		this.headerFontFillColour = headerFontFillColour;
		this.headerFontWrapTxt = headerFontWrapTxt;
		this.headerFontBold = headerFontBold;
		// Rows
		this.borderStyle = BorderStyle.valueOf(borderStyle.toUpperCase());
		this.fontName = fontName;
		this.fontSize = fontSize;
		this.fontColour = fontColour;
		this.fontFillColour = fontFillColour;
		this.fontWrapTxt = fontWrapTxt;
		this.fontBold = fontBold;
	}

	public XlsxOutput(final CmdArgs cmdArgs) {
		this.appName = cmdArgs.getParam(ARG_APP_NAME, DEFAULT_APP_NAME);
		this.workSheetName = cmdArgs.getParam(ARG_WORK_SHEET_NAME, DEFAULT_WORK_SHEET_NAME);
		this.workSheetVersion = cmdArgs.getParam(ARG_WORK_SHEET_VERSION, DEFAULT_WORK_SHEET_VERSION);
		// Headers
		this.headerBorderStyle = cmdArgs.getParam(ARG_HEADER_BORDER_STYLE, BorderStyle.valueOf(DEFAULT_HEADER_BORDER_STYLE));
		this.headerFontName = cmdArgs.getParam(ARG_HEADER_FONT_NAME, DEFAULT_HEADER_FONT_NAME);
		this.headerFontSize = cmdArgs.getParam(ARG_HEADER_FONT_SIZE, DEFAULT_HEADER_FONT_SIZE);
		this.headerFontColour = cmdArgs.getParam(ARG_HEADER_FONT_COLOUR, DEFAULT_HEADER_FONT_COLOUR);
		this.headerFontFillColour = cmdArgs.getParam(ARG_HEADER_FONT_FILL_COLOUR, DEFAULT_HEADER_FONT_FILL_COLOUR);
		this.headerFontWrapTxt = cmdArgs.getParam(ARG_HEADER_FONT_WRAP_TXT, DEFAULT_HEADER_FONT_WRAP_TXT);
		this.headerFontBold = cmdArgs.getParam(ARG_HEADER_FONT_BOLD, DEFAULT_HEADER_FONT_BOLD);
		// Rows
		this.borderStyle = cmdArgs.getParam(ARG_BORDER_STYLE, BorderStyle.valueOf(DEFAULT_BORDER_STYLE));
		this.fontName = cmdArgs.getParam(ARG_FONT_NAME, DEFAULT_FONT_NAME);
		this.fontSize = cmdArgs.getParam(ARG_FONT_SIZE, DEFAULT_FONT_SIZE);
		this.fontColour = cmdArgs.getParam(ARG_FONT_COLOUR, DEFAULT_FONT_COLOUR);
		this.fontFillColour = cmdArgs.getParam(ARG_FONT_FILL_COLOUR, DEFAULT_FONT_FILL_COLOUR);
		this.fontWrapTxt = cmdArgs.getParam(ARG_FONT_WRAP_TXT, DEFAULT_FONT_WRAP_TXT);
		this.fontBold = cmdArgs.getParam(ARG_FONT_BOLD, DEFAULT_FONT_BOLD);
	}

	@Override
	public void write(final CmdArgs cmdmArgs, final FieldsSet set) 
			throws UtilsException {
		final String filename = cmdmArgs.getArgument(ARG_OUT);

		try (final OutputStream os = Files.newOutputStream(Paths.get(filename));
				final Workbook wb = new Workbook(os, this.appName, this.workSheetVersion)) {
	        final Worksheet ws = wb.newWorksheet(this.workSheetName);
	        final FieldNames headers = headers(cmdmArgs, set, ws);
	        final StyleSetter style = ws.range(1, 0, numRows(set), headers.size())
	            .style()
	           	.fontName(this.fontName)
	    	   	.fontSize(this.fontSize)
	    	   	.fontColor(this.fontColour)
	    	   	.wrapText(this.fontWrapTxt)
	    	   	.fillColor(this.fontFillColour)
	    	   	.borderStyle(this.borderStyle);
			int lastRowIndex = 0;

	        if (cmdmArgs.getParam(ARG_FONT_BOLD, this.fontBold)) {
	            style.bold();
	        }
	    	style.set();
	        for (final FieldsGroup grp : set) {
	        	for (final FieldsLine line : grp) {
	        		write(ws, ++lastRowIndex, headers, line);
	        	}
	        }
		} catch (final IOException ioe) {
			throw new OutException(ioe, "Issues saving logs into XLSX file \"{0}\"", filename);
		}
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(ARG_APP_NAME, "Sets the tool name, default \"" + DEFAULT_APP_NAME + "\".", 
				false, new CmdHelpArgParamDef("name", "The name of the tool.", true));
		new CmdHelpArgDef(ARG_WORK_SHEET_VERSION, "Sets the work sheet version, default \"" 
				+ DEFAULT_WORK_SHEET_VERSION + "\".", false, 
				new CmdHelpArgParamDef("version", "The worksheet version.", true));
		new CmdHelpArgDef(ARG_WORK_SHEET_NAME, "Sets the worksheet name \"" + DEFAULT_WORK_SHEET_NAME 
				+ "\".", false, new CmdHelpArgParamDef("name", "the worksheet name.", true));

		// Headers
		new CmdHelpArgDef(ARG_HEADER_BORDER_STYLE, "Header border style., default \"" 
				+ DEFAULT_HEADER_BORDER_STYLE + "\".", 
				false, new CmdHelpArgParamDef("style", "The border style.", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_NAME, 
				"Sets the header font name \"" + DEFAULT_HEADER_FONT_NAME + "\".", false, 
				new CmdHelpArgParamDef("size", "A size in pixels.", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_SIZE, 
				"Sets the header font size \"" + DEFAULT_HEADER_FONT_SIZE + "\".", false, 
				new CmdHelpArgParamDef("size", "a size in pixels.", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_COLOUR, 
				"Sets the header font colour, default \"" + DEFAULT_HEADER_FONT_COLOUR + "\".", false, 
				new CmdHelpArgParamDef("colour", "a colour in hexadecimal, e.g. #C1CDCD.", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_WRAP_TXT, 
				"Sets the header fount text wrapping, default \"" + DEFAULT_HEADER_FONT_WRAP_TXT 
				+ "\".", 
				false, new CmdHelpArgParamDef("wrap", "true to wrap the text.", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_FILL_COLOUR, 
				"Sets the headers font filling colour, default \"" + DEFAULT_HEADER_FONT_FILL_COLOUR 
				+ "\".", 
				false, new CmdHelpArgParamDef("colour", "filling colour,", true));
		new CmdHelpArgDef(ARG_HEADER_FONT_BOLD, "Sets the headers font to bold.", 
				false, new CmdHelpArgParamDef("isBold", "true if the headers must be in bold font,", 
					true));

		// Rows
		new CmdHelpArgDef(ARG_BORDER_STYLE, "Header border style, default \"" + DEFAULT_BORDER_STYLE 
				+ "\".", 
				false, new CmdHelpArgParamDef("style", "The border style.", true));
		new CmdHelpArgDef(ARG_FONT_NAME, 
				"Sets the header font name \"" + DEFAULT_FONT_NAME + "\".", false, 
				new CmdHelpArgParamDef("size", "size in pixels.", true));
		new CmdHelpArgDef(ARG_FONT_SIZE, 
				"Sets the header font size \"" + DEFAULT_FONT_SIZE + "\".", false, 
				new CmdHelpArgParamDef("size", "a size in pixels.", true));
		new CmdHelpArgDef(ARG_FONT_COLOUR, 
				"Sets the header font colour, default \"" + DEFAULT_FONT_COLOUR + "\".", false, 
				new CmdHelpArgParamDef("colour", "a colour in hexadecimal, e.g. #C1CDCD.", true));
		new CmdHelpArgDef(ARG_FONT_WRAP_TXT, 
				"Sets the header fount text wrapping, default \"" + DEFAULT_FONT_WRAP_TXT + "\".", 
				false, new CmdHelpArgParamDef("wrap", "true to wrap the text.", true));
		new CmdHelpArgDef(ARG_FONT_FILL_COLOUR, 
				"Sets the font filling colour, default \"" + DEFAULT_FONT_FILL_COLOUR + "\".", 
				false, new CmdHelpArgParamDef("colour", "filling colour.", true));
		new CmdHelpArgDef(ARG_FONT_BOLD, "Sets the rows font to bold.", 
				false, new CmdHelpArgParamDef("isBold", "true if the rows must be in bold font.", 
					true));
	}

	private int numRows(final FieldsSet set) {
		int numRows = 0;

        for (final FieldsGroup grp : set) {
        	numRows += grp.size();
        }

        return numRows;
	}

	private FieldNames headers(final CmdArgs cmdArgs, final FieldsSet set, final Worksheet ws) {
		// Headers
		final FieldNames headers = LogOutput.sorted(cmdArgs, set);
		int columnIndex = -1;
		final StyleSetter setters = ws.range(0, 0, 0, headers.size())
			.style()
			.fontName(cmdArgs.getParam(ARG_HEADER_FONT_NAME, this.headerFontName))
        	.fontSize(cmdArgs.getParam(ARG_HEADER_FONT_SIZE, this.headerFontSize))
        	.fontColor(cmdArgs.getParam(ARG_HEADER_FONT_COLOUR, this.headerFontColour))
        	.wrapText(cmdArgs.getParam(ARG_HEADER_FONT_WRAP_TXT, this.headerFontWrapTxt))
        	.fillColor(cmdArgs.getParam(ARG_HEADER_FONT_FILL_COLOUR, this.headerFontFillColour))
    	   	.borderStyle(cmdArgs.getParam(ARG_HEADER_BORDER_STYLE, this.headerBorderStyle));

		if (cmdArgs.getParam(ARG_HEADER_FONT_BOLD, this.headerFontBold)) {
			setters.bold();
		}
		setters.set();

		for (final LogField header : headers) {
        	ws.value(0, ++columnIndex, header.getId());
		}

		return headers;
	}

	private void write(final Worksheet ws, int lastRowIndex, final FieldNames fieldNames, 
			final FieldsLine line) throws UtilsException {
		for (final Field field : line) {
			ws.value(lastRowIndex, fieldNames.index(field.getFieldName()), 
					field.getValue().toString());
		}
	}

} // end class XlsxOutput
