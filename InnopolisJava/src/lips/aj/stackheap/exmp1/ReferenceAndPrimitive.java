package lips.aj.stackheap.exmp1;

public class ReferenceAndPrimitive {

	public static void main(String[] args) {
		int x = 1;
		int[] arr = {1};
		
		Increment1(x, arr);
		System.out.println(x + " " + arr[0]);	
		
		Increment2(x, arr);
		System.out.println(x + " " + arr[0]);	
	}
	
	public static void Increment1(int x, int[] arr){
		x++;
		arr[0]++;		
	}
	
	public static void Increment2(int x, int[] arr){
		x++;
		arr = new int[]{arr[0]+1};	
	}
}
