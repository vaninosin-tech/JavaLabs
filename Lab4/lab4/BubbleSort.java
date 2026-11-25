package lab4;
import java.util.Arrays;
import java.util.Scanner;

public class BubbleSort {
    public static void sort(int[] values) {
        for (int end = values.length - 1; end > 0; end--) {
            boolean swapped = false;
            for (int i = 0; i < end; i++) {
                if (values[i] > values[i + 1]) {
                    int temporary = values[i]; values[i] = values[i + 1]; values[i + 1] = temporary;
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Введите целые числа через пробел:");
            String line = input.nextLine().trim();
            int[] values = line.isEmpty() ? new int[0] : Arrays.stream(line.split("\\s+")).mapToInt(Integer::parseInt).toArray();
            sort(values); System.out.println(Arrays.toString(values));
        } catch (RuntimeException ex) { System.err.println("Нужно вводить целые числа"); }
    }
}
