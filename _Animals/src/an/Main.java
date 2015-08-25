package an;

public class Main {


	@SuppressWarnings("unused")
	public static void main(String[] args) {	
		Animal cat1 = new Cat("Baby");
		Animal cat2 = new Cat("Baby");
		Animal dog1 = new Dog("Rocky");	
		System.out.println("finish");
		Dog d = new Dog("Ruf");
		d.voice();
	}

}
