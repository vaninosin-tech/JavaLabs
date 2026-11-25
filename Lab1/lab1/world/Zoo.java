package lab1.world;
import lab1.animals.Animal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Zoo {
    private final List<Animal> animals = new ArrayList<>();
    public void add(Animal animal) { animals.add(Objects.requireNonNull(animal)); }
    public List<Animal> animals() { return List.copyOf(animals); }
    public void feedAll() { animals.forEach(Animal::feed); }
}
