package obp2.language.remote.protocol;

import java.io.IOException;
import java.io.OutputStream;

public enum RequestKind {
    REQ_INITIAL_CONFIGURATIONS((byte) 1),
    REQ_FIREABLE_TRANSITIONS_FROM((byte) 2),
    REQ_FIRE_TRANSITION((byte) 3),
    REQ_REGISTER_ATOMIC_PROPOSITIONS((byte) 4),
    REQ_ATOMIC_PROPOSITION_VALUATIONS((byte) 5),
    REQ_EXTENDED_ATOMIC_PROPOSITION_VALUATIONS((byte) 6),

    REQ_CONFIGURATION_ITEMS((byte) 10),
    REQ_FIREABLE_TRANSITION_DESCRIPTION((byte) 11);

    private byte value;

    RequestKind(byte value) {
        this.value = value;
    }

    public void writeOn(OutputStream os) throws IOException {
        os.write((byte)1);
        os.write((byte)value);
    }
}
