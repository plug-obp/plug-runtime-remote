package plug.language.remote.driver;

import plug.language.remote.protocol.RequestKind;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

    public void connect() {
        try {
            socket = new Socket(address, port);
            inputStream = new BufferedInputStream(this.socket.getInputStream());
            outputStream = new BufferedOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Set<Configuration> initialConfigurations() {
        Set<Configuration> configurations = new HashSet<>();

        try {
            //send request
            RequestKind.REQ_INITIAL_CONFIGURATIONS.writeOn(outputStream);
            outputStream.flush();


            byte data[] = new byte[4];
            //read number of configurations
            inputStream.read(data, 0, 4);
            int numConfigurations = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
            //read the configuration size
            data = new byte[8];
            inputStream.read(data, 0, 8);
            int configurationSize = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                data = new byte[configurationSize];
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


            byte data[] = new byte[4];
            //read number of transitions
            inputStream.read(data, 0, 4);
            int numTransitions = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
            //read the transitions size
            data = new byte[8];
            inputStream.read(data, 0, 8);
            int transitionSize = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();

            //read number of transitions
            for (int i = 0; i<numTransitions;i++) {
                data = new byte[transitionSize];
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


            byte data[] = new byte[4];
            //read number of configurations
            inputStream.read(data, 0, 4);
            int numConfigurations = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
            //read the configuration size
            data = new byte[8];
            inputStream.read(data, 0, 8);
            int configurationSize = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();

            //read number of configurations
            for (int i = 0; i<numConfigurations;i++) {
                data = new byte[configurationSize];
                inputStream.read(data, 0, configurationSize);
                configurations.add(new Configuration(data));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return configurations;
    }
}
