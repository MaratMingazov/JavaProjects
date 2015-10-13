package lips.aj.stackheap.exmp2;

public class Main {
	public static void main(String[] args) {
		A a = new A();
		B b = new B();
		A c = new B();		
		System.out.println(a.x + " " + A.y);
		System.out.println(b.x + " " + B.y);
		System.out.println(c.x);
	}
	
}

