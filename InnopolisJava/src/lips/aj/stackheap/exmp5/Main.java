package lips.aj.stackheap.exmp5;

public class Main {
	public static void main(String[] args) {
		A <Integer> a = new A<>(1);
		A <String> b = new A<>("1");		
		System.out.println(a.getValue()+1);
		System.out.println(b.getValue()+1);
	}
}
