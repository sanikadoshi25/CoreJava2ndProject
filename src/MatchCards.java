import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.io.File;
import java.net.URL;

public class MatchCards {
    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        @Override
        public String toString() {
            return cardName;
        }
    }

    String[] cardList = { 
        "Darkness", "Double", "Fairy", "Fightning", "Fire",
        "Grass", "Lightning", "Metal", "Psychic", "Water"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 90;
    int cardHeight = 128;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    JFrame frame = new JFrame("Pokemon Match Cards");
    JLabel errorLabel = new JLabel();
    JLabel scoreLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    int scoreCount = 0;  // Score variable to keep track of matched pairs
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;

    MatchCards() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setSize(columns * cardWidth, rows * cardHeight + 100);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        errorLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        errorLabel.setHorizontalAlignment(JLabel.CENTER);
        errorLabel.setText("Errors: " + errorCount);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setText("Score: " + scoreCount);

        textPanel.setPreferredSize(new Dimension(columns * cardWidth, 30));
        textPanel.setLayout(new GridLayout(1, 2));
        textPanel.add(errorLabel);  // Left side: Errors
        textPanel.add(scoreLabel);  // Right side: Score
        frame.add(textPanel, BorderLayout.NORTH);

        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < rows * columns; i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setIcon(cardBackImageIcon); 
            tile.setFocusable(false);
            tile.addActionListener(e -> {
                if (!gameReady || card1Selected == tile || card2Selected == tile) {
                    return;
                }
                handleCardClick(tile);
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(columns * cardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(e -> restartGame());
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        hideCardTimer = new Timer(1500, e -> hideCards());
        hideCardTimer.setRepeats(false);

        Timer showCardsTimer = new Timer(2000, e -> flipAllCardsBack());
        showCardsTimer.setRepeats(false);
        showCardsTimer.start();

        startGame();
    }

    void setupCards() {
        cardSet = new ArrayList<>();
        try {

            for (String cardName : cardList) {
                String path = "F:/JavaProject/CardMatchingGame/Images/" + cardName + ".jpg";
                File file = new File(path);

                // If image not found in directory, try src path
                if (!file.exists()) {
                    path = "src/Images/" + cardName + ".jpg";
                }
                Image cardImg = loadImage(path);
                if (cardImg == null) {
                    path = "src/Images/DefaultCard.jpg";  
                    cardImg = loadImage(path);
                }

                if (cardImg != null) {
                    ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                    cardSet.add(new Card(cardName, cardImageIcon));
                }
            }

            // Duplicate the cards for matching pairs
            cardSet.addAll(cardSet);

            String backPath = "F:/JavaProject/CardMatchingGame/Images/Back.jpg";
            File backFile = new File(backPath);

            if (!backFile.exists()) {
                backPath = "src/Images/Back.jpg";
            }

            Image cardBackImg = loadImage(backPath);
            if (cardBackImg != null) {
                cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
            }

        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Load image from a given path and handle errors gracefully
    private Image loadImage(String path) {
        try {
            URL imageUrl = new File(path).toURI().toURL();
            return new ImageIcon(imageUrl).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image from " + path);
            return null;
        }
    }

    void shuffleCards() {
        // Shuffle the cards
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    void handleCardClick(JButton tile) {
        int index = board.indexOf(tile);

        if (card1Selected == null) {
            card1Selected = tile;
            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
        } else if (card2Selected == null) {
            card2Selected = tile;
            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

            if (!cardSet.get(board.indexOf(card1Selected)).cardName.equals(cardSet.get(board.indexOf(card2Selected)).cardName)) {
                errorCount++;
                errorLabel.setText("Errors: " + errorCount);
                gameReady = false;
                hideCardTimer.start();
            } else {
                scoreCount++;  // Increment score when cards match
                scoreLabel.setText("Score: " + scoreCount);
                card1Selected = null;
                card2Selected = null;
            }
        }
    }

    void hideCards() {
        if (card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
        }
        card1Selected = null;
        card2Selected = null;
        gameReady = true;
    }

    void flipAllCardsBack() {
        for (JButton button : board) {
            button.setIcon(cardBackImageIcon); // Flip all cards back
        }
        startGame();
    }

    void restartGame() {
        gameReady = false;
        restartButton.setEnabled(false);
        card1Selected = null;
        card2Selected = null;
        shuffleCards();

        for (JButton button : board) {
            button.setIcon(cardBackImageIcon); // Reset all cards to back image
        }

        errorCount = 0;
        scoreCount = 0;  // Reset score when restarting
        errorLabel.setText("Errors: " + errorCount);
        scoreLabel.setText("Score: " + scoreCount);
        startGame();
    }

    void startGame() {
        gameReady = false;
        hideCardTimer.setInitialDelay(1500);
        hideCardTimer.start();
        restartButton.setEnabled(true);
    }

    public static void main(String[] args) {
        new MatchCards();
    }
}
