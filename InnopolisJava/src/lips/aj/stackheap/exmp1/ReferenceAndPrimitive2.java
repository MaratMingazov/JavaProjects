package lips.aj.stackheap.exmp1;

public class ReferenceAndPrimitive2 {

	public static void main(String[] args) {
		int x1 = 1;
		Integer x2 = 1;
		Integer[] x3 = {1};
		
		
		Increment(x1,x2, x3);
		System.out.println(x1 + " " + x2 + " " + x3[0]);
		
		x1 = Increment(x1);
		x2 = Increment(x2);
		x3 = Increment(x3);
		System.out.println(x1 + " " + x2 + " " + x3[0]);
		
	
	}
	
	public static void Increment(int x1, Integer x2, Integer[] arr){
		++x1;
		++x2;	
		++arr[0];
	}
	
	public static int Increment(int x){
		return ++x;
	}
	
	public static int Increment(Integer x){
		return ++x;
	}	
	
	public static Integer[] Increment(Integer[] x){
		x[0]++;
		return x;
	}		

}
