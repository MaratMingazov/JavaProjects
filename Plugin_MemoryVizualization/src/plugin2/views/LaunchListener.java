package plugin2.views;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;

public class LaunchListener implements ILaunchListener {

	@Override
	public void launchRemoved(ILaunch launch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launchAdded(ILaunch launch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launchChanged(ILaunch launch) {
		System.out.println("launch = " + launch);
		
	}

}
