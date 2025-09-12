# Log to Output

[Introduction](#introduction) - [Usage](#usage) - [Development](#development) - [XLSX](#xlsx) - [HTML](#html) - [Backlog](#backlog)

---

# Introduction
A Spring Boot command line application.

It reads specified log files and convert them into a formatted output file, e.g. [HTML](#html) or 
[XLSX](#xlsx).

Formats supported are
- [HTML](#html) by providing a [Freemarker](https://freemarker.apache.org/index.html) template, 
default is htmlLogs.ftl
- [XLSX](#xlsx)


Each field is defined by starting character '{', to use the literal character in a log file escape 
it like '\\\\{' (without the ') and the end character is '}', which if needed to be use as part of 
the log lines then escape it, i.e. '\\\\{' (without the '). The general format is:

```
{<name>[, <type>[[, [p:<pattern>]][, [f:<format>]]|[<emun_name1>[,<enum_name>...]]]]}...
```

The '**type**' may be: **string**, **int**, **long**, **double**, **float**, **char**, **boolean**, 
**datetime**, **date**, and **enum**. By 
default it is 'string'.

If the '**type**' is '**enum**' then the following values must be valid enumeration values/names. 
Example:

```
{request, enum, REQUEST_RECEIVED, SENT_REQUEST, REQUEST_RESPONSE, SENT_RESPONSE}
```

Example of using format is:

```
{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}
```
and

```
{datetime, date, f:yyyyMMdd}
```

When a pattern is specified the loaded field from the log lines will be verified against the pattern
and a FormatException thrown if verification fails.

---

# Usage
java \-jar Log2Xlsx-1.0.000.jar \-formats &lt;filename&gt; \-idFieldName &lt;idFieldName&gt; \-logs &lt;filenames&gt; \-out &lt;filename&gt; \[-format <fieldname> <value/from> [<to>]\] \[\-sort &lt;field names&gt;\] \[\-appName &lt;name&gt;\] \[\-workSheetVersion &lt;version&gt;\] \[\-workSheetName &lt;name&gt;\] \[\-hBorderStyle &lt;style&gt;\] \[\-hFontName &lt;size&gt;\] \[-hFontSize &lt;size&gt;\] \[-hFontColour &lt;colour&gt;\] \[\-hFontWrapTxt &lt;wrap&gt;\] \[\-hFontFillColuor &lt;colour&gt;\] \[\-hFontBold &lt;isBold&gt;\] \[\-borderStyle &lt;style&gt;\] \[\-fontName &lt;size&gt;\] \[\-fontSize &lt;size&gt;\] \[\-fontColour &lt;colour&gt;\] \[-fontWrapTxt &lt;wrap&gt;\] \[\-fontFillColuor &lt;colour&gt;\] \[\-fontBold &lt;isBold&gt;\] \[-dir4template &lt;dir&gt;\] \[-template &lt;name&gt;\] \[\-encoding &lt;name&gt;\] \[\-freemakerVersion &lt;name&gt;\] \[\-htmlDefaultSize &lt;size&gt;\] \[\-htmlSize &lt;size&gt;\]

<br>
 -formats &lt;filename&gt;
	Sets the log formats to use.

	filename
		The format filename.

```
Example:

[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \{{computer}\} [{class}] - ({id}) {request, enum, REQUEST_RECEIVED, SENT_REQUEST, REQUEST_RESPONSE, SENT_RESPONSE} \\{\"headers\": {headers}, \"payload\": {body}\\} - END
[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \{{computer}\} [{class}] - ({id}) {request, enum, REQUEST_RECEIVED, SENT_REQUEST, REQUEST_RESPONSE, SENT_RESPONSE} {message} - END
[{datetime, datetime, f:yyyyMMdd HH:mm:ss.SSS}] \{{computer}\} [{class}] - ({id}) {message}
```

 -idFieldName &lt;idFieldName&gt;
	Sets the field name to use as ID for groups.

	idFieldName
		The name of the field used as ID for groups.
 -logs &lt;filenames&gt;
	Sets the files or directory with the log lines.

	filenames
		If a directory just one without '.' where all files ended with .log will be used
	 other wise will be considered a comma separated list of log filed.
 -out &lt;filename&gt;
	Sets the output file name.

	filename
		A filename for the output.

 [-filter &lt;field_name&gt; &lt;value/from&gt; [&lt;to&gt;]]
 	Sets the filter of the log lines to consider.

	field_name
		The name of the field to filter against.
		
	value/from
		The value to equal to the field or is bigger or equal to.
	to
		The value to be smaller or equal to.

 [-sort &lt;field_names&gt;]
	Sets the order of the columns based on the fields names separated by comma. The 
	field names not present will not be part of the output.

	field_names
		A list of the field names separated by comma.

[XLSX](#xlsx)<br>

 [-appName &lt;name&gt;]
	Sets the tool name, default "Log2Xlsx".

	name
		The name of the tool.
 [-workSheetVersion &lt;version&gt;]
	Sets the work sheet version, default "1.0".

	version
		The worksheet version.
 [-workSheetName &lt;name&gt;]
	Sets the worksheet name "Sheet 1".

	name
		the worksheet name.

[HTML](#html)<br>

 [-dir4template &lt;dir&gt;]
	Sets the template directory, default "/templates".

	dir
		the template directory.
 [-template &lt;name&gt;]
	Sets the template to create HTML, default "htmlLogs.ftl".

	name
		The name of the tool.
 [-encoding &lt;name&gt;]
	Sets the tool name, default "UTF-8".

	name
		The name of the tool.
 [-freemakerVersion &lt;name&gt;]
	Sets the tool name, default "2.3.34".

	name
		The name of the tool.
 [-maxCellLength <max_length>]
 	Sets the maximum number of characters in a cell before truncating it and providing full text on 
 	clicking the cell text. Default value is 2147483647.

	
	max_length
		The max. num. of characters in a cell.

 [-hBorderStyle &lt;style&gt;]
	Header border style., default "THIN".

	style
		The border style.
 [-hFontName &lt;size&gt;]
	Sets the header font name "Arial".

	size
		A size in pixels.
 [-hFontSize &lt;size&gt;]
	Sets the header font size "12".

	size
		a size in pixels.
 [-hFontColour &lt;colour&gt;]
	Sets the header font colour, default "#000000".

	colour
		a colour in hexadecimal, e.g. C1CDCD.
 [-hFontWrapTxt &lt;wrap&gt;]
	Sets the header fount text wrapping, default "false".

	wrap
		true to wrap the text.
 [-hFontFillColuor &lt;colour&gt;]
	Sets the headers font filling colour, default "#C1CDCD".

	colour
		filling colour.
 [-hFontBold &lt;isBold&gt;]
	Sets the headers font to bold.

	isBold
		true if the headers must be in bold font.
 [-borderStyle &lt;style&gt;]
	Header border style, default "THIN".

	style
		The border style.
 [-fontName &lt;size&gt;]
	Sets the header font name "Arial".

	size
		A size in pixels.
 [-fontSize &lt;size&gt;]
	Sets the header font size "12".

	size
		a size in pixels.
 [-fontColour &lt;colour&gt;\]
	Sets the header font colour, default "#000000".

	colour
		a colour in hexadecimal, e.g. C1CDCD.
 [-fontWrapTxt &lt;wrap&gt;]
	Sets the header fount text wrapping, default "false".

	wrap
		true to wrap the text.
 [-fontFillColuor &lt;colour&gt;]
	Sets the font filling colour, default "#FFFFFF".

	colour
		filling colour.
 [-fontBold &lt;isBold&gt;]
	Sets the rows font to bold.

	isBold
		true if the rows must be in bold font.
 [-htmlDefaultSize &lt;size&gt;]
	Sets the table default column size, default "auto".

	size
		A size, e.g. 150px.
 [-htmlSize &lt;size&gt;]
	&lt;name&gt; Sets the table column size.

	size
		A size, e.g. 150px.

---

# Development
Spring Boot (Command Line) application.

---

# XLSX
Example of HTML result from log lines can be seen at [testOut.xlsx](testOut_.xlsx).

---

# HTML
Example of HTML result from log lines can be seen at [testOut.html](testOut_.html).

---

# Backlog

- Add support to decouple the input format and output one.

---

[Introduction](#introduction) - [Development](#development) - [Usage](#usage) - [XLSX](#xlsx) - [HTML](#html) - [Backlog](#backlog)

---