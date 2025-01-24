import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Pacman");

        frame.setSize(1500, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Pacman pman = new Pacman();
        frame.add(pman); // adding jpanel in a frame
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }
}
