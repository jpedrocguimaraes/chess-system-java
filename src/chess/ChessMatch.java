package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] pieces = new ChessPiece[board.getRows()][board.getColumns()];
		
		for(int i = 0; i < board.getRows(); i++) {
			for(int j = 0; j < board.getColumns(); j++) {
				pieces[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		
		return pieces;
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		return (ChessPiece) capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		Piece piece = board.removePiece(source);
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(piece, target);
		return capturedPiece;
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem");
		}
		
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Não existem movimentos possíveis para a peça escolhida");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode se mover para a posição de destino");
		}
	}
	
	private void placeNewPiece(int row, char column, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(row, column).toPosition());
	}
	
	private void initialSetup() {
		placeNewPiece(8, 'a', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'h', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'e', new King(board, Color.BLACK));
		
		placeNewPiece(1, 'a', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'h', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'e', new King(board, Color.WHITE));
	}
}