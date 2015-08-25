package an;

public class Dog extends Animal {

	public int DogsCount;
	
	public Dog(String arg){
		name = arg;
		type = "Dog";
		DogsCount++;	
	}
	
	public void voice() {
		System.out.println("bau");
	}
}
