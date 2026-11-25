package lab3;
import java.util.Scanner;

public class CompoundInterest {
    private static void check(double amount, int periods) {
        if (!Double.isFinite(amount) || amount <= 0 || periods < 0)
            throw new IllegalArgumentException("Сумма > 0; число периодов >= 0");
    }
    public static double amount(double principal, double percent, int periods) {
        check(principal, periods);
        if (!Double.isFinite(percent) || percent <= -100) throw new IllegalArgumentException("Процент > -100");
        return finite(principal * Math.pow(1 + percent / 100, periods));
    }
    public static double rate(double principal, double target, int periods) {
        check(principal, periods); check(target, periods);
        if (periods == 0) throw new IllegalArgumentException("Для обратного расчета N > 0");
        return finite(Math.expm1((Math.log(target) - Math.log(principal)) / periods) * 100);
    }
    private static double finite(double value) {
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Результат вне диапазона");
        return value;
    }
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in).useLocale(java.util.Locale.US)) {
            System.out.println("1 — итоговая сумма, 2 — необходимый процент. Дробная часть через точку.");
            int mode = input.nextInt();
            if (mode != 1 && mode != 2) throw new IllegalArgumentException("Режим 1 или 2");
            System.out.println(mode == 1 ? "Начальная сумма, процент, число периодов:" : "Начальная сумма, целевая сумма, число периодов:");
            double first = input.nextDouble(), second = input.nextDouble(); int n = input.nextInt();
            System.out.printf(java.util.Locale.US, "Результат: %.6f%s%n", mode == 1 ? amount(first, second, n) : rate(first, second, n), mode == 2 ? "%" : "");
        } catch (RuntimeException ex) { System.err.println("Некорректный ввод: " + ex.getMessage()); }
    }
}
