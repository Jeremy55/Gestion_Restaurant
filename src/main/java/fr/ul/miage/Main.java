package fr.ul.miage;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        MainTerminal terminal = MainTerminal.getConsole();
        terminal.start();
    }
}
