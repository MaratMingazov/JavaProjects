package lips.aj.interfaces.exmp3;

public interface FlyCar {
    // ...
    default public int startEngine(int key) {
    	System.out.println("FlyCar");
		return 1;
    }
}
