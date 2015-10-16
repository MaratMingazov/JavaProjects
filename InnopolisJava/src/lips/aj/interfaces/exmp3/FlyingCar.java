package lips.aj.interfaces.exmp3;

public class FlyingCar implements OperateCar, FlyCar {

	public int startEngine(int key) {
        FlyCar.super.startEngine(key);
        OperateCar.super.startEngine(key);
        return 0;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FlyingCar car = new FlyingCar();
		car.startEngine(0);
	}

}
