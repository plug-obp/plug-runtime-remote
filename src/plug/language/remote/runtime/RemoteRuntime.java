package plug.language.remote.runtime;

import plug.core.IFiredTransition;
import plug.core.ILanguageRuntime;
import plug.language.remote.driver.TCPDriver;
import plug.statespace.transitions.FiredTransition;

import java.util.Collection;
import java.util.Set;

/**
 * Implementation of the runtime via tcp.
 *
 * @author Valentin Besnard & Ciprian Teodorov
 */
public class RemoteRuntime implements ILanguageRuntime<Configuration, FireableTransition> {

    /**
     * The instance of the driver
     */
    public TCPDriver driver;

    /**
     * Constructor of the ViaTCPRuntime.
     *
     * @param address the IP address of the connection.
     * @param port    the port of the connection.
     */
    public RemoteRuntime(String address, int port) {
        driver = new TCPDriver(address, port);
    }

    /**
     * Initialize the runtime (here nothing to do).
     *
     * @return true if initialized.
     */
    public boolean initializeRuntime() {
        driver.connect();
        return true;
    }

    @Override
    public synchronized Set<Configuration> initialConfigurations() {

        return driver.initialConfigurations();
    }

    @Override
    public synchronized Collection<FireableTransition> fireableTransitionsFrom(Configuration configuration) {
        return driver.fireableTransitionsFrom(configuration);
    }

    @Override
    public synchronized IFiredTransition<Configuration, ?> fireOneTransition(Configuration source, FireableTransition transition) {

        Set<Configuration> target = driver.fireOneTransition(source, transition);

        return new FiredTransition<>(source, target, transition);
    }
}
