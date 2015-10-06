package lips.aj.jni;



public class Callbacks {

	static{
		System.loadLibrary("libInnopolisCppDll");
	}
	private native void nativeMethod(int depth);
		
	public static void main(String[] args) {
		Callbacks c = new Callbacks();
		c.nativeMethod(0);
	}
	
	private void callback(int depth){
		if (depth < 5){       
			System.out.println("In Java, depth = " + depth + ", about to enter C");
			nativeMethod(depth+1);
			System.out.println("In Java, depth = " + depth + ", back from C");
		}else{
			System.out.println("In Java, depth = " + depth + ", limit exceeded");
		}
	}

}
