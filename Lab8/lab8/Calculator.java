package lab8;
import lab2.ConsoleCalculator;
import java.awt.*;
import javax.swing.*;

public class Calculator {
    public static JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTextField display = new JTextField(); display.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        display.setToolTipText("Введите выражение, например 2 * 6.5");
        JPanel buttons = new JPanel(new GridLayout(4, 4, 5, 5));
        String[] labels = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+"};
        Runnable calculate = () -> {
            try { display.setText(Double.toString(ConsoleCalculator.calculate(display.getText()))); }
            catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(panel, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE); }
        };
        for (String label : labels) {
            JButton button = new JButton(label); buttons.add(button);
            button.addActionListener(e -> { if (label.equals("=")) calculate.run(); else display.setText(display.getText() + label); });
        }
        display.addActionListener(e -> calculate.run());
        JButton clear = new JButton("Очистить"); clear.addActionListener(e -> display.setText(""));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(display, BorderLayout.NORTH); panel.add(buttons, BorderLayout.CENTER); panel.add(clear, BorderLayout.SOUTH);
        return panel;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Калькулятор"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(createPanel()); frame.setSize(360, 430); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
