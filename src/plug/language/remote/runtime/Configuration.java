package plug.language.remote.runtime;

import plug.core.IConfiguration;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class for configuration of the model.
 * @author valentin
 */
public class Configuration implements IConfiguration<Configuration> {
	public byte[] state;

	public Configuration() {

	}

	public Configuration(byte[] data) {
		this.state = data;
	}

	@Override
	public Configuration createCopy() {
		Configuration newC = new Configuration();

		newC.state = Arrays.copyOf(state, state.length);
		return newC;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(state);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj instanceof Configuration) {
			byte[] otherState = ((Configuration)obj).state;
			return Arrays.equals(state, otherState);
		}
		return false;
	}

	@Override
	public String toString() {
		return "RemoteConfig [" + Arrays.toString(state) + "]";
	}

	public void writeOn(OutputStream os) throws IOException {
		os.write(state);
	}
	
}
