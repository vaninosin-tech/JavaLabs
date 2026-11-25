package lab2;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleCalculator {
    private static final String NUMBER = "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?";
    private static final Pattern EXPRESSION = Pattern.compile("^\\s*(" + NUMBER + ")\\s*([+*/-])\\s*(" + NUMBER + ")\\s*$");
    public static double calculate(String expression) {
        var match = EXPRESSION.matcher(expression);
        if (!match.matches()) throw new IllegalArgumentException("Введите выражение, например: 2 * 6.5");
        double a = Double.parseDouble(match.group(1)), b = Double.parseDouble(match.group(3));
        if (!Double.isFinite(a) || !Double.isFinite(b)) throw new IllegalArgumentException("Число вне диапазона");
        double value = switch (match.group(2)) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new IllegalArgumentException("Деление на ноль");
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Неизвестная операция");
        };
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Результат вне диапазона");
        return value;
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("Введите выражение (например, 2 * 6.5):");
            System.out.println(calculate(input.nextLine()));
        } catch (RuntimeException ex) { System.err.println("Ошибка: " + ex.getMessage()); }
    }
}
