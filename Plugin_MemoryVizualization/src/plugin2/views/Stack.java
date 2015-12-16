




package plugin2.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.*;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;

import java.util.ArrayList;

import org.eclipse.cdt.debug.core.CDebugCorePlugin;
import org.eclipse.cdt.debug.core.cdi.ICDISession;
import org.eclipse.cdt.debug.core.cdi.model.ICDILocalVariableDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegister;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterDescriptor;
import org.eclipse.cdt.debug.core.cdi.model.ICDIRegisterGroup;
import org.eclipse.cdt.debug.core.cdi.model.ICDIStackFrame;
import org.eclipse.cdt.debug.core.cdi.model.ICDIThread;
import org.eclipse.cdt.debug.core.cdi.model.ICDIValue;
import org.eclipse.cdt.debug.core.cdi.model.ICDIVariable;
import org.eclipse.cdt.debug.mi.core.cdi.Session;
import org.eclipse.cdt.debug.mi.core.cdi.model.Variable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaThread;




public class Stack extends ViewPart {


	private CDIEventListener cdiEventListener = null;
	private Session cdiDebugSession = null;
	private Tree tree = null;

	class RunnableForThread2 implements Runnable{
		public void run() {
			while (true) {
				try { Thread.sleep(1000); } catch (Exception e) { }
				Runnable task = () -> { VizualizateStack—pp();};
				Display.getDefault().asyncExec(task);
			}			
		}
	}
	
	public void createPartControl(Composite parent) {


		//parent.setLayout(new GridLayout(2, false));
	   // Text text1 = new Text(parent, SWT.NONE);
	   // Text text2 = new Text(parent, SWT.NONE );
		
	    AnchorPane p = new AnchorPane();


	    CreateMemoryArea(10, 10, 100, 300, "Stack", p);
	    CreateMemoryArea(120, 10, 300, 300, "Heap", p);
	
		/*
	    HBox hbox = new HBox();
		hbox.setTranslateX(10);
		hbox.setTranslateY(10);
		
		Rectangle r = new Rectangle(20,20,50,50);
		r.setFill(Color.WHITE);
		r.setStroke(Color.BLACK);
		r.setStrokeWidth(1);
	
		hbox.getChildren().add(r);
		p.getChildren().add(hbox);
		
		
		 hbox = new HBox();
		hbox.setTranslateX(12);
		hbox.setTranslateY(10);		
		Text t1 = new Text();
		t1.setText("main:");
		hbox.getChildren().add(t1);
		p.getChildren().add(hbox);	
		*/
	    Scene scene = new Scene(p);

	   // parent.setLayout(new FillLayout());
	   FXCanvas fxCanvas = new FXCanvas(parent, SWT.NONE);

	    fxCanvas.setScene(scene);
    
	    
	    //Scene s = new Scene
	   // parent.setLayoutData(p);
		//createTree(parent);
		
		
		//this.cdiEventListener		= new CDIEventListener();
		//tryGetCdiSession();
		
		
		
		//Runnable runnable = new RunnableForThread2();
		//Thread Thread2 = new Thread(runnable);
		//Thread2.start();
		//System.out.println("go");
	}
	
	
	private void CreateMemoryArea(int dX, int dY, int Width, int Height, String Name, Pane root){
		CreateTextArea(dX, dY, Name, root);
		createRectangleArea(dX, dY*3, Width, Height, root);
	}	
	
	private void CreateTextArea(int TranslateX, int TranslateY, String setText, Pane root){

		HBox hbox = new HBox();
		hbox.setTranslateX(TranslateX);
		hbox.setTranslateY(TranslateY);

		Text t1 = new Text();
		t1.setText(setText);
		hbox.getChildren().add(t1);
		root.getChildren().add(hbox);	
	}	
	
	private void createRectangleArea(int dX, int dY, int width, int height, Pane root){
		
		HBox hbox = new HBox();
		hbox.setTranslateX(dX);
		hbox.setTranslateY(dY);
		
		Rectangle r = new Rectangle(0,0,width,height);
		r.setFill(Color.WHITE);
		r.setStroke(Color.BLACK);
		r.setStrokeWidth(2);
	
		hbox.getChildren().add(r);
		root.getChildren().add(hbox);
	}
	

	@Override
	public void setFocus() {
	}	
	
	private void createTree(Composite parent){
		this.tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		this.tree.setHeaderVisible(true);
		this.tree.setLinesVisible(true);		
		this.tree.setVisible(true);
		
		TreeColumn columnName = new TreeColumn(this.tree, SWT.LEFT);
		columnName.setText("frames:");
		columnName.setWidth(300);
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
	
	private void VizualizateStack—pp(){		
		tryGetCdiSession();
		if (cdiEventListener ==null){return;}
		if (!cdiEventListener.isItUpdatedThread()){return;}
		
		ICDIThread CurrentThread =  cdiEventListener.getCurrentThread();	
		ICDIStackFrame[] Frames = CDIEventListener.getStackFrames(CurrentThread);		
		
		for (TreeItem item : tree.getItems()){item.dispose();}
	
		for (int i = 0; i< Frames.length; i++){

			
			ICDIStackFrame frame = Frames[i];
			String FrameName 	= frame.getLocator().getFunction();
			String Location		= frame.getLocator().getFile();

			TreeItem item = new TreeItem(tree, SWT.LEFT);
			item.setText(0, Location + " " + FrameName);			
			
			TreeItem subItem;
			
			ICDIValue registerInstructionPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rip");
			String registerInstructionPointerString = CDIEventListener.getValueString(registerInstructionPointer);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "InstructionPointer : " + registerInstructionPointerString);			
			
			ICDIValue registerBasePointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rbp");
			String registerBasePointerString = CDIEventListener.getValueString(registerBasePointer);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "BasePointer : " + registerBasePointerString);	
	
			
			
			ICDIValue registerStackPointer = CDIEventListener.findRegisterValueByQualifiedName(frame, "$rsp");
			String registerStackPointerString = CDIEventListener.getValueString(registerStackPointer);		
		
			ArrayList<ICDIVariable> varlist = new ArrayList<ICDIVariable>();
			ICDILocalVariableDescriptor[] descriptors = CDIEventListener.GetStackFrameLocalVariableDescriptors(frame);
			ICDIVariable[] variables = new ICDIVariable[descriptors.length];
			for (int k = 0; k<descriptors.length; k++){variables[k] = CDIEventListener.getLocalVariable(descriptors[k]);}
			fillVarList(varlist, variables);
			
			for (ICDIVariable cdiVariable : varlist){
				Variable variable = (Variable)cdiVariable;
					
				ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
				String valuestring			= CDIEventListener.getValueString(value);
				String QualifiedName		= CDIEventListener.getQualifiedName(variable);
				String hexAddress = CDIEventListener.getHexAddress(variable);
				
				if (hexAddress.compareTo(registerStackPointerString) >=0  && hexAddress.compareTo(registerBasePointerString) <=0 ){
					subItem = new TreeItem(item, SWT.LEFT);
					subItem.setText(0, hexAddress + " : " + valuestring + " (" + QualifiedName + ")");	
				}		
			}
	
			ICDIValue eax = CDIEventListener.findRegisterValueByQualifiedName(frame, "$eax");
			String eaxString = CDIEventListener.getValueString(eax);		
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "Return value : " + eaxString);				
			
			subItem = new TreeItem(item, SWT.LEFT);
			subItem.setText(0, "StackPointer : " + registerStackPointerString);				
		}
	}	
	
	private boolean isExist(ArrayList<ICDIVariable> varlist, ICDIVariable variable){
		Variable v = (Variable)variable;
		String hexAddress1 = CDIEventListener.getHexAddress(v);
		for (ICDIVariable var : varlist){
			Variable variab = (Variable)var;
			String hexAddress2 = CDIEventListener.getHexAddress(variab);
			if (hexAddress1.equals(hexAddress2)){return true;}	
		}
		return false;
	}
	
	private void fillVarList(ArrayList<ICDIVariable> varlist, ICDIVariable[] variables){		
		for (ICDIVariable variable : variables){
			if (!isExist(varlist, variable)){varlist.add(variable);}
			ICDIValue value 			= CDIEventListener.getLocalVariableValue(variable);
			ICDIVariable[] subvariables = CDIEventListener.getLocalVariablesFromValue(value);
			fillVarList(varlist, subvariables);
		}
	}
	
	private void vizualizateCdiVariables(TreeItem item, ICDIVariable[] variables){
		if (variables == null){return;}
		for (ICDIVariable lvariable : variables){
			Variable variable = (Variable)lvariable;	
			
			ICDIValue value 			= CDIEventListener.getLocalVariableValue(lvariable);
			String valuestring			= CDIEventListener.getValueString(value);
			String typename				= CDIEventListener.getLocalVariableTypeName(lvariable);
			String QualifiedName		= CDIEventListener.getQualifiedName(lvariable);
			TreeItem subItem = new TreeItem(item, SWT.LEFT);
			String hexAddress = CDIEventListener.getHexAddress(variable);
			
			subItem.setText(0,typename + " " + QualifiedName + " : " + valuestring + " address: " + hexAddress);
			vizualizateCdiVariables(subItem, CDIEventListener.getLocalVariablesFromValue(value));		
		}
	}

}