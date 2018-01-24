package plug.language.remote.runtime;

import announce4j.Announcer;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import plug.core.IAtomicPropositionsEvaluator;
import plug.core.IExecutionController;
import plug.core.IFiredTransition;
import plug.core.ILanguageRuntime;
import plug.events.ExecutionEndedEvent;
import plug.language.remote.driver.TCPDriver;
import plug.statespace.transitions.FiredTransition;

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
     * The execution controller
     */
    private IExecutionController<Configuration, ?> executionController;

    private RemoteAtomicPropositionsEvaluator atomicPropositionsEvaluator;

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
        try {
            driver.connect();
            atomicPropositionsEvaluator = new RemoteAtomicPropositionsEvaluator(driver);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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

    @Override
    public void setAnnouncer(Announcer announcer) {
        announcer.when(ExecutionEndedEvent.class, (sender, event) -> {
            driver.disconnect();
        });
    }

    @Override
    public IAtomicPropositionsEvaluator getAtomicPropositionEvaluator() {
        return atomicPropositionsEvaluator;
    }
}
