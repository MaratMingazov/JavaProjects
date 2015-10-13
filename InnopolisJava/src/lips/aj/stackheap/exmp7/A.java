package lips.aj.stackheap.exmp7;

public class A {
	private int x;
	private static int y;	
	public A(){
		x++;
		y++;
	}
	private  void M1(int x, int y){
		this.x = x;
		A.y = y;
	}
}
