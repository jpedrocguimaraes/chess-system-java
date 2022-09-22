package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
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
		
		//cima
		position.setValues(this.position.getRow() - 1, this.position.getColumn());
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//noroeste
		position.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//nordeste
		position.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//esquerda
		position.setValues(this.position.getRow(), this.position.getColumn() - 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//direita
		position.setValues(this.position.getRow(), this.position.getColumn() + 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//sudoeste
		position.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//sudeste
		position.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//baixo
		position.setValues(this.position.getRow() + 1, this.position.getColumn());
		if(getBoard().positionExists(position) && canMove(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		return moves;
	}
	
	@Override
	public String toString() {
		return "K";
	}
}