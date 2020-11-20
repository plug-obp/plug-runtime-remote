package obp2.language.remote.driver;

import obp2.core.IFiredTransition;
import obp2.core.defaults.FiredTransition;
import obp2.language.remote.protocol.RequestKind;
import obp2.language.remote.runtime.Configuration;
import obp2.language.remote.runtime.RemoteAction;
import obp2.runtime.core.TreeItem;
import plug.utils.marshaling.Marshaller;
import plug.utils.marshaling.Unmarshaller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public void disconnect() throws IOException {
        socket.close();
    }

	private String readString() throws IOException {
        int size = Unmarshaller.readInt(inputStream);
        return size < 0 ? null : new String(Unmarshaller.readData(size, inputStream), StandardCharsets.UTF_8);
	}

	@SuppressWarnings("Duplicates")
    @Override
    public synchronized Set<Configuration> initialConfigurations() {
        Set<Configuration> configurations = new HashSet<>();

        try {
            //send request
            RequestKind.REQ_INITIAL_CONFIGURATIONS.writeOn(outputStream);
            outputStream.flush();

            //read number of configurations
            int numConfigurations = Unmarshaller.readInt(inputStream);

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                //read the configuration size
                int configurationSize = Unmarshaller.readInt(inputStream);
                configurations.add(new Configuration(Unmarshaller.readData(configurationSize, inputStream)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return configurations;
    }

    @Override
    public synchronized Collection<RemoteAction> fireableTransitionsFrom(Configuration configuration) {
        Collection<RemoteAction> fireableTransitions = new ArrayList<>();

        try {
            //send request
            RequestKind.REQ_FIREABLE_TRANSITIONS_FROM.writeOn(outputStream);
            Marshaller.writeInt(configuration.state.length, outputStream);
            configuration.writeOn(outputStream);
            outputStream.flush();

            //read number of transitions
            int numTransitions = Unmarshaller.readInt(inputStream);

            //read number of transitions
            for (int i = 0; i<numTransitions;i++) {
                //read the transitions size
                int transitionSize = Unmarshaller.readInt(inputStream);
                //read the transition
                fireableTransitions.add(new RemoteAction(Unmarshaller.readData(transitionSize, inputStream)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fireableTransitions;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public synchronized IFiredTransition<Configuration, RemoteAction> fireOneTransition(Configuration source, RemoteAction toFire) {
        List<Configuration> configurations = new LinkedList<>();
        byte payload[] = new byte[0];
        try {
            //send request
            RequestKind.REQ_FIRE_TRANSITION.writeOn(outputStream);
            //send source
            Marshaller.writeInt(source.state.length, outputStream);
            source.writeOn(outputStream);
            //sent fireable
            Marshaller.writeInt(toFire.data.length, outputStream);
            toFire.writeOn(outputStream);
            outputStream.flush();

            //read number of configurations
            int numConfigurations = Unmarshaller.readInt(inputStream);

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                //read the configuration size
                int configurationSize = Unmarshaller.readInt(inputStream);
                configurations.add(new Configuration(Unmarshaller.readData(configurationSize, inputStream)));
            }

            //read payload size
            int payloadSize = Unmarshaller.readInt(inputStream);
            //read size data
            payload = Unmarshaller.readData(payloadSize, inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FiredTransition<>(source, configurations, toFire, payload);
    }

    public synchronized int[] registerAtomicPropositions(String[] atomicPropositions) throws IOException {
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

        // reads the registered indexes
        int size = Unmarshaller.readInt(inputStream);
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = Unmarshaller.readInt(inputStream);
        }
        return result;
    }

    @SuppressWarnings("Duplicates")
    public synchronized boolean[] getAtomicPropositionValuations(Configuration configuration) {
        try {
            //send request
            RequestKind.REQ_ATOMIC_PROPOSITION_VALUATIONS.writeOn(outputStream);
            Marshaller.writeInt(configuration.state.length, outputStream);
            configuration.writeOn(outputStream);
            outputStream.flush();

            //read number of values
			int valueCount = Unmarshaller.readInt(inputStream);
            byte[] rawValues = Unmarshaller.readData(valueCount, inputStream);

            boolean[] values = new boolean[valueCount];
            for (int i = 0; i < valueCount; i++) {
                values[i] = rawValues[i] > 0;
            }

            return values;
        } catch (IOException e) {
            e.printStackTrace();
            return new boolean[0];
        }
    }

    @SuppressWarnings("Duplicates")
    public synchronized boolean[] getAtomicPropositionValuations(Configuration source, RemoteAction fireable, Object payload, Configuration target) {
        try {
            //send request
            RequestKind.REQ_EXTENDED_ATOMIC_PROPOSITION_VALUATIONS.writeOn(outputStream);
            //send source
            Marshaller.writeInt(source.state.length, outputStream);
            source.writeOn(outputStream);
            //send the fireable
            if (fireable == null) {
                Marshaller.writeInt(0, outputStream);
            } else {
                Marshaller.writeInt(fireable.data.length, outputStream);
                fireable.writeOn(outputStream);
            }
            //send the payload
            if (payload == null) {
                Marshaller.writeInt(0, outputStream);
            } else {
                byte[] thePayload = (byte[]) payload;
                Marshaller.writeInt(thePayload.length, outputStream);
                outputStream.write(thePayload);
            }
            //send the target
            Marshaller.writeInt(target.state.length, outputStream);
            target.writeOn(outputStream);

            outputStream.flush();

            //read number of values
            int valueCount = Unmarshaller.readInt(inputStream);
            byte[] rawValues = Unmarshaller.readData(valueCount, inputStream);

            boolean[] values = new boolean[valueCount];
            for (int i = 0; i < valueCount; i++) {
                values[i] = rawValues[i] > 0;
            }

            return values;
        } catch (IOException e) {
            e.printStackTrace();
            return new boolean[0];
        }
    }

    private synchronized TreeItem readConfigurationItem() throws IOException {
		String type = readString();
		String name = readString();
		String icon = readString();

		List<TreeItem> children = new ArrayList<>();
		int childrenCount = Unmarshaller.readInt(inputStream);
        for (int i = 0; i < childrenCount; i++) {
            children.add(readConfigurationItem());
        }

    	return new TreeItem(type, name, icon, children);
	}

    @Override
    public synchronized List<TreeItem> getConfigurationItems(Configuration value) {
    	try {
			RequestKind.REQ_CONFIGURATION_ITEMS.writeOn(outputStream);
            Marshaller.writeInt(value.state.length, outputStream);
			value.writeOn(outputStream);
			outputStream.flush();

			//read result
            List<TreeItem> items = new ArrayList<>();
            int itemsCount = Unmarshaller.readInt(inputStream);
            for (int i = 0; i < itemsCount; i++) {
                items.add(readConfigurationItem());
            }
			return items;
		} catch (IOException e) {
			return Collections.emptyList();
		}
    }

    @Override
    public synchronized String getFireableTransitionDescription(RemoteAction transition) {
        try {
            RequestKind.REQ_FIREABLE_TRANSITION_DESCRIPTION.writeOn(outputStream);
            Marshaller.writeInt(transition.data.length, outputStream);
            transition.writeOn(outputStream);
            outputStream.flush();

            //read result
            return readString();
        } catch (IOException e) {
            return "Transition " + Arrays.toString(transition.data);
        }
    }
}
