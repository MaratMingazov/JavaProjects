package lips.aj.stackheap.exmp6;

public class A {
	static Runnable r = null;
	public static void B(){
		final int k = 4;
		r = () -> System.out.println(k);
	}
}
