package plug.language.remote.runtime;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for a fireable transition of the model.
 * @author valentin
 */
public class FireableTransition
{
	byte rawTransitionData[];

	public FireableTransition(byte[] data) {
		rawTransitionData = data;
	}

	public void writeOn(OutputStream os) throws IOException {
		os.write(rawTransitionData);
	}
}