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

public class LoginScreen {
    public static void main(String[] args) throws IOException {
        // Setup terminal and screen layers
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

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

        // Create window to hold the panel
        BasicWindow window = new BasicWindow();
        window.setComponent(panel);

        // Create gui and start gui
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }
}
