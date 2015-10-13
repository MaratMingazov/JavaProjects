package lips.aj.stackheap.exmp4;

public class Main {
	public static  int x=0;
	public static void main(String[] args) {
		A(Main.x);
		System.out.println("");
		B();
	}	
	public static void A(int x){
		if (x>2){return;}
		System.out.print(x);
		A(++x);
		System.out.print(x);	
	}
	public static void B(){
		if (Main.x>2){return;}
		System.out.print(Main.x);
		Main.x++;
		B();
		System.out.print(Main.x);			
	}
}
