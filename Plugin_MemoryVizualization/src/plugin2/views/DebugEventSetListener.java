package plugin2.views;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;

public class DebugEventSetListener implements IDebugEventSetListener {

	private IJavaThread currentThread = null; 
	private boolean itIsUpdatedThread_Stack = false;
	private boolean itIsUpdatedThread_Heap = false;
	private boolean itIsUpdatedThread_ProgramInfo = false;
	private static  DebugEventSetListener UniqueInstance;
	
	public static DebugEventSetListener GetInstance(){
		if (UniqueInstance == null){
			UniqueInstance = new DebugEventSetListener();
		}
		return UniqueInstance;
	}
	
	private DebugEventSetListener() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		IJavaThread thread = null;
		for (DebugEvent event : events){
			if (event.getSource() instanceof IJavaThread){thread = (IJavaThread) event.getSource();}
		}		
		if (thread != null){
			setCurrentThread(thread);
			setItIsUpdated_Stack(true);
			setItIsUpdatedThread_Heap(true);
			setItIsUpdatedThread_ProgramInfo(true);	
		}	
	}
	
	public IJavaThread getCurrentThread_Stack() {
		setItIsUpdated_Stack(false);
		return currentThread;		
	}	
	
	public IJavaThread getCurrentThread_Heap() {
		setItIsUpdatedThread_Heap(false);
		return currentThread;		
	}	
	
	public IJavaThread getCurrentThread_ProgramInfo() {
		setItIsUpdatedThread_ProgramInfo(false);
		return currentThread;		
	}	

	private void setCurrentThread(IJavaThread thread) {
		currentThread = thread;
	}
	
	public boolean isItIsNewBreakpointHit_Stack() {
		return itIsUpdatedThread_Stack;
	}	
	
	public boolean isItIsNewBreakpointHit_Heap() {
		return itIsUpdatedThread_Heap;
	}		

	public boolean isItIsNewBreakpointHit_ProgramInfo() {
		return itIsUpdatedThread_ProgramInfo;
	}
	
	private void setItIsUpdated_Stack(boolean value) {
		itIsUpdatedThread_Stack = value;
	}	
	
	private void setItIsUpdatedThread_Heap(boolean value) {
		itIsUpdatedThread_Heap = value;
	}	
	
	private void setItIsUpdatedThread_ProgramInfo(boolean value) {
		itIsUpdatedThread_ProgramInfo = value;
	}	

	public IStackFrame[] GetStackFrames(IJavaThread thread){
		IStackFrame[] Frames = null;
		try {Frames = thread.getStackFrames();} catch (DebugException e) {}		
		return Frames;
	}
	
	public IStackFrame GetTopStackFrame(IJavaThread thread){
		IStackFrame Frame = null;
		try {Frame = thread.getTopStackFrame();} catch (DebugException e) {}		
		return Frame;
	}

	public String GetStackFrameName(IStackFrame frame){
		String FrameName = "";
		try {FrameName = frame.getName();} catch (DebugException e) {}	
		return FrameName;
	}
	
	public int GetStackFrameLineNumber(IStackFrame frame){
		int LineNumber = 0;
		try {LineNumber = frame.getLineNumber();} catch (DebugException e) {}	
		return LineNumber;
	}	


}
