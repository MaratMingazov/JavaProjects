package lips.aj.interfaces.exmp2;

public interface Animal {
    default public String identifyMyself() {
        return "I am an animal.";
    }
}
