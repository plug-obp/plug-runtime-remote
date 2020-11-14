package obp2.language.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteResource {

	// command to execute for a distant runtime
	private final String[] command;

	// host of the distant runtime
	private final String host;
	// port to connect to the runtime
	private final int port;

	public RemoteResource(
			@JsonProperty("command")
			String[] command,
			@JsonProperty(value = "host", defaultValue = "")
			String host,
			@JsonProperty(value = "port", defaultValue = "0")
			int port)
	{
		this.command = command;
		this.host = host;
		this.port = port;
	}

	public String[] getCommand() {
		return command;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
}
