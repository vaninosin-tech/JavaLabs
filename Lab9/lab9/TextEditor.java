package lab9;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.IOException;

public class TextEditor extends JFrame {
    protected final JTextArea textArea = new JTextArea();
    protected final JLabel status = new JLabel("UTF-8");
    private Path file;
    private boolean dirty;
    public TextEditor() {
        super("Текстовый редактор");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        add(new JScrollPane(textArea), BorderLayout.CENTER); add(status, BorderLayout.SOUTH);
        JMenuBar bar = new JMenuBar(); JMenu menu = new JMenu("Файл"); bar.add(menu); setJMenuBar(bar);
        item(menu, "Открыть", e -> open()); item(menu, "Сохранить", e -> save(false));
        item(menu, "Сохранить как", e -> save(true)); item(menu, "Выход", e -> requestClose());
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { changed(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { changed(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { changed(); }
            private void changed() { dirty = true; }
        });
        addWindowListener(new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { requestClose(); } });
        setSize(800, 550); setLocationRelativeTo(null);
    }
    private void item(JMenu menu, String name, ActionListener action) {
        JMenuItem item = new JMenuItem(name); item.addActionListener(action); menu.add(item);
    }
    private boolean confirmChanges() {
        if (!dirty) return true;
        int answer = JOptionPane.showConfirmDialog(this, "Сохранить изменения?", "Несохраненный текст", JOptionPane.YES_NO_CANCEL_OPTION);
        return answer == JOptionPane.NO_OPTION || answer == JOptionPane.YES_OPTION && save(false);
    }
    private void open() {
        if (!confirmChanges()) return;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path selected = chooser.getSelectedFile().toPath();
        try {
            String text = Files.readString(selected, StandardCharsets.UTF_8);
            textArea.setText(text); textArea.setCaretPosition(0); file = selected; dirty = false;
            status.setText("Открыт: " + file + " (UTF-8)");
        } catch (IOException ex) { error(ex); }
    }
    private boolean save(boolean chooseNew) {
        Path selected = file;
        if (chooseNew || selected == null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false;
            selected = chooser.getSelectedFile().toPath();
            if (Files.exists(selected) && JOptionPane.showConfirmDialog(this, "Перезаписать файл?", "Сохранение", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return false;
        }
        try {
            Files.writeString(selected, textArea.getText(), StandardCharsets.UTF_8);
            file = selected; dirty = false; status.setText("Сохранен: " + file); return true;
        } catch (IOException ex) { error(ex); return false; }
    }
    private void error(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка файла", JOptionPane.ERROR_MESSAGE); }
    protected void requestClose() { if (confirmChanges()) dispose(); }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new TextEditor().setVisible(true)); }
}
