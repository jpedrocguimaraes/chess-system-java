package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {
	
	private ChessMatch chessMatch;
	
	public King(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	private boolean canMove(Position position) {
		ChessPiece piece = (ChessPiece) getBoard().piece(position);
		return piece == null || piece.getColor() != getColor();
	}
	
	private boolean testRookCastling(Position position) {
		ChessPiece piece = (ChessPiece) getBoard().piece(position);
		return piece != null && piece instanceof Rook && piece.getColor() == getColor() && piece.getMoveCount() == 0;
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
		
		//rook
		if(getMoveCount() == 0 && !chessMatch.getCheck()) {
			//rook direito
			Position posT1 = new Position(this.position.getRow(), this.position.getColumn() + 3);
			if(testRookCastling(posT1)) {
				Position pos1 = new Position(this.position.getRow(), this.position.getColumn() + 1);
				Position pos2 = new Position(this.position.getRow(), this.position.getColumn() + 2);
				
				if(getBoard().piece(pos1) == null && getBoard().piece(pos2) == null) {
					moves[this.position.getRow()][this.position.getColumn() + 2] = true;
				}
			}
			
			//rook esquerdo
			Position posT2 = new Position(this.position.getRow(), this.position.getColumn() - 4);
			if(testRookCastling(posT2)) {
				Position pos1 = new Position(this.position.getRow(), this.position.getColumn() - 1);
				Position pos2 = new Position(this.position.getRow(), this.position.getColumn() - 2);
				Position pos3 = new Position(this.position.getRow(), this.position.getColumn() - 3);
				
				if(getBoard().piece(pos1) == null && getBoard().piece(pos2) == null && getBoard().piece(pos3) == null) {
					moves[this.position.getRow()][this.position.getColumn() - 2] = true;
				}
			}
		}
		
		return moves;
	}
	
	@Override
	public String toString() {
		return "K";
	}
}