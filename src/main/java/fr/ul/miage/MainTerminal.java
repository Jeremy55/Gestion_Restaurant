package fr.ul.miage;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class MainTerminal {
    private MultiWindowTextGUI gui;

    public MainTerminal(BasicWindow window) throws IOException {
        // Create gui and start gui
        setup();
        gui.addWindowAndWait(window);
    }

    private void setup() throws IOException {
        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        this.gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
    }

    public void switchWindow(BasicWindow window){
        gui.removeWindow(gui.getActiveWindow());
        gui.addWindowAndWait(window);
    }
}
