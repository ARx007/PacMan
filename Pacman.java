import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Pacman extends JPanel implements ActionListener, KeyListener {
    Image background;
    Image[] pac;// to store multiple pacman images according to direction arrow pressed
    Image fruit;
    Image bomb;

    // Pacman variables
    int pacx = 50;
    int pacy = 200;
    int pacwidth = 35;
    int pacheight = 35;
    int fruitvelocityX = -1;
    int obsy = 0;

    ArrayList<obstacles> obs;
    Random random = new Random();
    int score = 0;

    class obstacles {
        int x = 1500;// width of screen
        int y = obsy;
        int width1 = 35;
        int height1 = 35;
        // int width2=500;
        // int height2=500;
        Image f;

        obstacles(Image img) {
            this.f = img;
        }
    }

    public void placefruit() {
        int randomfruitY = (int) (obsy + Math.random() * 625); // 975 is height limit for obstacles to appear
        // inorder to increase number of bombs than fruit
        int rvalue = (int) (Math.random() * 10);  //generates number from 0-9
        if (rvalue % 2 == 0 || rvalue == 9 || rvalue == 7 || rvalue == 5) {
            obstacles bmb = new obstacles(bomb);
            bmb.y = randomfruitY;// probabilty of boomb: 7/10
            obs.add(bmb);
        } else {
            obstacles fr = new obstacles(fruit);
            fr.y = randomfruitY;// probability of fruit: 3/10
            obs.add(fr);
        }
    }

    int RIGHT = 0;
    int LEFT = 1;
    int UP = 2;
    int DOWN = 3;
    int direction = RIGHT;
    Timer loop;
    Timer placeobs;

    Pacman() {
        setPreferredSize(new Dimension(1500, 650));
        setFocusable(true);
        addKeyListener(this);
        pac = new Image[4];
        // loading images
        background = new ImageIcon(getClass().getResource("./back.png")).getImage();
        pac[RIGHT] = new ImageIcon(getClass().getResource("./pacman-png-25181.png")).getImage();
        pac[LEFT] = new ImageIcon(getClass().getResource("./pac2.png")).getImage();
        pac[UP] = new ImageIcon(getClass().getResource("./pac3.png")).getImage();
        pac[DOWN] = new ImageIcon(getClass().getResource("./pac1.png")).getImage();
        fruit = new ImageIcon(getClass().getResource("./yel.png")).getImage();
        bomb = new ImageIcon(getClass().getResource("./bomb.png")).getImage();

        // 1000/60 -> 60fps and this is action listener
        loop = new Timer(1000 / 200, this);
        // executes actionPerformed method inT 60fps
        obs = new ArrayList<obstacles>();

        placeobs = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placefruit();
            }
        });
        placeobs.start();
    }

    // paintComponent() is a built in method of Jpanel used to draw smth in Jpanel
    // It takes Graphics object as parameter
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(background, 0, 0, 1500, 650, null);
        g.drawImage(pac[direction], pacx, pacy, pacwidth, pacheight, null);

        for (int i = 0; i < obs.size(); i++) {
            obstacles o = obs.get(i);
            g.drawImage(o.f, o.x, o.y, o.width1, o.height1, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Agency FB", Font.PLAIN, 32));
        // Updates and display the current score in realtime
        g.drawString(Integer.toString(score), 10, 35);
    }

    int dx = 0;// denotes by what value pacx will be changed
    int dy = 0;// denotes by what valye pacy will be changed

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();// calls paintComponent() in 60fps
    }

    public void move() {
        // this moves the pacman
        pacx += dx;
        pacy += dy;

        // Pacman changes its direction when it reaches end of the screen
        if (pacx <= 0) {
            pacx = 0;// confines the pacman and doesnot let it go beyond the screen
            dx = -dx;
            direction = RIGHT;
        }
        if (pacx + pacwidth >= 1200) {
            pacx = 1200 - pacwidth;
        }
        if (pacy <= 0) {
            pacy = 0;
            dy = -dy;
            direction = DOWN;
        }
        if (pacy + pacheight >= 650) {
            pacy = 650 - pacheight;// confines the pacman and doesnot let it go beyond the screen
            dy = -dy;
            direction = UP;
        }

        // moving fruits
        for (int i = 0; i < obs.size(); i++) {
            obstacles o = obs.get(i);
            o.x += fruitvelocityX;

        }

        // Check for collision with bombs
        for (int i = 0; i < obs.size(); i++) {
            obstacles o = obs.get(i);
            o.x += fruitvelocityX;

            // Collision detection between pacman and bomb
            if (pacx < o.x + o.width1 && pacx + pacwidth > o.x &&
                    pacy < o.y + o.height1 && pacy + pacheight > o.y && o.f == bomb) {
                stopGame();
                break; // No need to check further collisions once a collision is detected
            }
        }

        for (int i = 0; i < obs.size(); i++) {
            obstacles o = obs.get(i);
            o.x += fruitvelocityX;

            // Collision detection between pacman and fruit
            if (pacx < o.x + o.width1 && pacx + pacwidth > o.x &&
                    pacy < o.y + o.height1 && pacy + pacheight > o.y && o.f == fruit) {
                score++;
                obs.remove(i);
                break; // No need to check further collisions once a collision is detected
            }
        }

    }

    public void stopGame() {
        loop.stop(); // Stop the game loop
        placeobs.stop(); // Stop placing obstacles
        int choice = JOptionPane.showConfirmDialog(null,
                "Game Over! Your score is " + score + ". Do you want to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION);

        // If user chooses to play again, restart the game
        if (choice == JOptionPane.YES_OPTION) {
            restart();
        } else {
            System.exit(0);
        }
    }

    public void restart() {
        score = 0;
        pacx = 50;
        pacy = 200;
        obs.clear(); // clear the existing ArrayList
        loop.start();
        placeobs.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // this if statment will be invoked after any key pressed even though we haven't
        // explicitly mentioned any key event for it
        if (!loop.isRunning()) {
            restart();
        }
        if (e.getKeyCode() == 'D') {
            dx = 8;
            dy = 0;
            direction = RIGHT;
            loop.start(); // calls actionperformed method with move() and repaint()
        } else if (e.getKeyCode() =='A') {
            dx = -8;
            dy = 0;
            direction = LEFT;
            loop.start(); // this loop won't stop until we press another arrow key
        } else if (e.getKeyCode() == 'W') {
            dx = 0;
            dy = -8;
            direction = UP;
            loop.start();

        } else if (e.getKeyCode() =='S') {
            dx = 0;
            dy = 8;
            direction = DOWN;
            loop.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
