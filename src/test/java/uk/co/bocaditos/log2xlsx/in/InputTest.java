package uk.co.bocaditos.log2xlsx.in;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

import uk.co.bocaditos.utils.cmd.CmdException;


/**
 * JUnits tests for class Input.
 */
public class InputTest {

	@Test
	public void buildTest() {
		assertThrows(CmdException.class, () -> {
			Input.build(null);
		});
	}

} // end class InputTest
