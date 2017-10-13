package plug.language.remote.protocol;

import java.io.IOException;
import java.io.OutputStream;

public enum RequestKind {
    REQ_INITIAL_CONFIGURATIONS((byte) 1),
    REQ_FIREABLE_TRANSITIONS_FROM((byte) 2),
    REQ_FIRE_TRANSITION((byte) 3);

    private byte value;

    RequestKind(byte value) {
        this.value = value;
    }

    public void writeOn(OutputStream os) throws IOException {
        os.write((byte)1);
        os.write((byte)value);
    }
}
