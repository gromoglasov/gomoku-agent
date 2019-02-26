import java.awt.Color;
import java.util.*;

class AlphaBeta140902859 extends GomokuPlayer {

	public Color oponent;
	public boolean firstMove = true;
	public Color me;
	public Move bestMove;
	public int moveCount = 0;
	private int depth = 3;

	public void initialise(Color me) {
		this.me = me;
		if (me == Color.WHITE) this.oponent = Color.BLACK;
		else this.oponent = Color.WHITE;
		this.firstMove = !this.firstMove;
	}

	public Move chooseMove(Color[][] board, Color me) {
		try {
			if (firstMove) initialise(me);
			moveCount++;
			this.bestMove = null;
			int eval = evalMinimax(new Node(board), this.depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true, true);
			return this.bestMove;
		} catch (Exception e) {
			System.out.println("returned nothing");
    	System.out.println("Exception: " + e);
    	e.printStackTrace();
			return null;
		}
	}

	public int evalMinimax(Node currestState, int depth, int alpha, int beta, boolean maximisingPlayer, boolean fatherNode) {
		int evaluation;
 		Color player = maximisingPlayer ? this.me : this.oponent;
		Color stateValue = getGameState(currestState.state);
		// exit condition
		if (depth == 0 || stateValue != Color.RED) return evaluateState(currestState, player);
		for(int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (currestState.getState()[row][col] == null) {
					Node child = new Node(getChildBoard(currestState.getState(), new Move(row, col), player), new Move(row, col));
					currestState.addChild(child);
				}
			}
		}
		if (maximisingPlayer) {
			int maxEvaluation = Integer.MIN_VALUE;
			for (Node child : currestState.children) {
				evaluation = evalMinimax(child, depth - 1, alpha, beta, false, false);
				if (fatherNode && maxEvaluation < evaluation) this.bestMove = child.lastMove;
				maxEvaluation = Math.max(maxEvaluation, evaluation);
				alpha = Math.max(alpha, evaluation);
				if (beta <= alpha || evaluation == Integer.MAX_VALUE) break;
			}
			return maxEvaluation;
		} else {
			int minEvaluation = Integer.MAX_VALUE;
			for (Node child : currestState.children) {
				evaluation = evalMinimax(child, depth - 1, alpha, beta, true, false);
				minEvaluation = Math.min(minEvaluation, evaluation);
				beta = Math.min(beta, evaluation);
				if (beta <= alpha || evaluation == Integer.MIN_VALUE) break;
			}
			return minEvaluation;
		}
	}

	public int evaluateState(Node state, Color nextPlayer) {
		Segment[] segments = state.getSegments();
		int stateEvaluation = 0;
		for (int iterator = 0; iterator < 96; iterator++) {
			int segmentEvaluation = evaluateSegment(segments[iterator], nextPlayer);
			if (segmentEvaluation == Integer.MAX_VALUE) return segmentEvaluation;
			stateEvaluation += segmentEvaluation;
		}
		return stateEvaluation;
	}

	public int evaluateSegment(Segment segment, Color nextPlayer) {
		int myMoves = 0;
		int oponentMoves = 0;
		int noMoves = 0;
		for (int i = 0; i < 5; i++) {
			if(segment.values[i] == this.me) myMoves++;
			else if(segment.values[i] == this.oponent) oponentMoves++;
			else noMoves++;
		}
		if ((myMoves != 0 && oponentMoves != 0) || noMoves == 5) return 0;
		if (myMoves == 5) return Integer.MAX_VALUE / moveCount;

		if (oponentMoves == 5) return Integer.MIN_VALUE / moveCount;
		if (nextPlayer == this.me) {
				if (myMoves == 4) return 131072  / (moveCount);
				else if (myMoves == 3) return 32768 / (moveCount);
				else if (myMoves == 2) return 8192 / (moveCount);
				else if (myMoves == 1)  return 2048 / (moveCount);
				else if (oponentMoves == 4) return -65536 / (moveCount);
				else if (oponentMoves == 3) return -16384 / (moveCount);
				else if (oponentMoves == 2) return -4096 / (moveCount);
				else return -1024 / (moveCount);
		} else {
				if (myMoves == 4) return 65536 / (moveCount);
				else if (myMoves == 3) return 16384 / (moveCount);
				else if (myMoves == 2) return 4096 / (moveCount);
				else if (myMoves == 1) return 1024 / (moveCount);
				else if (oponentMoves == 4) return -131072 / (moveCount);
				else if (oponentMoves == 3) return -32768 / (moveCount);
				else if (oponentMoves == 2) return -8192 / (moveCount);
				else return -2048 / (moveCount);
		}
	}

	public Color[][] getChildBoard(Color[][] board, Move move, Color player) {
		Color[][] childBoard = new Color[board.length][board[0].length];
		for (int i = 0; i < childBoard.length; i++) {
			childBoard[i] = Arrays.copyOf(board[i], board[i].length);
		}
		childBoard[move.row][move.col] = player;
		return childBoard;
	}

	public Color getGameState(Color[][] board) {
		Color sequence = Color.RED;
		boolean foundEmpty = false;
		for (int row = 0; row < 8; row ++) {
			if (sequence != Color.RED) break;
			for (int col = 0; col < 8; col ++) {
				if (board[row][col] == null) foundEmpty = true;
				else sequence = pointSequence(board, new int[]{row, col});
				if (sequence != Color.RED) break;
			}
		}
		return (sequence == Color.RED && !foundEmpty) ? Color.BLUE : sequence;
		// return blue for no drawn
		// return red for game not ended
	}

	public Color pointSequence(Color[][] board, int[] point) {
		Color playerColor = board[point[0]][point[1]];
		int rowCount, colCount;
		int seq = 1;
		if (point[0] + 4 < 8) { // check vertically
			for (rowCount = 1; rowCount < 5; rowCount++) {
				if (board[point[0] + rowCount][point[1]] == playerColor) seq++;
				else break;
			}
			if (seq == 5) return playerColor;
		}
		seq = 1;
		if (point[1] + 4 < 8) { // check horizontally
			for (colCount = 1; colCount < 5; colCount++) {
				if (board[point[0]][point[1]+colCount] == playerColor) seq++;
				else break;
			}
			if (seq == 5) return playerColor;
		}
		seq = 1;
		if (point[0] + 4 < 8 && point[1] + 4 < 8) { // check right diagonal
			for (colCount = 1, rowCount = 1; colCount < 5; colCount++, rowCount++) {
				if (board[point[0] + rowCount][point[1] + colCount] == playerColor) seq++;
				else break;
			}
			if (seq == 5) return playerColor;
		}
		seq = 1;
		if (point[0] + 4 < 8 && point[1] - 4 >= 0) { // check left diagonal
			for (colCount = 1, rowCount = 1; colCount < 5; colCount++, rowCount++) {
				if (board[point[0] + rowCount][point[1] - colCount] == playerColor) seq++;
				else break;
			}
			if (seq == 5) return playerColor;
		}
		return Color.RED;
	}

	class Node {
		private Color[][] state;
    private int valuation;
		public ArrayList<Node> children;
		private Move lastMove;
    private Segment[] segments;

		public Node(Color[][] state, Move lastMove) {
			this.state = state;
			this.lastMove = lastMove;
			this.children = new ArrayList<Node>();
      this.segments = seperateIntoSegments(state);
		}

		public Node(Color[][] state) {
			this.state = state;
			this.lastMove = null;
			this.children = new ArrayList<Node>();
			this.segments = seperateIntoSegments(state);
		}

		public Color[][] getState() {
			return this.state;
		}

		public void addChild(Node child) {
			children.add(child);
		}

		public Move getLastMove() {
			return this.lastMove;
		}

		public Segment[] getSegments() {
			return this.segments;
		}

    public Segment[] seperateIntoSegments(Color[][] board) {
      Segment[] segments = new Segment[96];
			int segmentCounter = 0;
			// populate horizontal
      for (int row = 0; row < 8; row++) {
        for (int col = 0; col < 4; col++) {
					segments[segmentCounter] = new Segment("hor", new Move(row, col), board);
					segmentCounter++;
        }
      }
			// populate vertical
			for (int col = 0; col < 8; col++) {
        for (int row = 0; row < 4; row++) {
					segments[segmentCounter] = new Segment("ver", new Move(row, col), board);
					segmentCounter++;
        }
      }
			// populate right diagonal
			for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
					segments[segmentCounter] = new Segment("rightD", new Move(row, col), board);
					segmentCounter++;
        }
      }
			for (int row = 0; row < 4; row++) {
				for (int col = 7; col > 3; col--) {
					segments[segmentCounter] = new Segment("leftD", new Move(row, col), board);
					segmentCounter++;
				}
			}
      return segments;
    }
	}

	class Segment {
		public String direction;
		public Move position;
    public Color[] values;
		public boolean isFull = false;

		public Segment(String direction, Move position, Color[][] board) {
			this.direction = direction;
			this.position = position;
      this.values = getValues(board, position, direction);
		}

    private Color[] getValues(Color[][] board, Move position, String direction) {
      Color[] values = new Color[5];
      if (direction == "hor") for (int i = 0; i < 5; i++) values[i] = board[position.row][position.col + i];
      else if (direction == "ver") for (int i = 0; i < 5; i++) values[i] = board[position.row + i][position.col];
      else if (direction == "leftD") for (int i = 0; i < 5; i++) values[i] = board[position.row + i][position.col - i];
      else if (direction == "rightD") for (int i = 0; i < 5; i++) values[i] = board[position.row + i][position.col + i];
      return values;
    }
	}
}
