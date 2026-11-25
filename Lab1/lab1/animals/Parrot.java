package lab1.animals;
public final class Parrot extends Animal {
    public Parrot(String name) { super(name); }
    @Override public String sound() { return "Привет!"; }
}
