package lab5;
import java.util.Scanner;

public class LeapYear {
    public static boolean isLeap(int year) {
        if (year <= 0) throw new IllegalArgumentException("Год должен быть положительным");
        return year % 400 == 0 || year % 4 == 0 && year % 100 != 0;
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Введите год григорианского календаря:");
            int year = Integer.parseInt(input.nextLine().trim());
            System.out.println(isLeap(year) ? "Високосный" : "Невисокосный");
        } catch (NumberFormatException ex) { System.err.println("Ошибка: год должен быть целым числом в диапазоне int"); }
        catch (IllegalArgumentException ex) { System.err.println("Ошибка: " + ex.getMessage()); }
    }
}
