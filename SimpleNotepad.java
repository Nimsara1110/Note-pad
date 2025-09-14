import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SimpleNotepad extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JFileChooser fileChooser;

    public SimpleNotepad() {
        // Set up the JFrame (main window)
        setTitle("Simple Notepad - Untitled");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create the main text area
        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        // Create the menu bar
        createMenuBar();

        // Initialize the file chooser
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        setVisible(true); // Make the window visible
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        createMenuItem(fileMenu, "New", KeyEvent.VK_N, "control N");
        createMenuItem(fileMenu, "Open", KeyEvent.VK_O, "control O");
        createMenuItem(fileMenu, "Save", KeyEvent.VK_S, "control S");
        fileMenu.addSeparator();
        createMenuItem(fileMenu, "Exit", KeyEvent.VK_X, "control Q");

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        createMenuItem(editMenu, "Cut", KeyEvent.VK_X, "control X");
        createMenuItem(editMenu, "Copy", KeyEvent.VK_C, "control C");
        createMenuItem(editMenu, "Paste", KeyEvent.VK_V, "control V");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void createMenuItem(JMenu menu, String text, int keyEvent, String accelerator) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(this);
        menuItem.setMnemonic(keyEvent);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        menu.add(menuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                newFile();
                break;
            case "Open":
                openFile();
                break;
            case "Save":
                saveFile();
                break;
            case "Exit":
                exitApp();
                break;
            case "Cut":
                textArea.cut();
                break;
            case "Copy":
                textArea.copy();
                break;
            case "Paste":
                textArea.paste();
                break;
        }
    }

    private void newFile() {
        if (!textArea.getText().isEmpty()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes to the current file?",
                    "Save Changes?",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return; // Don't create a new file if cancelled
            }
        }
        textArea.setText("");
        setTitle("Simple Notepad - Untitled");
    }

    private void openFile() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.read(reader, null);
                setTitle("Simple Notepad - " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure the file has a .txt extension
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                textArea.write(writer);
                setTitle("Simple Notepad - " + file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exitApp() {
        if (!textArea.getText().isEmpty()) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Do you want to save changes before exiting?",
                    "Save Changes?",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                saveFile();
                System.exit(0);
            } else if (option == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
            // If cancelled, do nothing and stay in the app
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // Run the application on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(SimpleNotepad::new);
    }
}