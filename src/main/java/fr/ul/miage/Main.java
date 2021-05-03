package fr.ul.miage;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LoginScreen loginScreen = new LoginScreen();
        MainTerminal terminal = new MainTerminal(loginScreen);
    }
}
