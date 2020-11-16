package obp2.language.remote.runtime;

import obp2.core.IFiredTransition;
import obp2.language.remote.driver.TCPDriver;
import obp2.runtime.core.ITransitionRelation;
import obp2.runtime.core.defaults.DefaultLanguageService;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Implementation of the runtime via tcp.
 *
 * @author Valentin Besnard & Ciprian Teodorov
 */
public class RemoteTransitionRelation
        extends DefaultLanguageService<Configuration, FireableTransition,byte[]>
        implements ITransitionRelation<Configuration, FireableTransition,byte[]>
        ,AutoCloseable  {

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
    public RemoteTransitionRelation(String address, int port) {
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
    public void close() throws IOException {
        driver.disconnect();
    }

    @Override
    public boolean hasBlockingTransitions() {
        return true;
    }
}
