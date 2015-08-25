package plugin2.views;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;

public class ExecutionListener implements IExecutionListener {

	@Override
	public void notHandled(String arg0, NotHandledException arg1) {	
	}

	@Override
	public void postExecuteFailure(String arg0, ExecutionException arg1) {
	}

	@Override
	public void postExecuteSuccess(String arg0, Object arg1) {
		
	}

	@Override
	public void preExecute(String arg0, ExecutionEvent arg1) {
	}

}
