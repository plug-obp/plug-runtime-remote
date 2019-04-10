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
import plug.utils.marshaling.Marshaller;
import plug.utils.marshaling.Unmarshaller;

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

	private ConfigurationItem readConfigurationItem() throws IOException {
		String type = Unmarshaller.readString(inputStream);
		String name = Unmarshaller.readString(inputStream);
		String icon = Unmarshaller.readString(inputStream);

		List<ConfigurationItem> children = new ArrayList<>();
		int childrenCount = Unmarshaller.readInt(inputStream);
		for (int i = 0; i < childrenCount; i++) {
			children.add(readConfigurationItem());
		}

		return new ConfigurationItem(type, name, icon, children);
	}

	private void writeConfigurationItem(ConfigurationItem item) throws IOException {
		Marshaller.writeString(item.getType(), outputStream);
		Marshaller.writeString(item.getName(), outputStream);
		Marshaller.writeString(item.getIcon(), outputStream);

		Marshaller.writeInt(item.getChildren().size(), outputStream);
		for (ConfigurationItem child : item.getChildren()) {
			writeConfigurationItem(child);
		}
	}

	public void sendConfigurations(Collection<IConfiguration> configurations) throws IOException {
		Marshaller.writeInt(configurations.size(), outputStream);

		for (IConfiguration configuration : configurations) {
			byte[] bytes = serializer.serializeConfiguration(configuration);
			Marshaller.writeInt(bytes.length, outputStream);
			Marshaller.write(bytes, outputStream);
		}
	}

	public void sendTransitions(Collection<Object> transitions) throws IOException {
		Marshaller.writeInt(transitions.size(), outputStream);

		for (Object transition : transitions) {
			byte[] bytes = serializer.serializeTransition(transition);
			Marshaller.writeInt(bytes.length, outputStream);
			Marshaller.write(bytes, outputStream);
		}
	}

	public void handleInitialConfigurations() throws IOException {
		sendConfigurations(runtime.initialConfigurations());
		outputStream.flush();
	}

	public void handleFireableTransitions() throws IOException {
		//read configuration size
		int configurationSize = Unmarshaller.readInt(inputStream);
		//read configuration
		byte[] buffer = Unmarshaller.readData(configurationSize, inputStream);
		IConfiguration configuration = serializer.deserializeConfiguration(buffer);

		//send transitions
		sendTransitions(runtime.fireableTransitionsFrom(configuration));
		outputStream.flush();
	}

	public void handleFireTransition() throws IOException {
		//read configuration size
		int configurationSize = Unmarshaller.readInt(inputStream);
		//read configuration
		byte[] buffer = Unmarshaller.readData(configurationSize, inputStream);
		IConfiguration configuration = serializer.deserializeConfiguration(buffer);

		//read transition size
		int transitionSize = Unmarshaller.readInt(inputStream);
		//read transition
		byte[] transitionBuffer = Unmarshaller.readData(transitionSize, inputStream);
		Object transition = serializer.deserializeTransition(transitionBuffer);

		IFiredTransition<IConfiguration, ?> fired = runtime.fireOneTransition(configuration, transition);
		sendConfigurations(fired.getTargets());

		byte[] payload = serializer.serializePayload(fired.getPayload());
		Marshaller.writeInt(payload.length, outputStream);
		Marshaller.write(payload, outputStream);

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
					//TODO:
					break;

				case 5: // Atomic propositions valuations
					//TODO:
					break;
				case 6: // Extended atomic propositions valuations

				case 10: // Configuration items
					//TODO:
					break;

				case 11: // Fireable transition description
					//TODO:
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
