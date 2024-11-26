package sudoku;

public interface Factory {
	public static Solver getSolver(Board board) {
		return new SolverDefault(board);
	}
}
