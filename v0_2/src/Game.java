import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Font;

public class Game extends JFrame{
    //constants
        private int COST_BUILDING;
        private int COST_SOLDIER;
        private int COST_MANUFACTORY;
        private int MAP_WIDTH;
        private int MAP_HEIGHT;
        private int TILE_SIZE; //size of an field
        private int STEP_SIZE;
        private int fontSize;
        private JPanel treasuryLabel;
        private JPanel financialPanel;
        private JPanel scaleFontSize;
        private JPanel pausePanel;
        private boolean isPaused;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new Game().run());
    }

    Game(){
        //initilise the constants
            this.COST_BUILDING=-100;
            this.COST_MANUFACTORY=-50;
            this.MAP_HEIGHT = 2000;
            this.MAP_WIDTH = 2000;
            this.TILE_SIZE = 5;
            this.COST_SOLDIER=-50;
            this.STEP_SIZE = 10;
            this.isPaused = false;


        //setup the frame
            setTitle("Mein Aufbauspiel");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);

            

        //pause panel
            JPanel pausePanel = new JPanel();
            JButton pauseButton = new JButton("Pausieren");
            pauseButton.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode()== KeyEvent.VK_SPACE){
                        isPaused = !isPaused;
                        togglePause(pauseButton);
                    }
                }

                @Override
                public void keyReleased(KeyEvent e) { }

                @Override
                public void keyTyped(KeyEvent e) { }
            });
            pauseButton.setBounds(650, 20, 150, 50);
            pauseButton.setSize(150, 50);
            pauseButton.addActionListener(e -> togglePause(pauseButton));
            pausePanel.add(pauseButton);
            pausePanel.setBounds(650,20, 150,50);

        //font size scaling
            scaleFontSize = new JPanel();
            scaleFontSize.setLayout(new BoxLayout(scaleFontSize, BoxLayout.X_AXIS));
            JButton larger = new JButton("+");
            larger.addActionListener(e -> adjustFontSize(1));
            JButton smaller = new JButton("-");
            smaller.addActionListener(e -> adjustFontSize(-1));

            scaleFontSize.add(larger);
            scaleFontSize.add(smaller);
            scaleFontSize.setBounds(650, 500, 80, 30);
            this.add(scaleFontSize);

        //setup the map
            GamePanel gamePanel = new GamePanel(MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, STEP_SIZE);
            add(gamePanel);

        //controlling inputs
            this.addKeyListener(new KeyListener(){
                @Override
                public void keyPressed(KeyEvent e) {
                    gamePanel.handleKeyPress(e);
                }

                @Override
                public void keyReleased(KeyEvent e) { }

                @Override
                public void keyTyped(KeyEvent e) { }
            });

        setVisible(true);
    }



    public void run(){

    }

    private void adjustFontSize(int adjustment) {
        fontSize += adjustment;
        if(treasuryLabel!= null) treasuryLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        if(financialPanel != null) financialPanel.setFont(new Font("Arial", Font.PLAIN, fontSize));
        repaint();
    }

    private void togglePause(JButton button) {
        isPaused = !isPaused;
        button.setText(isPaused ? "Weiter" : "Pausieren");
    }

    
}