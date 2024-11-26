package sudoku;

import java.util.ArrayList;
import java.util.List;

public class SolverDefault implements Solver {
   private static final long serialVersionUID = 1L;
   
   private static final List<Move> EMPTY_MOVES_LIST = new ArrayList<>();
   
   /** Tabla na kojoj se resava Sudoku problem */
   private Board board;

   // =================================================================================
   // Constructor
   // =================================================================================
   public SolverDefault(Board board) {
      this.board = board;
   }

   /**
    * Vraca sledeci potez pri resavanju sudoku problema (ili null, ako resenja nema)
    */
   @Override
   public List<Move> getMove() {
	   List<Move> moves;

	    // Prvo pokusavamo da resimo pomocu "Naked Single"
	    moves = solveNakedSingle();
	    if (isSolutionStep(moves)) {
	        return moves;
	    }

	    // Ako "Naked Single" ne daje rezultat, prelazimo na "Hidden Single"
	    moves = solveHiddenSingle();
	    if (isSolutionStep(moves)) {
	        return moves;
	    }
	    
	    solveLockedCandidates1();

	    // Ako nijedna metoda nije nasla resenje, vracamo praznu listu
	    return EMPTY_MOVES_LIST;
   }

   // ---------------------------------------------------------------------------------
   // Naked Single
   // Polje u kome je preostala samo jedna mogucnost za upis
   // ---------------------------------------------------------------------------------
   private List<Move> solveNakedSingle() {
      for ( int row = 0 ; row < Board.DIMENSION ; row++ ) {
         for ( int column = 0 ; column < Board.DIMENSION ; column++ ) {
            if (!board.isSolved(row, column) && board.getPossibilitiesCount(row, column) == 1) {
               for ( int value = 0 ; value < Board.DIMENSION ; value++ ) {
                  if (board.isPossible(row, column, value)) {
                     List<Move> moves = new ArrayList<>();
                     clue(row, column, value, moves);
                     return moves= zakljucak(row,column,value,moves);
                  }
               }
            }
         }
      }
      return EMPTY_MOVES_LIST;
   }
   
	private List<Move> solveHiddenSingle() {
		for (int row = 0; row < Board.DIMENSION; row++) {
			for (int column = 0; column < Board.DIMENSION; column++) {
				if (!board.isSolved(row, column) && board.getPossibilitiesCount(row, column) >= 2) {
					for (int value = 0; value < Board.DIMENSION; value++) {
						if (board.isPossible(row, column, value)) {
							List<Move> moves = new ArrayList<>();
							int valueCounterInBox = 1;
							for (int x = 3 * (row / 3); x < 3 * (row / 3 + 1); x++) {
								for (int y = 3 * (column / 3); y < 3 * (column / 3 + 1); y++) {
									if (x == row && y == column) continue;
									if (board.isPossible(x, y, value)) valueCounterInBox++;
								}
							}
							if (valueCounterInBox == 1) {
			                     clue(row, column, value, moves);
			                     return moves = zakljucak(row,column,value,moves);
							}
						}
					}
				}
			}
		}

		return EMPTY_MOVES_LIST;
	}
	
	private void solveLockedCandidates1(){
		for (int row = 0; row < Board.DIMENSION; row++) {
			for (int column = 0; column < Board.DIMENSION; column++) {
				if (!board.isSolved(row, column) && board.getPossibilitiesCount(row, column) >= 2) {
					for (int value = 0; value < Board.DIMENSION; value++) {
						if (board.isPossible(row, column, value)) {
							List<Move> moves = new ArrayList<>();
							
							int valueCounterInColumn = 1;
							for (int x = 3 * (row / 3); x < 3 * (row / 3 + 1); x++) {
								if(x==row)continue;
								if (board.isPossible(x, column, value)) valueCounterInColumn++;
							}
							int valueCounterInRow = 1;
							for (int y = 3 * (column / 3); y < 3 * (column / 3 + 1); y++) {
								if (y == column) continue;
								if (board.isPossible(row, y, value)) valueCounterInRow++;
								}
							
							if(valueCounterInColumn > 1 && valueCounterInRow == 1) {
								for(int x = 0; x < Board.DIMENSION; x++ ) {
									if((x==3 * (row / 3)) || (x==3 * (row / 3)+1) || (x==3 * (row / 3)+2)) continue;
									board.disable(x, column, value);
							}
						}
					}
				}
			}
		}
		}

	}
	
   
   
   private boolean isSolutionStep(List<Move> moves) {
      return moves != null && !moves.isEmpty();
   }
   
   private void clue(int row, int column, int value, List<Move> moves) {	   
	   for (int cluerow = 0; cluerow < Board.DIMENSION; cluerow++) {
			if (cluerow == row)
				continue;
			moves.add(new Move(cluerow, column, value, MoveOperation.CLUE, "Clue row"));
		}
		for (int cluecolumn = 0; cluecolumn < Board.DIMENSION; cluecolumn++) {
			if (cluecolumn == column)
				continue;
			moves.add(new Move(row, cluecolumn, value, MoveOperation.CLUE, "Clue line"));
		}
		for (int x = 3 * (row / 3); x < 3 * (row / 3 + 1); x++) {
			for (int y = 3 * (column / 3); y < 3 * (column / 3 + 1); y++) {
				if (x == row && y == column)
					continue;
				moves.add(new Move(x, y, value, MoveOperation.CLUE, ""));
			}
		}
   }
   
   private List<Move> zakljucak(int row, int column, int value, List<Move> moves){
	// Zakljucak ... bice ofarban zelenom bojom
		moves.add(new Move(row, column, value, MoveOperation.CONCLUSION,
				"Hidden Single (" + (row) + ", " + (column) + ") : " + (value + 1)));

		// "Rezultat razmisljanja" ... broj koji ce biti upisan u odgovarajucu celiju
		// tabele
		moves.add(new Move(row, column, value, MoveOperation.WRITE,
				"Hidden Single (" + (row) + ", " + (column) + ") : " + (value + 1)));
		return moves;
   }
}
