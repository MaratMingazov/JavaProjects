package plugin2.views;

import org.eclipse.cdt.debug.core.cdi.model.ICDIInstruction;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.mi.core.cdi.Session;
import org.eclipse.debug.core.DebugPlugin;
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

	private DebugEventListener jdiEventListener = null;
	private CDIEventListener cdiEventListener = null;
	private Session cdiDebugSession = null;
	private Tree tree;
	
	class RunnableForThread2 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(1000); } catch (Exception e) { }
				Runnable task = () -> { vizualizateProgramInfoJava();vizualizateProgramInfoCpp();};
				Display.getDefault().asyncExec(task);
			}			
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
		
		jdiEventListener = new DebugEventListener();
		DebugPlugin.getDefault().addDebugEventListener(jdiEventListener);
		
		this.cdiEventListener		= new CDIEventListener();
		tryGetCdiSession();
		
		Runnable runnable = new RunnableForThread2();
		Thread Thread2 = new Thread(runnable);
		Thread2.start();	
	}
	
	private void tryGetCdiSession(){	
		Session session = CDIDebugger.getSession();
		if (session == null){return;}
		if (session.equals(this.cdiDebugSession)){return;}
		else{
			this.cdiDebugSession = session;
			if (this.cdiDebugSession != null){this.cdiDebugSession.getEventManager().addEventListener(this.cdiEventListener);}	
		}
	}

	@Override
	public void setFocus() {
	}

	public void vizualizateProgramInfoJava(){	
		if(jdiEventListener == null){return;}		
		if (!jdiEventListener.isItUpdatedThread()){return;}

		IJavaThread CurrentThread =  jdiEventListener.getCurrentThread();	
		IStackFrame topFrame = DebugEventListener.getTopStackFrame(CurrentThread);		
		
		if (topFrame == null){return;}
		
		for (TreeItem item : tree.getItems()){item.dispose();}
				
		String frameName = DebugEventListener.getStackFrameName(topFrame);
		int lineNumber = DebugEventListener.getStackFrameLineNumber(topFrame);
		
		TreeItem item = new TreeItem(tree, SWT.LEFT);
		item.setText(0, "ProgramCounter : " + frameName + " " + lineNumber);	

		TreeItem item2 = new TreeItem(tree, SWT.LEFT);
		item2.setText(0, "StackPointer : ");	
		
		
	}
	
	public void vizualizateProgramInfoCpp(){
		tryGetCdiSession();
		if (cdiEventListener ==null){return;}
		if (!cdiEventListener.isItUpdatedThread()){return;}
		
		ICDIThread CurrentThread =  cdiEventListener.getCurrentThread();	
		ICDIStackFrame[] frames = CDIEventListener.getStackFrames(CurrentThread);		
		
		for (TreeItem item : tree.getItems()){item.dispose();}
		
		
		
		
		for (ICDIStackFrame frame : frames){
			TreeItem item = new TreeItem(tree, SWT.LEFT);
			item.setText(0, frame.getLocator().getFile() + " / " + frame.getLocator().getFunction());	
			ICDIInstruction[] instructions = CDIEventListener.getInstructions(frame);
			for (ICDIInstruction instruction : instructions){
				String instr = instruction.getInstruction();
				TreeItem subitem = new TreeItem(item, SWT.LEFT);
				subitem.setText(0, instr);
			}
		}

		
	}
	
}
