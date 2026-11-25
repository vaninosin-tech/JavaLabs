package lab12;
import lab9.TextEditor;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import javax.swing.*;

public class SpellEditor extends TextEditor {
    public static final Map<String, String> DICTIONARY = Map.of("превет", "привет", "малако", "молоко", "карова", "корова", "жыраф", "жираф");
    private static final Pattern WORD = Pattern.compile("[\\p{L}]+");
    public record Correction(int offset, String before, String after) {}
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "spell-check"); thread.setDaemon(true); return thread;
    });
    private long revision;
    public SpellEditor() {
        super(); setTitle("Текстовый редактор — автозамена");
        status.setText("Автозамена по пробелу: превет → привет, малако → молоко, карова → корова, жыраф → жираф");
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { revision++; }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { revision++; }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { revision++; }
        });
        textArea.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_SPACE) checkAsync(); }
        });
        addWindowListener(new WindowAdapter() { @Override public void windowClosed(WindowEvent e) { worker.shutdownNow(); } });
    }
    public static java.util.List<Correction> findCorrections(String text) {
        java.util.List<Correction> changes = new ArrayList<>();
        Matcher matcher = WORD.matcher(text);
        while (matcher.find()) {
            // Последнее незаконченное слово пока не исправляем.
            if (matcher.end() == text.length()) continue;
            String word = matcher.group(), replacement = DICTIONARY.get(word.toLowerCase(Locale.ROOT));
            if (replacement == null) continue;
            if (word.equals(word.toUpperCase(Locale.ROOT))) replacement = replacement.toUpperCase(Locale.ROOT);
            else if (Character.isUpperCase(word.charAt(0))) replacement = Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
            changes.add(new Correction(matcher.start(), word, replacement));
        }
        return changes;
    }
    private void checkAsync() {
        // Swing читается только на EDT; поиск выполняется в рабочем потоке.
        String snapshot = textArea.getText(); long expectedRevision = revision;
        worker.submit(() -> {
            var changes = findCorrections(snapshot);
            SwingUtilities.invokeLater(() -> {
                // Пользователь мог продолжить ввод: устаревший результат не применяем.
                if (!isDisplayable() || revision != expectedRevision) return;
                int caret = textArea.getCaretPosition();
                for (int i = changes.size() - 1; i >= 0; i--) {
                    Correction change = changes.get(i);
                    textArea.replaceRange(change.after(), change.offset(), change.offset() + change.before().length());
                    if (change.offset() < caret) caret += change.after().length() - change.before().length();
                }
                textArea.setCaretPosition(Math.max(0, Math.min(caret, textArea.getDocument().getLength())));
                status.setText("Исправлено слов: " + changes.size());
            });
        });
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new SpellEditor().setVisible(true)); }
}
