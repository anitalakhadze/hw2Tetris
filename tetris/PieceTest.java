package tetris;

import static org.junit.Assert.*;
import java.util.*;

import org.junit.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;
	private Piece[] pieces = Piece.getPieces();

	@Before
	public void setUp() throws Exception {
		
		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();
		
		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}
	
	// Here are some sample tests to get you started
	
	@Test
	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	@Test
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}

	@Test
	public void testSampleEquals() {
		assertEquals(false, pyr1.equals(pyr2));
		assertEquals(true, pyr1.equals(pyr1));
		assertEquals(false, pyr1.equals(pyr4));
		assertEquals(pyr1, pyr4.computeNextRotation()) ;
		assertNotEquals(pyr3, pyr3.computeNextRotation());
	}

	@Test
	public void testSampleRotation() {
		Piece stick = new Piece(Piece.STICK_STR);
		Piece stickRotated = stick.computeNextRotation();
		int[] stickSkirts = stickRotated.getSkirt();
		for (TPoint point : stickRotated.getBody()){
			System.out.println(point);
		}
		for (int i = 0; i < stickSkirts.length; i++) {
			System.out.println("stickSkirts[" + i + "] is " + stickSkirts[i]);
		}
		System.out.println("Width after rotation is " + stickRotated.getWidth() +
				" height is " + stickRotated.getHeight());
	}

	@Test
	public void testSampleRotation1() {
		Piece pyr = new Piece(Piece.PYRAMID_STR);
		Piece pyrRotated = pyr.computeNextRotation();
		int[] pyrSkirts = pyrRotated.getSkirt();
		for (TPoint point : pyrRotated.getBody()){
			System.out.println(point);
		}
		for (int i = 0; i < pyrSkirts.length; i++) {
			System.out.println("pyrSkirts[" + i + "] is " + pyrSkirts[i]);
		}
		System.out.println("Width after rotation is " + pyrRotated.getWidth() +
				" height is " + pyrRotated.getHeight());
	}

	@Test
	public void testFastRotationStick(){
		Piece stick = pieces[0];
		Piece rotatedStick = stick.computeNextRotation();
		assertTrue(rotatedStick.equals(stick.fastRotation()));
	}

	@Test
	public void testFastRotationL1(){
		Piece L1 = pieces[1];
		Piece myL1 = new Piece(Piece.L1_STR);
		Piece L1_2 = myL1.computeNextRotation();
		Piece L1_3 = L1_2.computeNextRotation();
		Piece L1_4 = L1_3.computeNextRotation();
		Piece L1_5 = L1_4.computeNextRotation();

		assertEquals(L1_2, L1.fastRotation());
		assertEquals(L1_3, L1.fastRotation().fastRotation());
		assertEquals(L1_4, L1.fastRotation().fastRotation().fastRotation());
		assertEquals(L1_5, L1);
	}

	@Test(expected = RuntimeException.class)
	public void testParseException(){
		new Piece("ablabla");
	}
	
}
