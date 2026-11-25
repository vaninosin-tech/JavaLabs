package lab7;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.Base64;

/** Словарь уникальных строк и последовательность ссылок восстанавливают порядок и переводы строк. */
public class LineCompression {
    public record Stats(int originalLines, int uniqueLines) {
        public int duplicates() { return originalLines - uniqueLines; }
    }
    private static List<String> lines(String text) {
        List<String> result = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\r' || c == '\n') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') i++;
                result.add(text.substring(start, i + 1)); start = i + 1;
            }
        }
        if (start < text.length()) result.add(text.substring(start));
        return result;
    }
    private static void distinctPaths(Path source, Path destination) throws IOException {
        if (source.toAbsolutePath().normalize().equals(destination.toAbsolutePath().normalize()) ||
                Files.exists(destination) && Files.isSameFile(source, destination))
            throw new IOException("Исходный файл и результат должны различаться");
    }
    public static Stats compress(Path source, Path destination) throws IOException {
        distinctPaths(source, destination);
        List<String> original = lines(Files.readString(source, StandardCharsets.UTF_8));
        Map<String, Integer> dictionary = new LinkedHashMap<>();
        List<Integer> sequence = new ArrayList<>();
        for (String line : original) {
            dictionary.putIfAbsent(line, dictionary.size()); sequence.add(dictionary.get(line));
        }
        Stats stats = new Stats(original.size(), dictionary.size());
        try (BufferedWriter writer = Files.newBufferedWriter(destination, StandardCharsets.UTF_8)) {
            writer.write("LINEPACK1\nremoved=" + stats.duplicates() + "\nunique=" + stats.uniqueLines() + "\n");
            for (String line : dictionary.keySet()) {
                writer.write(Base64.getEncoder().encodeToString(line.getBytes(StandardCharsets.UTF_8))); writer.newLine();
            }
            writer.write("count=" + sequence.size()); writer.newLine();
            for (int index : sequence) { writer.write(Integer.toString(index)); writer.newLine(); }
        }
        return stats;
    }
    private static int count(String value, String prefix) throws IOException {
        if (value == null || !value.startsWith(prefix)) throw new IOException("Неверный заголовок архива");
        try {
            int n = Integer.parseInt(value.substring(prefix.length()));
            if (n < 0) throw new NumberFormatException();
            return n;
        } catch (NumberFormatException ex) { throw new IOException("Неверный счетчик", ex); }
    }
    public static void restore(Path source, Path destination) throws IOException {
        distinctPaths(source, destination);
        StringBuilder restored = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(source, StandardCharsets.UTF_8)) {
            if (!"LINEPACK1".equals(reader.readLine())) throw new IOException("Неверный формат");
            int removed = count(reader.readLine(), "removed="), unique = count(reader.readLine(), "unique=");
            List<String> dictionary = new ArrayList<>();
            for (int i = 0; i < unique; i++) {
                String line = reader.readLine();
                if (line == null) throw new IOException("Неполный словарь");
                try { dictionary.add(new String(Base64.getDecoder().decode(line), StandardCharsets.UTF_8)); }
                catch (IllegalArgumentException ex) { throw new IOException("Неверная строка словаря", ex); }
            }
            int total = count(reader.readLine(), "count=");
            if (total - unique != removed) throw new IOException("Неверное количество дублей");
            Set<Integer> used = new HashSet<>();
            for (int i = 0; i < total; i++) {
                int index = count(reader.readLine(), "");
                if (index >= unique) throw new IOException("Индекс вне словаря");
                restored.append(dictionary.get(index)); used.add(index);
            }
            if (reader.readLine() != null || used.size() != unique) throw new IOException("Неверная структура архива");
        }
        Files.writeString(destination, restored, StandardCharsets.UTF_8);
    }
    public static void main(String[] args) {
        if (args.length != 3 || !(args[0].equals("pack") || args[0].equals("unpack"))) {
            System.err.println("pack|unpack исходный_файл результат"); return;
        }
        try {
            if (args[0].equals("pack")) {
                Stats stats = compress(Path.of(args[1]), Path.of(args[2]));
                System.out.println("Удалено повторных строк: " + stats.duplicates());
            } else { restore(Path.of(args[1]), Path.of(args[2])); System.out.println("Файл восстановлен"); }
        } catch (IOException | RuntimeException ex) { System.err.println("Ошибка: " + ex.getMessage()); }
    }
}
