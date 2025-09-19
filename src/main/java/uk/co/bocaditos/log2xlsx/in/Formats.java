package uk.co.bocaditos.log2xlsx.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import uk.co.bocaditos.log2xlsx.in.filter.Filter;
import uk.co.bocaditos.log2xlsx.model.FieldsSet;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.utils.Utils;


/**
 * Support to load the formats from different sources.
 */
public abstract class Formats {

	private Formats() {
	}

	public static FieldsSet process(final Filter filter, final String idFieldName, final LogSet set, 
			final Input input) throws FormatException, InputException {
		if (input == null) {
			throw new FormatException("Missing log inputs");
		}

		return new FieldsSet(set, idFieldName).load(filter, input);
	}

	public static LogSet loadFiles(final String... formatFiles) throws FormatException {
		if (Utils.isEmpty(formatFiles)) {
			throw new FormatException("Missing the format file names");
		}

		final LogSet set = new LogSet();

		for (final String filename : formatFiles) {
			loadFile(set, filename);
		}

		return set;
	}

	public static LogSet loadAll(final String... formatFiles) throws FormatException {
		if (Utils.isEmpty(formatFiles)) {
			throw new FormatException("Missing formats files");
		}

		final LogSet set = new LogSet();

		for (final String formatFile : formatFiles) {
			final File file = new File(formatFile.trim());
	
			if (!file.isFile()) {
				throw new FormatException("The format file \"{0}\" doesn''t exist or isn''t a file", 
						formatFile);
			}
			loadFile(set, file);
		}

		return set;
	}

	public static LogSet load(final String... formats) throws FormatException {
		if (Utils.isEmpty(formats)) {
			throw new FormatException("Missing format lines");
		}

		final LogSet set = new LogSet();

		for (final String line : formats) {
			if (Utils.isEmpty(line)) {
				continue;
			}

			set.load(line);
		}

		return set;
	}

	public static LogSet loadFile(final LogSet set, final String formatFile) 
			throws FormatException {
		if (Utils.isEmpty(formatFile)) {
			throw new FormatException("Missing the format file name");
		}

		return loadFile(set, new File(formatFile.trim()));
	}

	public static LogSet loadFile(final LogSet set, final File file) throws FormatException {
		if (file == null) {
			throw new FormatException("Missing the format file");
		}

		try (final BufferedReader in = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			String line;

			while ((line = in.readLine()) != null) {
				set.load(line);
			}
	
			return set;
		} catch (final IOException ioe) {
			throw new FormatException(ioe, 
					"Failed reading file \"{0}\" to build log formats: \"{1}\"", 
					file.getAbsolutePath(), ioe.getMessage());
		}
	}

} // end Formats
