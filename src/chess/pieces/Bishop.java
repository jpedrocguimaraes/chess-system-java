package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Bishop extends ChessPiece {

	public Bishop(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position position = new Position(0, 0);
		
		//noroeste
		position.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
		while(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
			position.setValues(position.getRow() - 1, position.getColumn() - 1);
		}
		
		if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//nordeste
		position.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
		while(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
			position.setValues(position.getRow() - 1, position.getColumn() + 1);
		}
		
		if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//sudeste
		position.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
		while(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
			position.setValues(position.getRow() + 1, position.getColumn() + 1);
		}
		
		if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		//sudoeste
		position.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
		while(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
			position.setValues(position.getRow() + 1, position.getColumn() - 1);
		}
		
		if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
			moves[position.getRow()][position.getColumn()] = true;
		}
		
		return moves;
	}
	
	@Override
	public String toString() {
		return "B";
	}
}