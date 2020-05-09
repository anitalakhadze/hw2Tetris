package tetris;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JBrainTetris extends JTetris{
    protected JCheckBox brainMode;
    private DefaultBrain brain;
    private Brain.Move bestMove;

    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     *
     * @param pixels
     */
    JBrainTetris(int pixels) {
        super(pixels);
        brain = new BadBrain();
        bestMove = new Brain.Move();
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

        //added code
        brainMode = new JCheckBox("Brain Active", true);
        panel.add(brainMode);

        return panel;
    }

    @Override
    public void tick(int verb) {
        if (verb == DOWN && brainMode.isSelected() &&
                (currentY + currentPiece.getHeight() < board.getHeight() - 4)) {
            board.undo();
            brain.bestMove(board, currentPiece, board.getHeight() - 4, bestMove);
            if (currentPiece != bestMove.piece) super.tick(ROTATE);
            else if (bestMove.x < currentX) super.tick(LEFT);
            else if (bestMove.x > currentX) super.tick(RIGHT);
        }
        super.tick(verb);
    }
}
