import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;

/**
 * Project: Solo Lab 7 Assignment
 * Purpose Details: Space Game Mods
 * Course: IST 242
 * Author: Matthew Sulpizio
 * Date Developed: 4/27/26
 * Last Date Changed: 5/03/26
 * Rev:
 */
public class SpaceGame extends JFrame implements KeyListener {

    /**
     * The width of the game window.
     */
    private static final int WIDTH = 500;

    /**
     * The height of the game window.
     */
    private static final int HEIGHT = 500;

    /**
     * The width of the player ship.
     */
    private static final int PLAYER_WIDTH = 50;

    /**
     * The height of the player ship.
     */
    private static final int PLAYER_HEIGHT = 50;

    /**
     * The width of each obstacle.
     */
    private static final int OBSTACLE_WIDTH = 64;

    /**
     * The height of each obstacle.
     */
    private static final int OBSTACLE_HEIGHT = 64;

    /**
     * The width of each projectile.
     */
    private static final int PROJECTILE_WIDTH = 5;

    /**
     * The height of each projectile.
     */
    private static final int PROJECTILE_HEIGHT = 10;

    /**
     * The speed of the player ship.
     */
    private static final int PLAYER_SPEED = 10;

    /**
     * The base speed of the obstacles.
     */
    private static final int OBSTACLE_SPEED = 3;

    /**
     * The speed of the projectile.
     */
    private static final int PROJECTILE_SPEED = 10;

    /**
     * The player's current score.
     */
    private int score = 0;

    /**
     * The main panel where the game is drawn.
     */
    private JPanel gamePanel;

    /**
     * The label that displays the player's score.
     */
    private JLabel scoreLabel;

    /**
     * The main game timer that controls game updates.
     */
    private Timer timer;

    /**
     * Tracks whether the game is over.
     */
    private boolean isGameOver;

    /**
     * The x and y coordinates of the player ship.
     */
    private int playerX, playerY;

    /**
     * The x and y coordinates of the projectile.
     */
    private int projectileX, projectileY;

    /**
     * Tracks whether the projectile is visible.
     */
    private boolean isProjectileVisible;

    /**
     * Tracks whether the player is currently firing.
     */
    private boolean isFiring;

    /**
     * The list of obstacles in the game.
     */
    private java.util.List<Point> obstacles;

    /**
     * The list of stars used for the background.
     */
    private List<Point> stars;

    /**
     * The image used for the player ship.
     */
    private BufferedImage shipImage;

    /**
     * The sound clip used when the player fires.
     */
    private Clip clip;

    /**
     * Sound for collision with an obstacle.
     */
    private Clip collisionClip;

    /**
     * The player's health.
     */
    private int health = 3;

    /**
     * The label that displays the player's health.
     */
    private JLabel healthLabel;

    /**
     * The amount of time left in the game.
     */
    private int timeLeft = 60;

    /**
     * The label that displays the countdown timer.
     */
    private JLabel timerLabel;

    /**
     * The countdown timer for the game.
     */
    private Timer countdownTimer;

    /**
     * The current game level.
     */
    private int level = 1;

    /**
     * The label that displays the current game level.
     */
    private JLabel levelLabel;

    /**
     * The list of health power-ups in the game.
     */
    private List<Point> healthPowerUps = new ArrayList<>();

    /**
     * The image used for health power-ups.
     */
    private BufferedImage healthPowerUpImage;

    /**
     * The sprite sheet used for obstacle images.
     */
    private BufferedImage spriteSheet;

    /**
     * The width of each sprite in the sprite sheet.
     */
    private int spriteWidth = 64;

    /**
     * The height of each sprite in the sprite sheet.
     */
    private int spriteHeight = 64;

    /**
     * Tracks whether the player's shield is active.
     */
    private boolean shieldActive = false;

    /**
     * The shield duration in milliseconds.
     */
    private int shieldDuration = 5000;

    /**
     * The time when the shield was activated.
     */
    private long shieldStartTime;

    /**
     * Creates the SpaceGame window, loads game images and sounds,
     * initializes labels, timers, player position, and game objects.
     */
    public SpaceGame() {

        try {
            // Load the ship image from file
            shipImage = ImageIO.read(new File("ship.png"));
            spriteSheet = ImageIO.read(new File("astro.png"));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("fire.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            AudioInputStream collisionAudioInputStream = AudioSystem.getAudioInputStream(new File("collision.wav").getAbsoluteFile());
            collisionClip = AudioSystem.getClip();
            collisionClip.open(collisionAudioInputStream);
            // Load the health power-up image from file
            healthPowerUpImage = ImageIO.read(new File("health.png"));
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (UnsupportedAudioFileException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            // Handle image loading error
            ex.printStackTrace();
        }

        stars = generateStars(200); // Generate 200 stars initially

        setTitle("Space Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        gamePanel.setLayout(null); // Choose label placement

        // Score label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.BLUE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreLabel.setBounds(10, 10, 300, 60);
        gamePanel.add(scoreLabel);

        // Player health label
        healthLabel = new JLabel("Health: " + health);
        healthLabel.setForeground(Color.WHITE);
        healthLabel.setFont(new Font("Arial", Font.BOLD, 20));
        healthLabel.setBounds(10, 70, 150, 30);
        gamePanel.add(healthLabel);

        // Game countdown timer label
        timerLabel = new JLabel("Time: " + timeLeft);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setBounds(10, 100, 150, 30);
        gamePanel.add(timerLabel);

        // ame level label
        levelLabel = new JLabel("Level: " + level);
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelLabel.setBounds(10, 130, 150, 30);
        gamePanel.add(levelLabel);

        add(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);

        playerX = WIDTH / 2 - PLAYER_WIDTH / 2;
        playerY = HEIGHT - PLAYER_HEIGHT - 20;
        projectileX = playerX + PLAYER_WIDTH / 2 - PROJECTILE_WIDTH / 2;
        projectileY = playerY;
        isProjectileVisible = false;
        isGameOver = false;
        isFiring = false;
        obstacles = new java.util.ArrayList<>();

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver) {
                    update();
                    gamePanel.repaint();
                }
            }
        });
        timer.start();

        // Game countdown timer
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isGameOver) {
                    timeLeft--;
                    timerLabel.setText("Time: " + timeLeft);

                    if (timeLeft <= 0) {
                        isGameOver = true;
                        countdownTimer.stop();
                        gamePanel.repaint();
                    }
                }
            }
        });
        countdownTimer.start();
    }

    /**
     * Plays the sound effect when the player fires a projectile.
     */
    public void playSound() {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start(); // Start playing the sound
        }
    }

    /**
     * Plays the sound effect when the player collides with an obstacle.
     */
    public void playCollisionSound() {
        if (collisionClip != null) {
            collisionClip.setFramePosition(0);
            collisionClip.start();
        }
    }

    /**
     * Generates a list of random stars for the background.
     *
     * @param numStars The number of stars to generate.
     * @return A list of Point objects representing star locations.
     */
    private List<Point> generateStars(int numStars) {
        List<Point> starsList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numStars; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            starsList.add(new Point(x, y));
        }
        return starsList;
    }

    /**
     * Generates a random color.
     *
     * @return A randomly generated Color object.
     */
    public static Color generateRandomColor() {
        Random rand = new Random();
        int r = rand.nextInt(256); // Red component (0-255)
        int g = rand.nextInt(256); // Green component (0-255)
        int b = rand.nextInt(256); // Blue component (0-255)
        return new Color(r, g, b);
    }

    /**
     * Draws all game objects on the screen, including the background,
     * projectile, obstacles, stars, player ship, power-ups, game over text,
     * and shield effect.
     *
     * @param g The Graphics object used to draw the game.
     */
    private void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (isProjectileVisible) {
            g.setColor(Color.GREEN);
            g.fillRect(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        }

        //g.setColor(Color.RED);
        //for (Point obstacle : obstacles) {
        //    g.fillRect(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
        //}

        for (Point obstacle : obstacles) {
            if (spriteSheet != null) {
                // Randomly select a sprite index (0-3)
                Random random = new Random();
                int spriteIndex = random.nextInt(4);

                // Calc the x y coord of the selected sprite on the sprite sheet
                int spriteX = spriteIndex * spriteWidth;
                int spriteY = 0; // Assuming all sprites are in the first row

                // Draw the selected sprite onto the canvas
                g.drawImage(spriteSheet.getSubimage(spriteX, spriteY,
                        spriteWidth, spriteHeight), obstacle.x, obstacle.y, null);
            }
        }

        // Draw stars
        g.setColor(generateRandomColor());
        //g.setColor(Color.WHITE);
        for (Point star : stars) {
            g.fillOval(star.x, star.y, 2, 2);
        }

        //Player
        g.drawImage(shipImage, playerX, playerY, null);
        //g.setColor(Color.BLUE);
        //g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        // Player health power-ups
        for (Point powerUp : healthPowerUps) {
            if (healthPowerUpImage != null) {
                g.drawImage(healthPowerUpImage, powerUp.x, powerUp.y, 30, 30, null);
            } else {
                g.setColor(Color.PINK);
                g.fillOval(powerUp.x, powerUp.y, 30, 30);
            }
        }

        if (isGameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over!", WIDTH / 2 - 80, HEIGHT / 2);
        }

        if (isShieldActive()) {
            g.setColor(new Color(0, 255, 255, 100)); // Semi-transparent cyan
            g.fillOval(playerX, playerY, 60, 60);
        }
    }

    /**
     * Updates the game state by moving objects, generating obstacles and
     * power-ups, checking collisions, updating the score, and changing levels.
     */
    private void update() {
        if (!isGameOver) {
            // Move obstacles
            for (int i = 0; i < obstacles.size(); i++) {
                obstacles.get(i).y += OBSTACLE_SPEED + level; // Higher levels make obstacles faster

                if (obstacles.get(i).y > HEIGHT) {
                    obstacles.remove(i);
                    i--;
                }
            }

            // Move player health power-ups towards bottom
            for (int i = 0; i < healthPowerUps.size(); i++) {
                healthPowerUps.get(i).y += 2;

                if (healthPowerUps.get(i).y > HEIGHT) {
                    healthPowerUps.remove(i);
                    i--;
                }
            }

            // Generate new obstacles
            if (Math.random() < 0.02) {
                int obstacleX = (int) (Math.random() * (WIDTH - OBSTACLE_WIDTH));
                obstacles.add(new Point(obstacleX, 0));
            }

            // Randomly generate player health power-ups
            if (Math.random() < 0.005) {
                int powerUpX = (int) (Math.random() * (WIDTH - 30));
                healthPowerUps.add(new Point(powerUpX, 0));
            }

            if (Math.random() < 0.1) {
                stars = generateStars(200); // Regenerate stars
            }

            // Move projectile
            if (isProjectileVisible) {
                projectileY -= PROJECTILE_SPEED;
                if (projectileY < 0) {
                    isProjectileVisible = false;
                }
            }

            // Check collision with player
            Rectangle playerRect = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);
            for (int i = 0; i < obstacles.size(); i++) {
                Point obstacle = obstacles.get(i);
                Rectangle obstacleRect = new Rectangle(obstacle.x, obstacle.y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);

                if (playerRect.intersects(obstacleRect) && !isShieldActive()) {
                    playCollisionSound(); // Collision sound

                    health--; // Lose player health
                    healthLabel.setText("Health: " + health);

                    obstacles.remove(i);

                    if (health <= 0) {
                        isGameOver = true;
                    }

                    break;
                }
            }

            // Check collision with player health power-up
            Rectangle playerRectForPowerUp = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

            for (int i = 0; i < healthPowerUps.size(); i++) {
                Rectangle powerUpRect = new Rectangle(healthPowerUps.get(i).x, healthPowerUps.get(i).y, 30, 30);

                if (playerRectForPowerUp.intersects(powerUpRect)) {
                    health++;
                    healthPowerUps.remove(i);
                    healthLabel.setText("Health: " + health);
                    break;
                }
            }

            // Check collision with obstacle
            Rectangle projectileRect = new Rectangle(projectileX, projectileY, PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle obstacleRect = new Rectangle(obstacles.get(i).x, obstacles.get(i).y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT);
                if (projectileRect.intersects(obstacleRect)) {
                    obstacles.remove(i);
                    score += 10;
                    isProjectileVisible = false;
                    break;
                }
            }

            // Game level challenge
            if (score >= 50 && level == 1) {
                level = 2;
                levelLabel.setText("Level: " + level);
            }

            if (score >= 100 && level == 2) {
                level = 3;
                levelLabel.setText("Level: " + level);
            }

            scoreLabel.setText("Score: " + score);
        }
    }

    /**
     * Resets the game back to the starting values.
     */
    private void reset() {
        score = 0;
        health = 3;
        timeLeft = 60;
        level = 1;

        isGameOver = false;
        isProjectileVisible = false;
        isFiring = false;

        obstacles.clear();
        healthPowerUps.clear();

        healthLabel.setText("Health: " + health);
        timerLabel.setText("Time: " + timeLeft);
        levelLabel.setText("Level: " + level);
        scoreLabel.setText("Score: " + score);

        if (countdownTimer != null) {
            countdownTimer.restart();
        }

        repaint();
    }

    /**
     * Activates the player's shield and records the start time.
     */
    private void activateShield() {
        shieldActive = true;
        shieldStartTime = System.currentTimeMillis();
    }

    /**
     * Deactivates the player's shield.
     */
    private void deactivateShield() {
        shieldActive = false;
    }

    /**
     * Checks whether the shield is currently active.
     *
     * @return true if the shield is active and still within its duration, otherwise false.
     */
    private boolean isShieldActive() {
        return shieldActive && (System.currentTimeMillis() - shieldStartTime) < shieldDuration;
    }

    /**
     * Handles keyboard input for moving the player, resetting the game,
     * activating the shield, and firing projectiles.
     *
     * @param e The KeyEvent created when a key is pressed.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT && playerX > 0) {
            playerX -= PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_RIGHT && playerX < WIDTH - PLAYER_WIDTH) {
            playerX += PLAYER_SPEED;
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            reset();
        } else if (keyCode == KeyEvent.VK_CONTROL) {
            activateShield();
        } else if (keyCode == KeyEvent.VK_SPACE && !isFiring) {
            playSound();
            isFiring = true;
            projectileX = playerX + PLAYER_WIDTH / 2 - PROJECTILE_WIDTH / 2;
            projectileY = playerY;
            isProjectileVisible = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500); // Limit firing rate
                        isFiring = false;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * Handles key typed events.
     *
     * @param e The KeyEvent created when a key is typed.
     */
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Handles key released events.
     *
     * @param e The KeyEvent created when a key is released.
     */
    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Starts the SpaceGame program.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SpaceGame().setVisible(true);
            }
        });
    }
}