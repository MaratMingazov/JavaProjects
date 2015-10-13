package lips.aj.stackheap.exmp3;

public class Main {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		A.B ab = new A.B();		
		A a = new A();
		A.C ac1 = a.new C();
		A.C ac2 = a.new C();
		System.out.println(A.x + " " +  a.y);	
	}
}
