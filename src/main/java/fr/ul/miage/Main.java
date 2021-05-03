package fr.ul.miage;

import java.io.IOException;

public class Main {

    public static MainTerminal terminal;

    public static void main(String[] args) throws IOException {
        LoginScreen loginScreen = new LoginScreen();
        terminal = new MainTerminal(loginScreen);
        System.out.println("test");
    }
}
