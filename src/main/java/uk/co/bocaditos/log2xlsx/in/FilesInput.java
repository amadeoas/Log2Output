package uk.co.bocaditos.log2xlsx.in;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import uk.co.bocaditos.utils.Utils;


/**
 * Support to access lines in files.
 */
public class FilesInput extends Input {

	private final String[] filenames;
	private BufferedReader reader;
	private int currentFile = -1;
	private int lineNum;


	protected FilesInput(final String... filenames) throws InputException {
		super(filenames);

		this.filenames = new String[filenames.length];
		for (int i = 0; i < filenames.length; ++i) {
			final File file;

			this.filenames[i] = filenames[i].trim();
			file = new File(this.filenames[i]);
			if (!file.isFile()) {
				throw new InputException("The file \"{0}\" is not a fale", file.getName());
			}

			if (!file.exists()) {
				throw new InputException("The file \"{0}\" doesn't exist", file.getName());
			}
		}
		next();
	}

	public static boolean isValid(final String... filenames) {
		if (Utils.isEmpty(filenames)) {
			return false;
		}

		for (final String filename : filenames) {
			final File file = new File(filename);

			if (!file.isFile() && !file.exists()) {
				return false;
			}
		}

		return true;
	}

	public final String getFilename() {
		return (this.currentFile < 0) ? null : this.filenames[this.currentFile];
	}

	public final int getLineNum() {
		return this.lineNum;
	}

	@Override
	public void close() throws IOException {
		close(true);
	}

	final void close(final boolean end) throws IOException {
		if (this.reader != null) {
			final BufferedReader reader = this.reader;
	
			this.reader = null;
			this.lineNum = 0;
			reader.close();
		} else if (this.currentFile >= this.filenames.length) {
			this.currentFile = -1;
		}

		if (end) {
			this.currentFile = -1;
			this.lineNum = 0;
		}
	}

	@Override
	public String readLine() throws InputException {
		// this.filenames cannot be null or empty because inherited constructor
		if (this.currentFile == this.filenames.length) {
			return null;
		}

		try {
			String line;

			do {
				line = this.reader.readLine();
				if (line != null) {
					++this.lineNum;

					return line;
				}
			} while (next());

			return line;
		} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed getting next line for file \"{0}\" at line {1, number}", 
					getFilename(), getLineNum());
		}
	}

	@Override
	public String getId() {
		return "FILES";
	}

	private boolean next() throws InputException {
		// this.filenames cannot be null or empty because inherited constructor
		if (this.reader != null) {
			try {
				close(false);
			} catch (final IOException ioe) {
				throw new InputException(ioe, "Failed closing file \"{0}\"", getFilename());
			}
		}

		++this.currentFile;
		if (this.currentFile >= this.filenames.length) {
			return false;
		}

		final String filename = this.filenames[this.currentFile];

		if (Utils.isEmpty(filename)) {
			throw new InputException("Missing the file name for file index {0, number}", 
					this.currentFile);
		}

		try {
			final File file = new File(filename);

			this.reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
			this.lineNum = 0;

			return true;
		} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed accessing file \"{0}\"", filename);
		}
	}

} // end class FilesInput
