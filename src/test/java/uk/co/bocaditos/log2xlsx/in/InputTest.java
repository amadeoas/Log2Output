package uk.co.bocaditos.log2xlsx.in;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.springframework.core.env.Environment;

import uk.co.bocaditos.utils.cmd.CmdArgs;
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

		final Environment env = mock(Environment.class);

		assertThrows(InputException.class, () -> {
			Input.build(new CmdArgs(env, new String[] {CmdArgs.START + "none", "nothing", 
					CmdArgs.START + Input.CMD_LOGS}));
		});
	}

	@Test
	public void getArgumentTest() throws InputException {
		final boolean DEFAULT = false;
		final String[] args = {};

		assertThrows(InputException.class, () -> {
				Input.getArgument(args, "none");
			});
		assertEquals(DEFAULT, Input.getValue(args, "none", DEFAULT));
	}

} // end class InputTest
