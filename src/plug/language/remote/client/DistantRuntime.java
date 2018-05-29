package plug.language.remote.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import plug.core.IConfiguration;
import plug.core.IFiredTransition;
import plug.core.ITransitionRelation;
import plug.core.RuntimeDescription;
import plug.core.view.ConfigurationItem;

public class DistantRuntime {

	protected final String address;

	protected final int port;

	protected final RuntimeDescription description;

	protected final IRuntimeSerializer serializer;

	protected ServerSocket serverSocket;

	protected Socket connectedSocket;

	protected BufferedInputStream inputStream;

	protected BufferedOutputStream outputStream;

	protected ITransitionRelation<IConfiguration, Object> runtime;

	public DistantRuntime(
			int port, RuntimeDescription description,
			IRuntimeSerializer serializer
	) {
		this("127.0.0.1", port, description, serializer);
	}

	public DistantRuntime(
			String address, int port,
			RuntimeDescription description, IRuntimeSerializer serializer
	) {
		this.address = address;
		this.port = port;
		this.description = description;
		this.serializer = serializer;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public RuntimeDescription getDescription() {
		return description;
	}

	public void connect() throws Exception {
		serverSocket = new ServerSocket(port);
		connectedSocket = serverSocket.accept();

		inputStream = new BufferedInputStream(connectedSocket.getInputStream());
		outputStream = new BufferedOutputStream(connectedSocket.getOutputStream());

		runtime = description.getRuntime();
	}

	private byte[] readData(int size) throws IOException {
		byte[] data = new byte[size];
		int read = 0;
		do { read += inputStream.read(data, 0, size); } while (read < size);
		return data;
	}

	private int readInt() throws IOException {
		return ByteBuffer.wrap(readData(4)).order(ByteOrder.LITTLE_ENDIAN).getInt();
	}

	private long readLong() throws IOException {
		return ByteBuffer.wrap(readData(8)).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

	private String readString() throws IOException {
		int size = readInt();
		return size < 0 ? null : new String(readData(size), StandardCharsets.UTF_8);
	}

	private ConfigurationItem readConfigurationItem() throws IOException {
		String type = readString();
		String name = readString();
		String icon = readString();

		List<ConfigurationItem> children = new ArrayList<>();
		int childrenCount = readInt();
		for (int i = 0; i < childrenCount; i++) {
			children.add(readConfigurationItem());
		}

		return new ConfigurationItem(type, name, icon, children);
	}

	private void writeData(byte[] data) throws IOException {
		if (data != null) {
			outputStream.write(data);
		}
	}

	private void writeInt(int value) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(value);
		writeData(buffer.array());
	}

	private void writeLong(long value) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
		buffer.putLong(value);
		writeData(buffer.array());
	}

	private void writeString(String value) throws IOException {
		byte[] bytes = value != null ? value.getBytes(StandardCharsets.UTF_8) : null;
		if (bytes != null) {
			writeInt(bytes.length);
			writeData(bytes);
		} else {
			writeInt(0);
		}
	}

	private void writeConfigurationItem(ConfigurationItem item) throws IOException {
		writeString(item.getType());
		writeString(item.getName());
		writeString(item.getIcon());

		writeInt(item.getChildren().size());
		for (ConfigurationItem child : item.getChildren()) {
			writeConfigurationItem(child);
		}
	}

	public void sendConfigurations(Collection<IConfiguration> configurations) throws IOException {
		writeInt(configurations.size());
		writeLong(serializer.getConfigurationSize());

		for (IConfiguration configuration : configurations) {
			writeData(serializer.serializeConfiguration(configuration));
		}
	}

	public void sendTransitions(Collection<Object> transitions) throws IOException {
		writeInt(transitions.size());
		writeLong(serializer.getTransitionSize());

		for (Object transition : transitions) {
			writeData(serializer.serializeTransition(transition));
		}
	}

	public void handleInitialConfigurations() throws IOException {
		sendConfigurations(runtime.initialConfigurations());
		outputStream.flush();
	}

	public void handleFireableTransitions() throws IOException {
		byte[] buffer = readData(serializer.getConfigurationSize());
		IConfiguration configuration = serializer.deserializeConfiguration(buffer);
		sendTransitions(runtime.fireableTransitionsFrom(configuration));
		outputStream.flush();
	}

	public void handleFireTransition() throws IOException {
		byte[] configurationBuffer = readData(serializer.getConfigurationSize());
		IConfiguration configuration = serializer.deserializeConfiguration(configurationBuffer);

		byte[] transitionBuffer = readData(serializer.getTransitionSize());
		Object transition = serializer.deserializeTransition(transitionBuffer);

		IFiredTransition<IConfiguration, ?> fired = runtime.fireOneTransition(configuration, transition);
		sendConfigurations(fired.getTargets());
		outputStream.flush();
	}

	public boolean handleRequest() throws IOException {
		int status = inputStream.read();
		if (status == 1) {
			byte request = (byte) inputStream.read();
			switch (request) {
				case 1: // Initial Configurations
					handleInitialConfigurations();
					break;

				case 2: // Fireable transitions
					handleFireableTransitions();
					break;

				case 3: // Fire transition
					handleFireTransition();
					break;

				case 4: // Register atomic propositions
					break;

				case 5: // Atomic proposition valuations
					break;

				case 10: // Configuration items
					readData(serializer.getConfigurationSize());
					writeInt(0);
					outputStream.flush();
					break;

				case 11: // Fireable transition description
					Object transition = serializer.deserializeTransition(readData(serializer.getTransitionSize()));
					writeString(transition.toString());
					outputStream.flush();
					break;
			}
			return true;
		}
		return false;
	}


	public void close() throws IOException {
		serverSocket.close();
	}
}
