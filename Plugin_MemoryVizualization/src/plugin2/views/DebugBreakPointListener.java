package plugin2.views;


import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpointListener;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.JDIDebugModel;

public class DebugBreakPointListener implements IJavaBreakpointListener{


	private IJavaThread currentThread = null; 
	private boolean itIsNewBreakpointHit_Stack = false;
	private boolean itIsNewBreakpointHit_Heap = false;
	private boolean ItIsNewBreakpointHit_ProgramInfo = false;
	private static DebugBreakPointListener UniqueInstance;
	
	public static DebugBreakPointListener GetInstance(){
		if (UniqueInstance == null){
			UniqueInstance = new DebugBreakPointListener();
		}
		return UniqueInstance;
	}
	
	private DebugBreakPointListener() {
		JDIDebugModel.addJavaBreakpointListener(this);
	}

	@Override
	public void addingBreakpoint(IJavaDebugTarget arg0, IJavaBreakpoint arg1) {
		
	}

	@Override
	public void breakpointHasCompilationErrors(IJavaLineBreakpoint arg0, Message[] arg1) {
	}

	@Override
	public void breakpointHasRuntimeException(IJavaLineBreakpoint arg0, DebugException arg1) {
	}

	@Override
	public int breakpointHit(IJavaThread arg0, IJavaBreakpoint arg1) {
		setCurrentThread(arg0);
		setItIsNewBreakpointHit_Stack(true);
		setItIsNewBreakpointHit_Heap(true);
		setItIsNewBreakpointHit_ProgramInfo(true);
		return 0;
	}

	@Override
	public void breakpointInstalled(IJavaDebugTarget arg0, IJavaBreakpoint arg1) {
	}

	@Override
	public void breakpointRemoved(IJavaDebugTarget arg0, IJavaBreakpoint arg1) {

	}

	@Override
	public int installingBreakpoint(IJavaDebugTarget arg0, IJavaBreakpoint arg1, IJavaType arg2) {
		return 0;
	}
	
	public IJavaThread getCurrentThread_Stack() {
		setItIsNewBreakpointHit_Stack(false);
		return currentThread;		
	}	
	
	public IJavaThread getCurrentThread_Heap() {
		setItIsNewBreakpointHit_Heap(false);
		return currentThread;		
	}	
	
	public IJavaThread getCurrentThread_ProgramInfo() {
		setItIsNewBreakpointHit_ProgramInfo(false);
		return currentThread;		
	}	

	private void setCurrentThread(IJavaThread thread) {
		currentThread = thread;
	}
	
	public boolean isItIsNewBreakpointHit_Stack() {
		return itIsNewBreakpointHit_Stack;
	}	
	
	public boolean isItIsNewBreakpointHit_Heap() {
		return itIsNewBreakpointHit_Heap;
	}		

	public boolean isItIsNewBreakpointHit_ProgramInfo() {
		return ItIsNewBreakpointHit_ProgramInfo;
	}
	
	private void setItIsNewBreakpointHit_Stack(boolean value) {
		itIsNewBreakpointHit_Stack = value;
	}	
	
	private void setItIsNewBreakpointHit_Heap(boolean value) {
		itIsNewBreakpointHit_Heap = value;
	}	
	
	private void setItIsNewBreakpointHit_ProgramInfo(boolean value) {
		ItIsNewBreakpointHit_ProgramInfo = value;
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
