package plug.language.remote.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import plug.core.defaults.DefaultConfiguration;

/**
 * Class for configuration of the model.
 * @author valentin
 */
public class Configuration extends DefaultConfiguration<Configuration> {
	public final byte[] state;

	public Configuration(byte[] data) {
		this.state = data;
	}

	@Override
	public Configuration createCopy() {
		return new Configuration(Arrays.copyOf(state, state.length));
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
