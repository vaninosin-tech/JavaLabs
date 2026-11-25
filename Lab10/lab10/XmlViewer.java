package lab10;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XmlViewer {
    public static DefaultMutableTreeNode parse(Path path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setXIncludeAware(false); factory.setExpandEntityReferences(false);
        Document document = factory.newDocumentBuilder().parse(path.toFile());
        return convert(document.getDocumentElement());
    }
    private static DefaultMutableTreeNode convert(Node node) {
        if (node.hasAttributes()) throw new IllegalArgumentException("В задании используются теги без атрибутов");
        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(node.getNodeName());
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) tree.add(convert(child));
            else if ((child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) && !child.getNodeValue().isBlank())
                tree.add(new DefaultMutableTreeNode(child.getNodeValue().trim()));
        }
        return tree;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("XML-парсер"); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JTree tree = new JTree(new DefaultMutableTreeNode("Откройте XML-файл"));
            JButton open = new JButton("Открыть XML");
            open.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) return;
                Path path = chooser.getSelectedFile().toPath(); open.setEnabled(false);
                new SwingWorker<DefaultMutableTreeNode, Void>() {
                    @Override protected DefaultMutableTreeNode doInBackground() throws Exception { return parse(path); }
                    @Override protected void done() {
                        open.setEnabled(true);
                        try {
                            tree.setModel(new javax.swing.tree.DefaultTreeModel(get()));
                            for (int row = 0; row < tree.getRowCount(); row++) tree.expandRow(row);
                        } catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage(), "Ошибка XML", JOptionPane.ERROR_MESSAGE); }
                    }
                }.execute();
            });
            frame.add(open, BorderLayout.NORTH); frame.add(new JScrollPane(tree), BorderLayout.CENTER);
            frame.setSize(600, 450); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
