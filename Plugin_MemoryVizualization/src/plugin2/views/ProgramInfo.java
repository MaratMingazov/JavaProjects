package plugin2.views;

import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;



public class ProgramInfo extends ViewPart {

	private DebugEventSetListener debugEventSetListener = null;
	private Tree tree;
	
	class SecondaryThread1 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(100); } catch (Exception e) { }
				SecondaryThread2 SThread2 = new SecondaryThread2();
				Thread myThread2 = new Thread(SThread2);	        	 
				Display.getDefault().asyncExec(myThread2);
			}			
		}
	}
	
	class SecondaryThread2 implements Runnable{
		public void run() {
			VizualizateProgramInfo();
		}
	}
	
	
	public ProgramInfo() {
	}

	@Override
	public void createPartControl(Composite parent) {
		tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);		
		tree.setVisible(true);
		
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("");
		columnName.setWidth(300);
		
	
		debugEventSetListener = DebugEventSetListener.GetInstance();
		
		SecondaryThread1 SThread1 = new SecondaryThread1();
		Thread myThread1 = new Thread(SThread1);
		myThread1.start();
	}

	@Override
	public void setFocus() {
	}

	public void VizualizateProgramInfo(){		
		if (debugEventSetListener == null){return;}
		if (!debugEventSetListener.isItIsNewBreakpointHit_ProgramInfo()){return;}
		 
		IJavaThread CurrentThread =  debugEventSetListener.getCurrentThread_ProgramInfo();	
		IStackFrame topFrame = debugEventSetListener.GetTopStackFrame(CurrentThread);		
		
		if (topFrame == null){return;}
		
		for (TreeItem item : tree.getItems()){item.dispose();}
		
		String frameName = debugEventSetListener.GetStackFrameName(topFrame);
		int lineNumber = debugEventSetListener.GetStackFrameLineNumber(topFrame);
		TreeItem item = new TreeItem(tree, SWT.LEFT);
		item.setText(0, "ProgramCounter : " + frameName + " " + lineNumber);	
	}
	
}
