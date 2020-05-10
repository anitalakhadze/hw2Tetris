// Board.java
package tetris;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board	{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] board;
	private boolean DEBUG = true;
	boolean committed;
	
	int[] widths;
	int[] heights;

	boolean[][] tempBoard;

	boolean[][] xBoard;
	int[] xWidths;
	int[] xHeights;
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		board = new boolean[width][height];
		committed = true;

		tempBoard = new boolean[width][height];

		xBoard = new boolean[width][height];
		xWidths = new int[height];
		xHeights = new int[width];

		heights = new int[width];
		for (int x = 0; x < getWidth(); x++) {
			heights[x] = getColumnHeight(x);
			xHeights[x] = getColumnHeight(x);
		}

		widths = new int[height];
		for (int y = 0; y < getHeight(); y++){
			widths[y] = getRowWidth(y);
			xWidths[y] = getRowWidth(y);
		}

	}
	
	
	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() {
		return width;
	}
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	

	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() {
		int maxHeight = 0;
		for (int i = 0; i < getWidth(); i++) {
			maxHeight = Math.max(maxHeight, getColumnHeight(i));
		}
		return maxHeight;
	}
	
	
	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			for (int x = 0; x < getWidth(); x++) {
				if(heights[x] != getColumnHeight(x))
					throw new RuntimeException("Height is not correct in column " + x);
			}
			for (int y = 0; y < getHeight(); y++){
				if(widths[y] != getRowWidth(y))
					throw  new RuntimeException("Width is not correct in row " + y);
			}
		}
		return;
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	public int dropHeight(Piece piece, int x) {
		int y = 0;
		int[] skirt = piece.getSkirt();
		for (int i = 0; i < piece.getWidth(); i++) {
			y = Math.max(heights[x + i] - skirt[i], y);
		}

		return y;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	public int getColumnHeight(int x) {
		for (int y = getHeight() - 1; y >= 0; y--){
			if (board[x][y]) return y + 1;
		}
		return 0;
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	public int getRowWidth(int y) {
		int counter = 0;
		for (int x = 0; x < getWidth(); x++){
			if (board[x][y]) counter++;
		}
		return counter;
	}

	private boolean inBoundary(int x, int y){
		return (x < getWidth() && x >= 0) && (y < getHeight() && y >= 0);
	}
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) {
		if (!inBoundary(x, y)) return true;
		if (board[x][y]) return true;
		return false;
	}
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if (!committed) throw new RuntimeException("place commit problem");

		TPoint[] pieceBody = piece.getBody();

		for (TPoint point : pieceBody){
			if (!inBoundary(x + point.x, y + point.y)) return PLACE_OUT_BOUNDS;
			if (xBoard[x + point.x][y + point.y]) return PLACE_BAD;
		}


		boolean rowFilled = false;
		for (TPoint point : pieceBody){
			int newX = x + point.x;
			int newY = y + point.y;
			board[newX][newY] = true;
			if(++widths[newY] == getWidth()) rowFilled = true;
			heights[newX] = getColumnHeight(newX);
		}

		committed = false;
		sanityCheck();
		if(rowFilled) return PLACE_ROW_FILLED;
		return PLACE_OK;
	}

	private boolean rowIsFull(int row) {
		for (int col = 0; col < getWidth(); col++) {
			if(!getGrid(col, row)) return false;
		}
		return true;
	}
	
	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		int[] shiftedWidth = new int[getHeight()];
		int toRow = 0;
		int rowsCleared = 0;
		for (int fromRow = 0; fromRow < getHeight(); fromRow++) {
			if(!rowIsFull(fromRow)){
				copyRow(tempBoard, fromRow, toRow);
				shiftedWidth[toRow] = widths[fromRow];
				toRow++;
			} else rowsCleared++;
		}

		this.board = tempBoard;
		this.widths = shiftedWidth;
		for (int col = 0; col < heights.length; col++) {
			heights[col] = getColumnHeight(col);
		}

		committed = false;
		sanityCheck();
		return rowsCleared;
	}

	private void copyRow(boolean[][] result, int fromRow, int toRow) {
		for (int col = 0; col < getWidth(); col++) {
			result[col][toRow] = board[col][fromRow];
		}
	}


	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		restore(xBoard, board, xWidths, widths, xHeights, heights);

		committed = true;
		sanityCheck();
	}

	private void backup(boolean[][] srcBoard, boolean[][] dstBoard,
						int[] srcWidths, int[]dstWidths,
						int[] srcHeights, int[] dstHeights){
		for (int row = 0; row < getWidth(); row++) {
			System.arraycopy(srcBoard[row], 0, dstBoard[row], 0, srcBoard[row].length);
		}
		System.arraycopy(srcWidths, 0, dstWidths, 0, srcWidths.length);
		System.arraycopy(srcHeights, 0, dstHeights, 0, srcHeights.length);
	}

	private void restore(boolean[][] srcBoard, boolean[][] dstBoard,
						 int[] srcWidths, int[]dstWidths,
						 int[] srcHeights, int[] dstHeights){
		for (int row = 0; row < getWidth(); row++) {
			System.arraycopy(srcBoard[row], 0, dstBoard[row], 0, srcBoard[row].length);
		}
		System.arraycopy(srcWidths, 0, dstWidths, 0, srcWidths.length);
		System.arraycopy(srcHeights, 0, dstHeights, 0, srcHeights.length);
	}


	/**
	 Puts the board in the committed state.
	*/
	public void commit() {
		committed = true;
		backup(board, xBoard, widths, xWidths, heights, xHeights);
	}

	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++)
			buff.append('-');
		return(buff.toString());
	}
}


