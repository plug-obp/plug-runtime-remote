package plug.language.remote;

import javafx.scene.control.TreeItem;
import javax.swing.tree.TreeNode;
import plug.core.ILanguageRuntime;
import plug.core.IRuntimeView;
import plug.language.remote.runtime.Configuration;
import plug.language.remote.runtime.FireableTransition;
import plug.language.remote.runtime.RemoteRuntime;


/**
 * Created by Ciprian TEODOROV on 03/03/17.
 */
public class RemoteRuntimeView implements IRuntimeView<Configuration, FireableTransition> {
	RemoteRuntime runtime;

    public RemoteRuntimeView(RemoteRuntime runtime) {
        this.runtime = runtime;
    }

	@Override
	public ILanguageRuntime<Configuration, FireableTransition> getRuntime() {
    	return runtime;
	}

	@Override
	public TreeNode getConfigurationTreeModel(Configuration value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeItem getConfigurationTreeModelFx(Configuration value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeNode getFireableTransitionTreeModel(FireableTransition transition) {
		// TODO Auto-generated method stub
		return null;
	}



   
}
