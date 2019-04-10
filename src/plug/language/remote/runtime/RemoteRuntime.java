package plug.language.remote.runtime;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import plug.core.IAtomicPropositionsEvaluator;
import plug.core.IFiredTransition;
import plug.core.ITransitionRelation;
import plug.language.remote.driver.TCPDriver;
import plug.statespace.transitions.FiredTransition;

/**
 * Implementation of the runtime via tcp.
 *
 * @author Valentin Besnard & Ciprian Teodorov
 */
public class RemoteRuntime implements ITransitionRelation<Configuration, FireableTransition> {

    /**
     * The instance of the driver
     */
    protected final TCPDriver driver;

    protected final RemoteAtomicPropositionsEvaluator atomicPropositionsEvaluator;

    /**
     * Constructor of the ViaTCPRuntime.
     *
     * @param address the IP address of the connection.
     * @param port    the port of the connection.
     */
    public RemoteRuntime(String address, int port) {
        driver = new TCPDriver(address, port);
        atomicPropositionsEvaluator = new RemoteAtomicPropositionsEvaluator(driver);
    }

    public TCPDriver getDriver() {
        return driver;
    }

    /**
     * Initialize the runtime (here nothing to do).
     *
     * @throws IOException when connection failed
     */
    public void initializeRuntime() throws IOException {
        driver.connect();
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
    public synchronized IFiredTransition<Configuration, FireableTransition> fireOneTransition(Configuration source, FireableTransition transition) {
        return driver.fireOneTransition(source, transition);
    }

    @Override
    public IAtomicPropositionsEvaluator getAtomicPropositionEvaluator() {
        return atomicPropositionsEvaluator;
    }

    @Override
    public void close() {
        driver.disconnect();
    }
}
