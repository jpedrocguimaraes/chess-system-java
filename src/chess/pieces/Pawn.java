package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
	
	private ChessMatch chessMatch;
	
	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] moves = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position position = new Position(0, 0);
		
		if(getColor() == Color.WHITE) {
			//um acima
			position.setValues(this.position.getRow() - 1, this.position.getColumn());
			if(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//dois acima
			position.setValues(this.position.getRow() - 2, this.position.getColumn());
			Position aux = new Position(this.position.getRow() - 1, this.position.getColumn());
			if(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position) && getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux) && getMoveCount() == 0) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//diagonal esquerda
			position.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
			if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//diagonal direita
			position.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
			if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//en passant
			if(this.position.getRow() == 3) {
				Position left = new Position(this.position.getRow(), this.position.getColumn() - 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					moves[left.getRow() - 1][left.getColumn()] = true;
				}
				
				Position right = new Position(this.position.getRow(), this.position.getColumn() + 1);
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					moves[right.getRow() - 1][right.getColumn()] = true;
				}
			}
		} else {
			//um abaixo
			position.setValues(this.position.getRow() + 1, this.position.getColumn());
			if(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//dois abaixo
			position.setValues(this.position.getRow() + 2, this.position.getColumn());
			Position aux = new Position(this.position.getRow() + 1, this.position.getColumn());
			if(getBoard().positionExists(position) && !getBoard().thereIsAPiece(position) && getBoard().positionExists(aux) && !getBoard().thereIsAPiece(aux) && getMoveCount() == 0) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//diagonal esquerda
			position.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
			if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//diagonal direita
			position.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
			if(getBoard().positionExists(position) && isThereOpponentPiece(position)) {
				moves[position.getRow()][position.getColumn()] = true;
			}
			
			//en passant
			if(this.position.getRow() == 4) {
				Position left = new Position(this.position.getRow(), this.position.getColumn() - 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					moves[left.getRow() + 1][left.getColumn()] = true;
				}
				
				Position right = new Position(this.position.getRow(), this.position.getColumn() + 1);
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					moves[right.getRow() + 1][right.getColumn()] = true;
				}
			}
		}
		
		return moves;
	}
	
	@Override
	public String toString() {
		return "P";
	}
}