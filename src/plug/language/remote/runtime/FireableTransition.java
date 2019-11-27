package plug.language.remote.runtime;

import plug.statespace.transitions.FiredTransition;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class for a fireable transition of the model.
 * @author valentin
 */
public class FireableTransition
{

	public final byte data[];

	public FireableTransition(byte[] data) {
		this.data = data;
	}

	public void writeOn(OutputStream os) throws IOException {
		os.write(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FiredTransition) {
			FireableTransition other = (FireableTransition) obj;
			return Arrays.equals(this.data, other.data);
		}
		return false;
	}
}
