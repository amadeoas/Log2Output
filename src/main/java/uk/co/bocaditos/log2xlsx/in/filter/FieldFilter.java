package uk.co.bocaditos.log2xlsx.in.filter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import uk.co.bocaditos.log2xlsx.model.Field;
import uk.co.bocaditos.log2xlsx.model.FieldsLine;
import uk.co.bocaditos.log2xlsx.model.FormatException;
import uk.co.bocaditos.log2xlsx.model.LogField;
import uk.co.bocaditos.log2xlsx.model.LogSet;
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

	private static final String ARG_FILTER_FIELD_NAME = "filter";

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
			throws CmdException, FormatException {
		if (cmdArgs == null) {
			return build();
		}

		final String fieldName = cmdArgs.getParam(ARG_FILTER_FIELD_NAME, 0, null);

		if (fieldName == null) {
			return build();
		}

		final String[] args = {
				cmdArgs.getArgument(ARG_FILTER_FIELD_NAME, 1),
				cmdArgs.getParam(ARG_FILTER_FIELD_NAME, 2, null)
		};

		final LogField field = set.getField(fieldName);

		if (field.getFieldClass() == String.class) {
			if (args[1] != null) {
				throw new FormatException(MSG_TOO_MANY_ARGS, ARG_FILTER_FIELD_NAME);
			}

			return new StringFilter(field, (String) field.build(args[1]));
		}

		if (field.getFieldClass() == LocalDateTime.class) {
			if (args[1] == null) {
				return new LocalDateTimeFilter(field, (LocalDateTime) field.build(args[1]));
			} else {
				return new LocalDateTimeFilter(field, (LocalDateTime) field.build(args[1]), 
						(LocalDateTime) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == LocalDate.class) {
			if (args[1] == null) {
				return new LocalDateFilter(field, (LocalDate) field.build(args[1]));
			} else {
				return new LocalDateFilter(field, (LocalDate) field.build(args[1]), 
						(LocalDate) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == int.class) {
			if (args[1] == null) {
				return new IntFilter(field, (Integer) field.build(args[1]));
			} else {
				return new IntFilter(field, (Integer) field.build(args[1]), 
						(Integer) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == long.class) {
			if (args[1] == null) {
				return new LongFilter(field, (Long) field.build(args[1]));
			} else {
				return new LongFilter(field, (Long) field.build(args[1]), 
						(Long) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == double.class) {
			if (args[1] == null) {
				return new DoubleFilter(field, (Double) field.build(args[1]));
			} else {
				return new DoubleFilter(field, (Double) field.build(args[1]), 
						(Double) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == float.class) {
			if (args[1] == null) {
				return new FloatFilter(field, (Float) field.build(args[1]));
			} else {
				return new FloatFilter(field, (Float) field.build(args[1]), 
						(Float) field.build(args[2]));
			}
		}

		if (field.getFieldClass() == char.class) {
			if (args[1] != null) {
				throw new FormatException(MSG_TOO_MANY_ARGS, ARG_FILTER_FIELD_NAME);
			}

			return new CharFilter(field, (Character) field.build(args[0]));
		}

		if (field.getFieldClass() == boolean.class) {
			if (args[1] != null) {
				throw new FormatException(MSG_TOO_MANY_ARGS, ARG_FILTER_FIELD_NAME);
			}

			return new BooleanFilter(field, (Boolean) field.build(args[1]));
		}

		throw new FormatException("");
	}

	public final T getFirstValue() {
		return getValues()[0];
	}

	public boolean valid(final FieldsLine line) {
		final Field field = (Field) line.get(this.field);
		final Object value = field.getValue();

		if (this.values.length == 1) {
			return Objects.equals(getFirstValue(), value);
		}
		
		return valid_(value);
	}

	public final LogField getField() {
		return this.field;
	}

	public static void initHelp() {
		new CmdHelpArgDef(ARG_FILTER_FIELD_NAME, "Sets the filter of the log lines to consider.", 
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
