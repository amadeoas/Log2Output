package uk.co.bocaditos.log2xlsx.in.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import uk.co.bocaditos.log2xlsx.model.LogField;


/**
 * JUnit tests for class ComparableFilterTest.
 */
public class ComparableFilterTest {

	@Test
	public void constructorsTest() throws FilterException {
		final LogField field = mock(LogField.class);

		assertThrows(FilterException.class, new ThrowingRunnable() {

				@Override
				public void run() throws Throwable {
					new ComparableFilter<String>(field, (String) null) {};
				}

			});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new ComparableFilter<String>(field, null, "") {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new ComparableFilter<String>(field, "", null) {};
			}

		});
		assertThrows(FilterException.class, new ThrowingRunnable() {

			@Override
			public void run() throws Throwable {
				new ComparableFilter<Integer>(field, 10, 9) {};
			}

		});
	}

	@Test
	public void valuesTest() throws FilterException {
		final LogField field = mock(LogField.class);
		final Integer from = 8;
		final Integer to = 9;
		ComparableFilter<Integer> filter = new ComparableFilter<Integer>(field, from, to) {};

		assertEquals(from, filter.from());
		assertEquals(to, filter.to());

		filter = new ComparableFilter<Integer>(field, from) {};
		assertEquals(from, filter.from());
		assertNull(filter.to());
	}

} // end class ComparableFilterTest
