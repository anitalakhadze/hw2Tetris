package tetris;

import javax.swing.*;
import java.awt.*;

/** Here are variables which we need in this class.
 * if brainMode is checked, tetris is auto-played by AI.
 * JSlider controls the intervention of "adversary".
 * StatusLabel indicates if the figures are chosen by random or by adversary.
 * brain is the DefaultBrain object we use to compute bestMove.
 * currentCount is used in the process of computing the bestMove only once for each piece.
 */
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

    /**
     * Creates control panel with some added features - check box and a slider.
     * @return
     * returns the panel.
     */
    @Override
    public JComponent createControlPanel() {
        JComponent panel = super.createControlPanel();

        brainMode = new JCheckBox("Brain Active");
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

    /**
     * If the value of "adversary" JSlider is 0, "adversary"
     * never intervenes in the game. If the value is 100, "adversary"
     * intervenes during each move.
     * @return
     * If the value is between 0-100,
     * we compute the random number and if the number is equal to or more than
     * the value, we call pickNextPiece() of the parent, otherwise
     * we return the piece computed by computeNextWorstPiece();
     */
    @Override
    public Piece pickNextPiece() {
        int value = adversary.getValue();
        statusLabel.setText("*ok*");
        int randNumber = random.nextInt(101);
        if (randNumber >= value) return super.pickNextPiece();
        return computeNextWorstPiece();
    }

    /**
     * Uses the brain to compute the worst piece among all the possibilities.
     * @return
     * The piece which has the worst (the largest) score.
     */
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

    /**
     * every time the system calls tick(DOWN) to
     * move the piece down one, JBrainTetris takes the opportunity
     * to move the piece a bit first. Our rule is that the brain
     * may do up to one rotation and one left/right move each time
     * tick(DOWN) is called. JBrainTetris should detect when
     * the JTetris.count variable has changed to know that a
     * new piece is in play, so that we compute the best move only once
     * for each piece. The brain needs the board in a committed state
     * before doing its computation. Therefore, we should do a board.undo()
     * before using the brain.
     * @param verb
     */
    @Override
    public void tick(int verb) {

        if (verb == DOWN && brainMode.isSelected()) {

            if (currentCount != super.count){
                board.undo();
                brain.bestMove(board, currentPiece, board.getHeight() - TOP_SPACE, bestMove);
                currentCount = super.count;
            }

            if (currentPiece != bestMove.piece) super.tick(ROTATE);
            if (bestMove.x < currentX) super.tick(LEFT);
            else if (bestMove.x > currentX) super.tick(RIGHT);
        }
        super.tick(verb);
    }
}
