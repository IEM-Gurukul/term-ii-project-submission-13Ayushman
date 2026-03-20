
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Stack;

// Command Interface
interface Command {
    void execute();
    void undo();
}

// Insert Command
class InsertCommand implements Command {
    private JTextArea textArea;
    private String text;
    private int position;

    public InsertCommand(JTextArea textArea, String text, int position) {
        this.textArea = textArea;
        this.text = text;
        this.position = position;
    }

    public void execute() {
        textArea.insert(text, position);
    }

    public void undo() {
        textArea.replaceRange("", position, position + text.length());
    }
}

// Delete Command
class DeleteCommand implements Command {
    private JTextArea textArea;
    private String deletedText;
    private int start, end;

    public DeleteCommand(JTextArea textArea, int start, int end) {
        this.textArea = textArea;
        this.start = start;
        this.end = end;
        this.deletedText = textArea.getText().substring(start, end);
    }

    public void execute() {
        textArea.replaceRange("", start, end);
    }

    public void undo() {
        textArea.insert(deletedText, start);
    }
}

public class CodeFlowEditor {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditorFrame::new);
    }
}

class EditorFrame extends JFrame {
    private JTextArea textArea;
    private JLabel statusBar;
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();
    private boolean isModified = false;
    private File currentFile = null;
    private boolean isDark = true;
    private int fontSize = 14;

    public EditorFrame() {
        setTitle("Code-Flow Editor - Untitled");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));

        textArea.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                int pos = textArea.getCaretPosition();
                Command cmd = new InsertCommand(textArea, String.valueOf(e.getKeyChar()), pos);
                undoStack.push(cmd);
                redoStack.clear();
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStatus(); isModified = true; }
            public void removeUpdate(DocumentEvent e) { updateStatus(); isModified = true; }
            public void changedUpdate(DocumentEvent e) {}
        });

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(createToolBar(), BorderLayout.NORTH);
        add(createStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());

        applyDarkTheme();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });

        setVisible(true);
    }

    private JToolBar createToolBar() {
        JToolBar bar = new JToolBar();

        JButton undo = new JButton("Undo");
        JButton redo = new JButton("Redo");
        JButton find = new JButton("Find");
        JButton theme = new JButton("Theme");
        JButton save = new JButton("Save");
        JButton fontPlus = new JButton("A+");
        JButton fontMinus = new JButton("A-");

        undo.addActionListener(e -> undo());
        redo.addActionListener(e -> redo());
        find.addActionListener(e -> showFindDialog());
        theme.addActionListener(e -> toggleTheme());
        save.addActionListener(e -> saveFile());
        fontPlus.addActionListener(e -> changeFontSize(2));
        fontMinus.addActionListener(e -> changeFontSize(-2));

        bar.add(undo);
        bar.add(redo);
        bar.add(save);
        bar.add(find);
        bar.add(theme);
        bar.add(fontPlus);
        bar.add(fontMinus);

        return bar;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");

        open.addActionListener(e -> openFile());
        save.addActionListener(e -> saveFile());

        file.add(open);
        file.add(save);
        menuBar.add(file);

        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        statusBar = new JLabel("Ready");
        panel.add(statusBar, BorderLayout.WEST);
        return panel;
    }

    private void updateStatus() {
        int words = textArea.getText().trim().isEmpty() ? 0 : textArea.getText().trim().split("\\s+").length;
        int chars = textArea.getText().length();
        statusBar.setText("Words: " + words + " | Characters: " + chars);
    }

    private void showFindDialog() {
        String find = JOptionPane.showInputDialog(this, "Find:");
        if (find != null) {
            int index = textArea.getText().indexOf(find);
            if (index >= 0) textArea.select(index, index + find.length());
            else JOptionPane.showMessageDialog(this, "Not found");
        }
    }

    private void toggleTheme() {
        if (isDark) {
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
        } else {
            applyDarkTheme();
        }
        isDark = !isDark;
    }

    private void applyDarkTheme() {
        textArea.setBackground(new Color(30,30,30));
        textArea.setForeground(new Color(220,220,220));
        textArea.setCaretColor(Color.WHITE);
    }

    private void changeFontSize(int delta) {
        fontSize += delta;
        textArea.setFont(new Font("Consolas", Font.PLAIN, fontSize));
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            Command cmd = undoStack.pop();
            cmd.undo();
            redoStack.push(cmd);
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            Command cmd = redoStack.pop();
            cmd.execute();
            undoStack.push(cmd);
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(br, null);
                isModified = false;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error");
            }
        }
    }

    private void saveFile() {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
            }
        }
        if (currentFile != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(bw);
                isModified = false;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error");
            }
        }
    }

    private void handleExit() {
        if (!isModified) System.exit(0);

        int option = JOptionPane.showConfirmDialog(this, "Save changes?", "Exit",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            saveFile();
            System.exit(0);
        } else if (option == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
}
