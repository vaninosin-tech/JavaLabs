package lab1.animals;

public abstract class Animal implements Feedable {
    private final String name;
    private int energy = 50;
    protected Animal(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Нужно имя");
        this.name = name;
    }
    public String getName() { return name; }
    public int getEnergy() { return energy; }
    @Override public void feed() { energy = Math.min(100, energy + 20); }
    public abstract String sound();
}
