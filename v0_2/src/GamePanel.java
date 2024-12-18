import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

class GamePanel extends JPanel{
    private int offsetX = 0; // X-Versatz der Karte
    private int offsetY = 0; // Y-Versatz der Karte 
    private Color[][] mapColors; //Farben der Karte
    private int t;
    private int h;
    private int w;
    private int s; 

    GamePanel(int w, int h, int t, int s){
        setPreferredSize(new Dimension(w, h));
        mapColors = new Color[h / t][w / t];
        this.t = t;
        this.s = s;
        this.w = w;
        this.h = h;
        generateRandomMap(); //TODO: replace later through a defined map
    }

    private void generateRandomMap(){
        Random rand = new Random();

        for(int i = 0; i < mapColors.length; i++){
            for(int j = 0; j <mapColors[i].length; j++){
                mapColors[i][j] = generateRandomTileColor(rand);
            }
        }
    }

    // Zufällige Farben für Tiles
    private Color generateRandomTileColor(Random rand) {
        int type = rand.nextInt(4);
        return switch (type) {
            case 0 -> Color.GREEN;  // Wälder
            case 1 -> Color.BLUE;   // Wasser
            case 2 -> Color.GRAY;   // Berge
            default -> Color.YELLOW; // Wüste
        };
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-offsetX, -offsetY); // Karte verschieben

        // Karte zeichnen
        for (int row = 0; row < mapColors.length; row++) {
            for (int col = 0; col < mapColors[row].length; col++) {
                g.setColor(mapColors[row][col]);
                g.fillRect(col * t, row * t, t, t);
            }
        }

        g2d.translate(offsetX, offsetY); // Zurücksetzen der Verschiebung
    }

    void handleKeyPress(KeyEvent e){
        switch (e.getKeyCode()) {
            //Pfeiltasten
                case KeyEvent.VK_LEFT -> offsetX = Math.max(0, offsetX - s);
                case KeyEvent.VK_RIGHT -> offsetX = Math.min(w - getWidth(), offsetX + s);
                case KeyEvent.VK_UP -> offsetY = Math.max(0, offsetY - s);
                case KeyEvent.VK_DOWN -> offsetY = Math.min(h - getHeight(), offsetY + s);

            //Pfeiltasten
                case KeyEvent.VK_A -> offsetX = Math.max(0, offsetX - s);
                case KeyEvent.VK_D -> offsetX = Math.min(w - getWidth(), offsetX + s);
                case KeyEvent.VK_W -> offsetY = Math.max(0, offsetY - s);
                case KeyEvent.VK_S -> offsetY = Math.min(h - getHeight(), offsetY + s);
        }

        repaint();
    }
}