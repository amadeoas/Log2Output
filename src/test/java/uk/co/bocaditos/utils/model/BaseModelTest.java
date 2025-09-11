package uk.co.bocaditos.utils.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.junit.Test;

/**
 * JUnit tests for class BaseModel.
 */
public class BaseModelTest {

	@Test
	public void test() {
		final BaseModelImp base = new BaseModelImp("");

		assertFalse(base.equals(null));
		assertFalse(base.equals(""));
		assertTrue(base.equals(base));

		assertEquals("{}", base.toString());
	}

	@Test
	public void appendTest() {
		final String DATE = "20251201";
		final String DATE_TIME = DATE + "T13:45:21.002";
		final String VALUE = "value";
		final StringBuilder buf = new StringBuilder();
		final BaseModelImp base = new BaseModelImp(VALUE);

		assertEquals("", BaseModel.append(buf, "field", (String) null).toString());
		assertEquals('"' + VALUE + '"', BaseModel.append(buf, VALUE).toString());

		buf.setLength(0);
		assertEquals("", BaseModel.append(buf, "field", (BaseModelImp) null).toString());
		assertEquals("", BaseModel.append(buf, "field", (Collection<BaseModelImp>) null).toString());
		BaseModel.append(buf, "field", base);

		buf.setLength(0);
		assertEquals("", BaseModel.append(buf, "field", (Level) null).toString());
		assertEquals("field: \"INFO\"", BaseModel.append(buf, "field", Level.INFO).toString());

		buf.setLength(0);
		assertEquals("", BaseModel.append(buf, "field", (LocalDateTime) null).toString());
		assertEquals("field: \"" + DATE_TIME + '"', 
				BaseModel.append(buf, "field", LocalDateTime.parse(DATE_TIME, BaseModel.FORMATER_DATETIME)).toString());

		buf.setLength(0);
		assertEquals("", BaseModel.append(buf, "field", (LocalDate) null).toString());
		assertEquals("field: \"" + DATE + '"', 
				BaseModel.append(buf, "field", LocalDate.parse(DATE, BaseModel.FORMATER_DATE)).toString());

		buf.setLength(0);
		assertEquals("", BaseModel.appendTxt(buf, "field", (Collection<String>) null).toString());
		assertEquals("field: [\"1\", \"2\"]", 
				BaseModel.appendTxt(buf, "field", Arrays.asList("1", "2")).toString());

		buf.setLength(0);
		buf.append('{');
		assertEquals("{", BaseModel.separator(buf).toString());
		buf.setLength(0);
		buf.append('[');
		assertEquals("[", BaseModel.separator(buf).toString());
	}

} // end class BaseModelTest


enum Level {
	ERROR,
	WARN,
	INFO,
	DEBUG;
	
} // end enum Level


/**
 * .
 */
class BaseModelImp extends BaseModel<BaseModelImp> {

	private final String id;


	BaseModelImp(final String id) {
		this.id = id;
	}

	@Override
	public boolean equalsIt(final BaseModelImp obj) {
		return Objects.equals(this.id, obj.id);
	}

	@Override
	public StringBuilder toString(final StringBuilder buf) {
		return buf.append(this.id);
	}
	
} // end class BaseModelImp
