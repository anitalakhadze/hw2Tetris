package tetris;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris{
    protected JCheckBox brainMode;
    protected JSlider adversary;
    protected JLabel statusLabel;
    private DefaultBrain brain;
    private Brain.Move bestMove;
    private int currentCount;

    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JBrainTetris(int pixels) {
        super(pixels);
        brain = new DefaultBrain();
        bestMove = new Brain.Move();
        statusLabel = new JLabel("ok");
        currentCount = super.count;
    }

    public static void main(String[] args) {
        // Set GUI Look And Feel Boilerplate.
        // Do this incantation at the start of main() to tell Swing
        // to use the GUI LookAndFeel of the native platform. It's ok
        // to ignore the exception.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        JBrainTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }

    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();

        brainMode = new JCheckBox("Brain Active", true);
        panel.add(brainMode);

        JPanel row = new JPanel();
        panel.add(Box.createVerticalStrut(12));
        row.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0);	// min, max, current
        adversary.setPreferredSize(new Dimension(100, 15));
        row.add(adversary);
        panel.add(row);
        panel.add(statusLabel);
        return panel;
    }

    @Override
    public Piece pickNextPiece() {
        int value = adversary.getValue();
        statusLabel.setText("*ok*");
        int randNumber = random.nextInt(101);
        if (randNumber >= value) return super.pickNextPiece();
        return computeNextWorstPiece();
    }

    private Piece computeNextWorstPiece(){
        double worstScore = 0;
        Piece worstPiece = null;
        for (Piece piece : pieces){
            brain.bestMove(board, piece, board.getHeight() - TOP_SPACE, bestMove);
            if (bestMove.score > worstScore) {
                worstScore = bestMove.score;
                worstPiece = bestMove.piece;
            }
        }
        return worstPiece;
    }

    @Override
    public void tick(int verb) {

        if (verb == DOWN && brainMode.isSelected()
//               && (currentY + currentPiece.getHeight() < board.getHeight() - 4)
        ) {

            if (currentCount != super.count){
                board.undo();
                brain.bestMove(board, currentPiece, board.getHeight() - TOP_SPACE, bestMove);
                currentCount = super.count;
            }

            if (currentPiece != bestMove.piece) super.tick(ROTATE);

            if (bestMove.x < currentX) super.tick(LEFT);
            else if (bestMove.x > currentX) super.tick(RIGHT);
//            else super.tick(DROP);
        }
        super.tick(verb);
    }
}
