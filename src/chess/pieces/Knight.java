package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}
	
	private boolean canMove(Position position) {
		ChessPiece piece = (ChessPiece) getBoard().piece(position);
		return piece == null || piece.getColor() != getColor();
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position position = new Position(0, 0);
		
		position.setValues(this.position.getRow() - 1, this.position.getColumn() - 2);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() - 2, this.position.getColumn() - 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() - 2, this.position.getColumn() + 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() - 1, this.position.getColumn() + 2);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() + 1, this.position.getColumn() + 2);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() + 2, this.position.getColumn() + 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() + 2, this.position.getColumn() - 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		position.setValues(this.position.getRow() + 1, this.position.getColumn() - 2);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		return moves;
	}

	@Override
	public String toString() {
		return "N";
	}
}