import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.io.*;
import javax.imageio.ImageIO;

public class AufbauSpiel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }
}

class Game {
    private JFrame frame;
    private JPanel gamePanel;
    private JLabel treasuryLabel;
    private int treasury = 1000; // Startwert der Schatzkammer
    private final ArrayList<Building> buildings = new ArrayList<>();
    private Image backgroundImage;
    private boolean financialMode = false;
    private int income = 0;
    private int upkeep = 0;
    private JPanel financialPanel;
    private Timer timer;
    private boolean isPaused = false;
    private int fontSize = 14;

    // Verschiedene Gebäude-Kosten
    private final int BUILDING_COST = 100;
    private final int SOLDIER_COST = 50;
    private final int MANU_COST = -50;

    private Building selectedBuilding;

    public Game() {
        // Hintergrundbild laden
        try {
            backgroundImage = ImageIO.read(new File("background.png")); // Pfad zum Hintergrundbild
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hintergrundbild konnte nicht geladen werden.");
        }

        frame = new JFrame("Aufbauspiel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel scaleFontSize = new JPanel();
        scaleFontSize.setLayout(new BoxLayout(scaleFontSize, BoxLayout.X_AXIS));
        JButton larger = new JButton("+");
        larger.addActionListener(e -> adjustFontSize(1));
        JButton smaller = new JButton("-");
        smaller.addActionListener(e -> adjustFontSize(-1));

        scaleFontSize.add(larger);
        scaleFontSize.add(smaller);
        scaleFontSize.setBounds(650, 500, 80, 30);
        frame.add(scaleFontSize);

        // Menüleiste
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Modus");

        JMenuItem easyModeItem = new JMenuItem("Easy Mode");
        easyModeItem.addActionListener(e -> {
            financialMode = false;
            financialPanel.setVisible(false);
            updateTreasuryLabel();
        });

        JMenuItem financialModeItem = new JMenuItem("Finanzmodus");
        financialModeItem.addActionListener(e -> {
            financialMode = true;
            financialPanel.setVisible(true);
            updateTreasuryLabel();
        });

        menu.add(easyModeItem);
        menu.add(financialModeItem);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);

        // Oberes Panel mit Schatzkammer-Anzeige
        JPanel topPanel = new JPanel(new BorderLayout());
        treasuryLabel = new JLabel("Schatzkammer: " + treasury);
        treasuryLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        topPanel.add(treasuryLabel, BorderLayout.WEST);

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
        frame.add(pausePanel);

        frame.add(topPanel, BorderLayout.NORTH);

        // Finanz-Panel (links)
        financialPanel = new JPanel();
        financialPanel.setLayout(new BoxLayout(financialPanel, BoxLayout.Y_AXIS));
        financialPanel.setBackground(Color.GRAY);
        financialPanel.setPreferredSize(new Dimension(200, frame.getHeight()));
        financialPanel.setVisible(false);

        //line1
            JPanel line1 = new JPanel();
            line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
            JCheckBox taxCheckBox = new JCheckBox();
            
            JLabel taxLabel = new JLabel("Steuern eintreiben");
            taxLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
            taxLabel.setForeground(Color.BLACK);

            taxCheckBox.addActionListener(e -> {
                if (taxCheckBox.isSelected()) {
                    income += 100;
                } else {
                    income -= 100;
                }
                updateTreasuryLabel();
            });
            line1.add(taxCheckBox);
            line1.add(taxLabel);

        //line2
            JPanel line2 = new JPanel();
            line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));
            JCheckBox importCheckBox = new JCheckBox();

            JLabel importLabel = new JLabel("Import stoppen");
            importLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
            importLabel.setForeground(Color.BLACK);

            importCheckBox.addActionListener(e -> {
                if (importCheckBox.isSelected()) {
                    income += 300;
                } else {
                    income -= 0;
                }
                updateTreasuryLabel();
            });

            line2.add(importCheckBox);
            line2.add(importLabel);
        
        //line3
            JPanel line3 = new JPanel();
            line2.setLayout(new BoxLayout(line2, BoxLayout.X_AXIS));
            JCheckBox rohstoffCheckBox = new JCheckBox();

            JLabel rohstoffLabel = new JLabel("Rohstoffe aus Kolonien");
            rohstoffLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
            rohstoffLabel.setForeground(Color.BLACK);

            rohstoffCheckBox.addActionListener(e -> {
                if (rohstoffCheckBox.isSelected()) {
                    income += 300;
                } else {
                    income -= 0;
                }
                updateTreasuryLabel();
            });

            line3.add(rohstoffCheckBox);
            line3.add(rohstoffLabel);



        financialPanel.add(line1);
        financialPanel.add(line2);
        financialPanel.add(line3);

        frame.add(financialPanel, BorderLayout.WEST);

        // Auswahl-Panel für Gebäude
        JPanel buildingPanel = new JPanel();
        buildingPanel.setLayout(new BoxLayout(buildingPanel, BoxLayout.X_AXIS));
        buildingPanel.setBackground(Color.LIGHT_GRAY);
        buildingPanel.setPreferredSize(new Dimension(150, frame.getHeight()));

        JButton buildingButton = new JButton("Schloss (-10)");
        buildingButton.addActionListener(e -> selectedBuilding = new Building(0, 0));
        buildingPanel.add(buildingButton);

        JButton soldierButton = new JButton("Armee (-5)");
        soldierButton.addActionListener(e -> selectedBuilding = new Soldier(0, 0));
        buildingPanel.add(soldierButton);

        JButton manuButton = new JButton("Manufaktur (+5)");
        manuButton.addActionListener(e -> selectedBuilding = new Manufaktur(0, 0));
        buildingPanel.add(manuButton);
        buildingPanel.setBounds(300, 500,400,30);

        frame.add(buildingPanel);

        // Spielfeld-Panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMap(g);
                for (Building building : buildings) {
                    building.draw(g);
                }
            }
        };
        gamePanel.setBackground(Color.LIGHT_GRAY);
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addBuilding(e.getX(), e.getY());
            }
        });
        frame.add(gamePanel, BorderLayout.CENTER);

        // Spiel-Logik starten
        startGameLoop();

        frame.setVisible(true);
    }

    private void adjustFontSize(int adjustment) {
        fontSize += adjustment;
        treasuryLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        financialPanel.setFont(new Font("Arial", Font.PLAIN, fontSize));
        frame.repaint();
    }

    private void addBuilding(int x, int y) {
        if (selectedBuilding != null) {
           
            int cost = -1;
            if(selectedBuilding instanceof Soldier){
                cost = SOLDIER_COST;
                upkeep += cost;
            } else if(selectedBuilding instanceof Manufaktur){
                cost = MANU_COST;
                income -= cost;
                //System.out.println("###"+income+"\t"+cost);
            } else{
                System.out.println("hey");
                upkeep += BUILDING_COST;
                cost = BUILDING_COST;
            }

            if (treasury >= upkeep) {
                selectedBuilding.setPosition(x, y);
                buildings.add(selectedBuilding);
                treasury -= upkeep;
                updateTreasuryLabel();
                gamePanel.repaint();
                selectedBuilding = null;
            } else {
                JOptionPane.showMessageDialog(frame, "Sie sind bankrot!");
            }
        }
    }

    private void updateTreasuryLabel() {
        String labelText = "Schatzkammer: " + treasury;
        if (financialMode) {
            labelText += " (Einnahmen: +" + income + ", Ausgaben: -" + upkeep + ")";
        }
        treasuryLabel.setText(labelText);
    }

    private void startGameLoop() {
        timer = new Timer();
        upkeep = 30; // Unterhalt: Beamte usw.
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    //System.out.println(treasury+"\t\t"+ (-1*income)+"\t"+(-1*upkeep));
                    treasury = treasury - ((-1*income) - (-1*upkeep));
                    if (treasury < 0) treasury = 0; // Kein negativer Schatzkammer-Wert
                    updateTreasuryLabel();
                    if (treasury == 0) {
                        JOptionPane.showMessageDialog(frame, "Game Over: Schatzkammer leer!");
                        timer.cancel();
                    }
                }
            }
        }, 0, 1000); // Jede Sekunde ausführen
    }

    private void togglePause(JButton button) {
        isPaused = !isPaused;
        button.setText(isPaused ? "Weiter" : "Pausieren");
    }

    private void drawMap(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, gamePanel.getWidth(), gamePanel.getHeight(), null);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        }
    }
}

class Building {
     int x, y;
    private Color facadeColor;

    public Building(int x, int y) {
        this.x = x;
        this.y = y;
        this.facadeColor = getRandomFacadeColor();
    }

    private Color getRandomFacadeColor() {
        Color[] colors = {Color.YELLOW, Color.WHITE, new Color(255, 165, 0), new Color(255, 215, 0)}; // Gelb, Weiß, Ocker, Orange
        Random random = new Random();
        return colors[random.nextInt(colors.length)];
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        // Stufen des Schlosses
        g.setColor(Color.GRAY);
        g.fillRect(x - 55, y +20, 120, 10);

        // Fassade des Schlosses
        g.setColor(facadeColor);
        g.fillRect(x - 50, y - 20, 110, 40);

        // Fenster
        g.fillRect(x - 50, y - 10, 10, 20);
        g.setColor(Color.BLUE);
        g.fillRect(x - 40, y - 10, 10, 20);
        g.setColor(facadeColor);
        g.fillRect(x - 30, y - 10, 10, 20);
        g.setColor(Color.BLUE);
        g.fillRect(x - 20, y - 10, 10, 20);
        g.setColor(facadeColor);
        g.fillRect(x -10, y - 10, 10, 20);
        g.setColor(Color.BLUE);
        g.fillRect(x , y - 10, 10, 20);
        g.setColor(facadeColor);
        g.fillRect(x + 10, y - 10, 10, 20);
        g.setColor(Color.BLUE);
        g.fillRect(x + 20, y - 10, 10, 20);
        g.setColor(facadeColor);
        g.fillRect(x + 30, y - 10, 10, 20);
        g.setColor(Color.BLUE);
        g.fillRect(x + 40, y - 10, 10, 20);
        g.setColor(facadeColor);
        g.fillRect(x + 50, y - 10, 10, 20);

       // Dach
       g.setColor(new Color(178, 34, 34)); // Rotes Spitzdach
       g.fillPolygon(new int[]{x - 50, x, x + 60}, new int[]{y - 20, y - 50, y - 20}, 3);
    }
}
class Manufaktur extends Building{
    public Manufaktur(int x, int y){
        super(x, y);
    }

    @Override
    public void draw(Graphics g) {
       Color facadeColor = new Color(178, 34, 34);
       // Fassade des Schlosses
       g.setColor(facadeColor);
       g.fillRect(x - 50, y - 20, 110, 45);
       g.setColor(Color.BLACK);
       g.fillRect(x-50, y-20, 110, 5);
       g.setColor(Color.BLUE);
       g.fillRect(x-40, y-10, 5, 20);
    }
}
class Soldier extends Building {

    public Soldier(int x, int y) {
        super(x, y);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y+25, 25, 40);  // Körper des Soldaten
        g.fillRect(x-5, y+25, 5, 30);
        g.fillRect(x+25, y+25, 5, 30);
        g.setColor(Color.WHITE);        
        g.fillRect(x+10, y+25, 5, 40);

        g.setColor(new Color(227, 188, 154));  // Kopf
        g.fillRect(x , y +5, 25, 20);  

        g.setColor(Color.BLACK);   
        g.fillRect(x , y - 15, 25, 20);  // Hut
        g.setColor(Color.RED);
        g.fillRect(x+5, y-25, 5, 10);
        g.setColor(Color.WHITE);
        g.fillRect(x+10, y-25, 5, 10);
        g.setColor(Color.BLUE);
        g.fillRect(x+15, y-25, 5, 10);
        g.setColor(Color.YELLOW);

        
    }
}
