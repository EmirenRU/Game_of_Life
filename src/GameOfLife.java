import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameOfLife {
    private static JFrame frame = new JFrame("Game of Life");
    private static JPanel controlPanel;
    private static JPanel gamePanel;

    private static final int GRID_SIZE = 100;  // Размер сетки
    private static final int CELL_SIZE = 10;  // Размер Клетки

    private Cell[][] gameBoard;
    private ArrayList<JButton> buttons = new ArrayList<>();

    private static Timer timer;
    private static boolean running;

    Clip clip;
    Random random = new Random();

    Thread tSound = new Thread(new Runnable() {
        @Override
        public void run() {

            File musicFile = new File("assets/The Eminence in Shadow OST - Heavy Breeze.wav");
            if (!musicFile.exists())
                System.out.println("File is not exists");

            AudioInputStream audioInputStream = null;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(musicFile);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            } finally {
                if (audioInputStream != null)
                {
                    try {
                        audioInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    });

    public GameOfLife() {
        gameBoard = new Cell[GRID_SIZE][GRID_SIZE];
        initializeGameBoard();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        tSound.start();

        gamePanel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                for (int row = 0; row < GRID_SIZE; row++)
                    for (int col = 0; col < GRID_SIZE; col++)
                    {
                        if (gameBoard[row][col].isAlive())
                        {
                            g.setColor(Color.BLACK);
                            g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                        else
                        {
                            g.setColor(Color.WHITE);
                            g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        }
                    }
            }
        };

        gamePanel.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));

        buttons.add(new JButton("Start"));
        buttons.get(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });


        buttons.add( new JButton("Restart"));
        buttons.get(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeGameBoard();
                gamePanel.repaint();
            }
        });


        buttons.add(new JButton("Stop"));
        buttons.get(2).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopGame();
            }
        });

        controlPanel = new JPanel();
        for (JButton i : buttons)
            controlPanel.add(i);

        frame.add(gamePanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }


    private void initializeGameBoard() {
        for (int row = 0; row < GRID_SIZE; row++)
            for (int col = 0; col < GRID_SIZE; col++) {
                gameBoard[row][col] = new Cell(random.nextBoolean());

            }
    }

    private void startGame()
    {
        ;
        if (!running)
        {
            running = true;
            timer = new Timer(50, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Update();
                    gamePanel.repaint();
                }
            });

            timer.start();
        }
    }

    private void Update()
    {
        Cell[][] newGameBoard = new Cell[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int aliveNeighbors = countAliveNeighbors(row, col);
                boolean currentState = gameBoard[row][col].isAlive();
                boolean nextState = computeNextState(currentState, aliveNeighbors);
                newGameBoard[row][col] = new Cell();
                newGameBoard[row][col].setAlive(nextState);
            }
        }

        gameBoard = newGameBoard;
        gamePanel.repaint();
    }

    private int countAliveNeighbors(int row, int col) {
        int count = 0;
        int[][] neighbors = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] neighbor : neighbors) {
            int neighborRow = row + neighbor[0];
            int neighborCol = col + neighbor[1];

            if (neighborRow >= 0 && neighborRow < GRID_SIZE && neighborCol >= 0 && neighborCol < GRID_SIZE) {
                if (gameBoard[neighborRow][neighborCol].isAlive()) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean computeNextState(boolean currentState, int aliveNeighbors) {
        if (currentState) {
            return aliveNeighbors == 2 || aliveNeighbors == 3;
        } else {
            return aliveNeighbors == 3;
        }
    }

    private void stopGame()
    {
        if (running)
        {
            running = false;
            timer.stop();
        }
    }

}
