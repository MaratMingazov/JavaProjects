package an;

public class Dog extends Animal {

	public int DogsCount;
	
	public Dog(String arg){
		name = arg;
		{ 
			int i = 10;
			i++;
		}
		type = "Dog";
		DogsCount++;	
	}
	
	public void voice() {
		System.out.println("bau");
	}
}
