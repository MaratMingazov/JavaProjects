package lips.aj.interfaces.exmp3;

public interface OperateCar {

    default public int startEngine(int key) {
    	System.out.println("OperateCar");
		return 0;
    }
}
