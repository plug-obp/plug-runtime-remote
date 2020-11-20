package obp2.language.remote.runtime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Class for a fireable transition of the model.
 * @author valentin
 */
public class RemoteAction
{

	public final byte data[];

	public RemoteAction(byte[] data) {
		this.data = data;
	}

	public void writeOn(OutputStream os) throws IOException {
		os.write(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RemoteAction) {
			RemoteAction other = (RemoteAction) obj;
			return Arrays.equals(this.data, other.data);
		}
		return false;
	}

	@Override
	public String toString() {
		return "RemoteAction {" +
				"data=" + Arrays.toString(data) +
				'}';
	}
}
