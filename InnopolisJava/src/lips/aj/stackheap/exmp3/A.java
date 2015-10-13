package lips.aj.stackheap.exmp3;

public class A {
	public static int x = 0;
	public int y = 0;
	public A(){
		System.out.println("A");
	}
	public static class B{
		public B(){
			System.out.println("B");
			x++;
		}
	}
	public class C{
		public C(){
			System.out.println("C");
			x++;
			y++;
		}
	}
}
