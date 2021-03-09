package io.github.ericedwards.terakemu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminalGUI implements SerialTerminal {

    private static Logger logger = LoggerFactory.getLogger(TerminalGUI.class);
    private static final int TERMINAL_COLUMNS = 80;
    private static final int TERMINAL_ROWS = 24;
    private static final int TERMINAL_FONT_SIZE = 12;
    private JFrame mainFrame;
    private JTextArea textArea;
    private Font font;
    private SerialHost host;
    private int currentRow;
    private int currentColumn;
    private StringBuffer currentText;
    private int escapeSequence;
    private int cursorSaved;

    public TerminalGUI(SerialHost host) {
        this.host = host;
        mainFrame = new JFrame("Console Terminal");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea(TERMINAL_ROWS, TERMINAL_COLUMNS + 1);
        font = new Font(Font.MONOSPACED, Font.BOLD, TERMINAL_FONT_SIZE);
        textArea.addKeyListener(new KeyListener());
        textArea.setFont(font);
        textArea.setEditable(false);
        textArea.getCaret().setVisible(true);
        textArea.getCaret().setSelectionVisible(true);
        textArea.setLineWrap(false);

        clearScreen();
        home();
        updateText();
        positionCaret();

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.getCaret().setVisible(true);
                textArea.getCaret().setSelectionVisible(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                textArea.getCaret().setSelectionVisible(true);
            }
        });

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new QuitAction());
        menu.add(quitItem);
        mb.add(menu);

        mainFrame.setJMenuBar(mb);
        mainFrame.setSize(1000, 800);       // FIXME - how to auto size?
        mainFrame.add(textArea);
        mainFrame.setVisible(true);
        
        escapeSequence = 0;

    }

    private void clearScreen() {
        currentText = new StringBuffer();
        for (int i = 0; i < TERMINAL_ROWS; ++i) {
            for (int j = 0; j < TERMINAL_COLUMNS; ++j) {
                currentText.append(" ");
            }
            currentText.append("\n");
        }
    }

    private void home() {
        currentRow = 0;
        currentColumn = 0;
    }

    private void positionCaret() {
        int position = (currentRow * (TERMINAL_COLUMNS + 1)) + currentColumn;
        textArea.setCaretPosition(position);
    }

    private void updateText() {
        textArea.setText(currentText.toString());
    }

    @Override
    public void sendToTerminal(int s) {
        int position = (currentRow * (TERMINAL_COLUMNS + 1)) + currentColumn;
        if (escapeSequence > 0) {
            switch(escapeSequence) {
                case 1:
                    if (s == 61) {                   // cursor positioning             
                        escapeSequence = 2;
                    } else {
                        logger.debug("unknown escape sequence: " + s);
                        escapeSequence = 0;                   
                    }
                    break;
                case 2:
                    cursorSaved = s;
                    escapeSequence = 3;
                    break;
                case 3:
                    logger.debug("will cursor position: " + cursorSaved + " " + s);
                    currentRow = cursorSaved - 32;
                    currentColumn = s - 32;
                    escapeSequence = 0;
                    break;
                default:
                    logger.debug("unknown escape sequence state: " + escapeSequence);
                    break;
            }
        } else if ((s >= 32) && (s < 127)) {
            String charString = Character.toString((char) s);
            currentText = currentText.replace(position, position + 1, charString);
            textArea.setText(currentText.toString());
            ++currentColumn;
            if (currentColumn >= TERMINAL_COLUMNS) {
                currentColumn = 0;
                ++currentRow;
                if (currentRow >= TERMINAL_ROWS) {
                    currentText = currentText.delete(0, TERMINAL_COLUMNS + 1);
                    currentRow = TERMINAL_ROWS - 1;
                    for (int j = 0; j < TERMINAL_COLUMNS; ++j) {
                        currentText.append(" ");
                    }
                    currentText.append("\n");
                }
            }
        } else {
            switch (s) {
                case 7:
                    Toolkit.getDefaultToolkit().beep();
                    break;
                case 8:     // CTRL-H (BS) (Cursor Left)
                    if (currentColumn > 0) {
                        --currentColumn;
                    }
                    break;
                case 9:     // CTRL-I (TAB)
                    // not sure it supports this
                    break;
                case 10:    // CTRL-J (LF) (Cursor Down)
                    ++currentRow;
                    if (currentRow >= TERMINAL_ROWS) {
                        currentText = currentText.delete(0, TERMINAL_COLUMNS + 1);
                        currentRow = TERMINAL_ROWS - 1;
                        for (int j = 0; j < TERMINAL_COLUMNS; ++j) {
                            currentText.append(" ");
                        }
                        currentText.append("\n");
                    }
                    break;
                case 11:    // CTRL-J (Cursor Up)
                    if (currentRow > 0) {
                        --currentRow;
                    }
                    break;
                case 12:    // CTRL-K (Cursor Right)
                    ++currentColumn;
                    if (currentColumn >= TERMINAL_COLUMNS) {
                        currentColumn = 0;
                        ++currentRow;
                        if (currentRow >= TERMINAL_ROWS) {
                            currentText = currentText.delete(0, TERMINAL_COLUMNS + 1);
                            currentRow = TERMINAL_ROWS - 1;
                            for (int j = 0; j < TERMINAL_COLUMNS; ++j) {
                                currentText.append(" ");
                            }
                            currentText.append("\n");
                        }
                    }
                    break;
                case 13:    // CTRL-M (CR)
                    currentColumn = 0;
                    break;
                case 26:    // CTRL-Z (Clear Screen)
                    clearScreen();
                    home();
                    break;
                case 27:    // CTRL-[ (ESC) (Cursor Position)
                    escapeSequence = 1;
                    break;
                case 30:    // CTRL-^ (Home)
                    home();
                    break;
                default:
                    break;
            }
        }
        updateText();
        positionCaret();
    }

    private class KeyListener extends KeyAdapter {

        @Override
        public void keyTyped(KeyEvent evt) {
            char r = evt.getKeyChar();
            host.receiveFromTerminal(r);
        }

    }

    private class QuitAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

}