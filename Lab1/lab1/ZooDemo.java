package lab1;
import lab1.animals.*;
import lab1.world.Zoo;

public class ZooDemo {
    public static void main(String[] args) {
        Zoo zoo = new Zoo(); zoo.add(new Lion("Лев")); zoo.add(new Parrot("Кеша"));
        zoo.feedAll();
        for (Animal animal : zoo.animals())
            System.out.println(animal.getName() + ": " + animal.sound() + " Энергия: " + animal.getEnergy());
    }
}
