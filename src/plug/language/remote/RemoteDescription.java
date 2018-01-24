package plug.language.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteDescription {

	// command to execute for a distant runtime
	private final String[] command;
	// delay to wait before connecting
	private final int delay;

	// host of the distant runtime
	private final String host;
	// port to connect to the runtime
	private final int port;

	public RemoteDescription(
			@JsonProperty("command")
			String[] command,
			@JsonProperty("delay")
			int delay,
			@JsonProperty(value = "host", defaultValue = "")
			String host,
			@JsonProperty(value = "port", defaultValue = "0")
			int port)
	{
		this.command = command;
		this.delay = delay;
		this.host = host;
		this.port = port;
	}

	public String[] getCommand() {
		return command;
	}

	public int getDelay() {
		return delay;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
