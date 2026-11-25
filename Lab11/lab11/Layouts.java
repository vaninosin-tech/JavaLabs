package lab11;
import java.awt.*;
import javax.swing.*;

public class Layouts {
    public static JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JLabel status = new JLabel("JPanel использует FlowLayout");
        for (int i = 1; i <= 5; i++) {
            String name = "Кнопка " + i; JButton button = new JButton(name);
            button.addActionListener(e -> status.setText("Нажата " + name)); toolbar.add(button);
        }
        root.add(toolbar, BorderLayout.NORTH);
        root.add(new JTextArea("Внешний контейнер: BorderLayout\nВерхняя панель: FlowLayout\nИзменяйте размеры окна и наблюдайте компоновку."), BorderLayout.CENTER);
        root.add(status, BorderLayout.SOUTH); return root;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Менеджеры компоновки"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // У JFrame устанавливаем BorderLayout, у вложенного JPanel — FlowLayout.
            frame.setContentPane(createPanel()); frame.setSize(650, 350); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
