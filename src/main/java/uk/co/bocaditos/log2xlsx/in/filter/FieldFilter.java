package uk.co.bocaditos.log2xlsx.in.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import uk.co.bocaditos.log2xlsx.model.FieldsLine;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.log2xlsx.model.LogSet;
import uk.co.bocaditos.utils.Utils;
import uk.co.bocaditos.utils.cmd.CmdArgs;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to filter log lines based on a field value.
 * 
 * @param <T> the type of value.
 */
public abstract class FieldFilter<T> implements Filter {

	public static final String CMD_FILTER = "filter";

	private static final List<String> FILTER_EMPTY_ARGS = new ArrayList<>(0);

	private static final String MSG_TOO_MANY_ARGS = "Too many values for CMD Line arguments \"{0}\"";

	private final LogField field;
	private final T[] values;


	@SafeVarargs
	FieldFilter(final LogField field, final T... values) {
		this.field = field;
		this.values = values;
	}

	boolean valid_(final Object value) {
		return true;
	}

	public static FieldFilter<?> build() {
		return new AllFilter();
	}

	public static FieldFilter<?> build(final CmdArgs cmdArgs, final LogSet set) 
			throws CmdException, FormatException, FilterException {
		if (cmdArgs == null) {
			return build();
		}

		final List<String> argValues = cmdArgs.getArguments(CMD_FILTER, FILTER_EMPTY_ARGS);

		if (Utils.isEmpty(argValues)) {
			return build();
		}

		final LogField field = set.getField(argValues.get(0));

		if (field.getFieldClass() == String.class) {
			if (argValues.size() > 2) {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}

			return new StringFilter(field, (String) field.build(argValues.get(1)));
		}

		if (field.getFieldClass() == LocalDateTime.class) {
			if (argValues.size() == 2) {
				return new LocalDateTimeFilter(field, (LocalDateTime) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new LocalDateTimeFilter(field, (LocalDateTime) field.build(argValues.get(1)), 
						(LocalDateTime) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == LocalDate.class) {
			if (argValues.size() == 2) {
				return new LocalDateFilter(field, (LocalDate) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new LocalDateFilter(field, (LocalDate) field.build(argValues.get(1)), 
						(LocalDate) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == int.class) {
			if (argValues.size() == 2) {
				return new IntFilter(field, (Integer) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new IntFilter(field, (Integer) field.build(argValues.get(1)), 
						(Integer) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == long.class) {
			if (argValues.size() == 2) {
				return new LongFilter(field, (Long) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new LongFilter(field, (Long) field.build(argValues.get(1)), 
						(Long) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == double.class) {
			if (argValues.size() == 2) {
				return new DoubleFilter(field, (Double) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new DoubleFilter(field, (Double) field.build(argValues.get(1)), 
						(Double) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == float.class) {
			if (argValues.size() == 2) {
				return new FloatFilter(field, (Float) field.build(argValues.get(1)));
			} else if (argValues.size() == 3) {
				return new FloatFilter(field, (Float) field.build(argValues.get(1)), 
						(Float) field.build(argValues.get(2)));
			} else {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}
		}

		if (field.getFieldClass() == char.class) {
			if (argValues.size() != 2) {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}

			return new CharFilter(field, (Character) field.build(argValues.get(1)));
		}

		if (field.getFieldClass() == boolean.class) {
			if (argValues.size() != 2) {
				throw new FormatException(MSG_TOO_MANY_ARGS, CMD_FILTER);
			}

			return new BooleanFilter(field, (Boolean) field.build(argValues.get(1)));
		}

		throw new FormatException("Failed to build filter for value");
	}

	public final T getFirstValue() {
		return getValues()[0];
	}

	public boolean valid(final FieldsLine line) {
		final Object value = line.get(this.field);

		if (this.values.length == 1) {
			return Objects.equals(getFirstValue(), value);
		}
		
		return valid_(value);
	}

	public final LogField getField() {
		return this.field;
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(CMD_FILTER, "Sets the filter of the log lines to consider.", 
			true, 
			new CmdHelpArgParamDef("fieldname", "The name of the field to filter against.",
				true), 
			new CmdHelpArgParamDef("value/from", "The value to be bigger or equal to.",
				true), 
			new CmdHelpArgParamDef("to", "The value to smaller or equal to.", false));
	}
	
	final T[] getValues() {
		return this.values;
	}

} // end class FieldFilter


/**
 * No filter is applied.
 */
class AllFilter extends FieldFilter<Object> {

	AllFilter() {
		super(null);
	}

	public boolean valid(final FieldsLine line) {
		return true;
	}

} // end class AllFilter
