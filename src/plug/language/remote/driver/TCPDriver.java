package plug.language.remote.driver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import plug.core.view.ConfigurationItem;
import plug.language.remote.protocol.RequestKind;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;

/**
 * Created by Ciprian TEODOROV on 08/09/17.
 */
public class TCPDriver extends AbstractDriver {

    private String address;
    private int port;
    private Socket socket;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;

    public TCPDriver(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() throws IOException {
        // tries to connect several times
        ConnectException exception = null;
        for (int i=0; i<10; i+=1) {
            try {
                System.out.println("Connecting to " + address + ":" + port + " (attempt " + (i+1) + ")");
                socket = new Socket(address, port);
                inputStream = new BufferedInputStream(this.socket.getInputStream());
                outputStream = new BufferedOutputStream(this.socket.getOutputStream());

                // success
                exception = null;
                break;

            } catch (ConnectException e) {
                exception = e;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e1) { /* nothing to do */ }
                continue;
            }

        }

        if (exception != null) {
            throw exception;
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readInt(int size) throws IOException {
		byte[] data = new byte[size];
		inputStream.read(data, 0, size);
		return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	private String readString() throws IOException {
    	int size = readInt(4);
		byte[] data = new byte[size];
		inputStream.read(data, 0, size);
    	return new String(data, StandardCharsets.UTF_8);
	}

    @Override
    public Set<Configuration> initialConfigurations() {
        Set<Configuration> configurations = new HashSet<>();

        try {
            //send request
            RequestKind.REQ_INITIAL_CONFIGURATIONS.writeOn(outputStream);
            outputStream.flush();

            //read number of configurations
            int numConfigurations = readInt(4);
            //read the configuration size
            int configurationSize = readInt(8);

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                byte[] data = new byte[configurationSize];
                inputStream.read(data, 0, configurationSize);
                configurations.add(new Configuration(data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configurations;
    }

    @Override
    public Collection<FireableTransition> fireableTransitionsFrom(Configuration configuration) {
        Collection<FireableTransition> fireableTransitions = new ArrayList<>();

        try {
            //send request
            RequestKind.REQ_FIREABLE_TRANSITIONS_FROM.writeOn(outputStream);
            configuration.writeOn(outputStream);
            outputStream.flush();


            //read number of transitions
            int numTransitions = readInt(4);
            //read the transitions size
            int transitionSize = readInt(8);

            //read number of transitions
            for (int i = 0; i<numTransitions;i++) {
                byte[] data = new byte[transitionSize];
                inputStream.read(data, 0, transitionSize);
                fireableTransitions.add(new FireableTransition(data));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fireableTransitions;
    }

    @Override
    public Set<Configuration> fireOneTransition(Configuration source, FireableTransition toFire) {
        Set<Configuration> configurations = new HashSet<>();
        try {
            //send request
            RequestKind.REQ_FIRE_TRANSITION.writeOn(outputStream);
            source.writeOn(outputStream);
            toFire.writeOn(outputStream);
            outputStream.flush();

            //read number of configurations
            int numConfigurations = readInt(4);
            //read the configuration size
            int configurationSize = readInt(8);

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                byte[] data = new byte[configurationSize];
                inputStream.read(data, 0, configurationSize);
                configurations.add(new Configuration(data));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return configurations;
    }

    public void registerAtomicPropositions(String[] atomicPropositions) {
        try {
            //send request
            RequestKind.REQ_REGISTER_ATOMIC_PROPOSITIONS.writeOn(outputStream);
            ByteBuffer data = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            data.putInt(atomicPropositions.length);
            outputStream.write(data.array());

            for (String atomicProposition : atomicPropositions) {
                byte[] bytes = atomicProposition.getBytes(StandardCharsets.UTF_8);

                data = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
                data.putInt(bytes.length);
                outputStream.write(data.array());

                outputStream.write(bytes);
            }

            outputStream.flush();

            // nothing to wait for
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean[] getAtomicPropositionValuations(Configuration target) {
        try {
            //send request
            RequestKind.REQ_ATOMIC_PROPOSITION_VALUATIONS.writeOn(outputStream);
            target.writeOn(outputStream);
            outputStream.flush();

            //read number of values
			int valueCount = readInt(4);

            byte[] rawValues = new byte[valueCount];
            inputStream.read(rawValues);

            boolean[] values = new boolean[valueCount];
            for (int i = 0; i < valueCount; i++) {
                values[i] = rawValues[i] > 0;
            }

            return values;
        } catch (IOException e) {
            e.printStackTrace();
            return new boolean[] {};
        }
    }

    private ConfigurationItem readConfigurationItem() throws IOException {
		String type = readString();
		String name = readString();
		String icon = readString();

		List<ConfigurationItem> children = new ArrayList<>();
		int childrenCount = readInt(4);
        for (int i = 0; i < childrenCount; i++) {
            children.add(readConfigurationItem());
        }

    	return new ConfigurationItem(type, name, icon, children);
	}

    @Override
    public List<ConfigurationItem> getConfigurationItems(Configuration value) {
    	try {
			RequestKind.REQ_CONFIGURATION_ITEMS.writeOn(outputStream);
			value.writeOn(outputStream);
			outputStream.flush();

			//read result
            List<ConfigurationItem> items = new ArrayList<>();
            int itemsCount = readInt(4);
            for (int i = 0; i < itemsCount; i++) {
                items.add(readConfigurationItem());
            }
			return items;
		} catch (IOException e) {
			return Collections.emptyList();
		}
    }

    @Override
    public String getFireableTransitionDescription(FireableTransition transition) {
        try {
            RequestKind.REQ_FIREABLE_TRANSITION_DESCRIPTION.writeOn(outputStream);
            transition.writeOn(outputStream);
            outputStream.flush();

            //read result
            return readString();
        } catch (IOException e) {
            return "Transition " + Arrays.toString(transition.rawTransitionData);
        }
    }
}
