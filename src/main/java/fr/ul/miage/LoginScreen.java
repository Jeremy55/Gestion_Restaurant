package fr.ul.miage;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.gui2.*;

import java.io.IOException;
import java.util.regex.Pattern;

public class LoginScreen extends BasicWindow {

    public LoginScreen() {

        // Create panel to hold components
        Panel panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        final Label lblOutput = new Label("");

        panel.addComponent(new Label("Nom d'utilisateur :"));
        final TextBox username = new TextBox().addTo(panel);

        panel.addComponent(new Label("Mot de passe :"));
        final TextBox password = new TextBox().addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        new Button("Connexion", new Runnable() {
            @Override
            public void run() {
                boolean isConnected = DBQueries.userConnection(username.getText(),(password.getText()));
                String message = isConnected ? "Connexion valide." : "Connexion invalide, veuillez vérifier vos identifiants.";
                lblOutput.setText(message);
            }
        }).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(lblOutput);

        // Set the panel
        this.setComponent(panel);
    }


}
