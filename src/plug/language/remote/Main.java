package plug.language.remote;

import java.io.IOException;
import plug.core.IStateSpaceManager;
import plug.explorer.BFSExplorer;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;
import plug.language.remote.runtime.RemoteRuntime;
import plug.statespace.SimpleStateSpaceManager;
import plug.visualisation.StateSpace2TGF;

/**
 * Main class of the program that instantiate an explorer and a runtime to perform an exploration.
 *
 * @author Valentin Besnard & Ciprian Teodorov
 */
public class Main
{
    /**
     * Main function of the program. Explore a model using the BSF explorer.
     * @param arg arguments of the program (not used here).
     * @throws IOException 
     */
    public static final void main(String[] arg) throws IOException
    {
        RemoteRuntime runtime = new RemoteRuntime("localhost", 1234);
        runtime.initializeRuntime();

        IStateSpaceManager<Configuration, FireableTransition> stateSpaceManager = new SimpleStateSpaceManager<>();
        stateSpaceManager.fullConfigurationStorage();
        stateSpaceManager.fullTransitionStorage();

        BFSExplorer explorer = new BFSExplorer(runtime, stateSpaceManager);

        explorer.execute();

        runtime.driver.disconnect();

        //explorer.announcer.when(ExecutionEndedEvent.class, (x, y)->runtime.driver.disconnect());
        
        // Display results of the exploration.
        System.out.println("Exploration finished : numConfigurations = " + stateSpaceManager.size() + "; numTransitions = " + stateSpaceManager.transitionCount());
    
        // Store the graph in a file
        new StateSpace2TGF().toTGF(stateSpaceManager.getGraphView(), true, "PingPongPang.tgf");
    }
}
