package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Rook;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em cheque");
		}
		
		check = testCheck((opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}

		return (ChessPiece) capturedPiece;
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece piece = (ChessPiece) board.removePiece(source);
		piece.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(piece, target);
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece piece = (ChessPiece) board.removePiece(target);
		piece.decreaseMoveCount();
		board.placePiece(piece, source);
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.thereIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem");
		}
		
		if(currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessException("A peça escolhida não é sua");
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
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		
		for(Piece piece : pieces) {
			if(piece instanceof King) {
				return (ChessPiece) piece;
			}
		}
		
		throw new IllegalStateException("Não existe o rei da cor " + (color == Color.WHITE ? "branca" : "preta") + " no tabuleiro");
	}
	
	private boolean testCheck(Color color) {
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		Position kingPosition = king(color).getChessPosition().toPosition();
		
		for(Piece piece : opponentPieces) {
			boolean[][] moves = piece.possibleMoves();
			
			if(moves[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		
		List<Piece> pieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		
		for(Piece piece : pieces) {
			boolean[][] moves = piece.possibleMoves();
			
			for(int i = 0; i < board.getRows(); i++) {
				for(int j = 0; j < board.getRows(); j++) {
					if(moves[i][j]) {
						Position source = ((ChessPiece) piece).getChessPosition().toPosition();
						Position target = new Position(i, j);
						
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						
						if(!testCheck) {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void placeNewPiece(int row, char column, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(row, column).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece(8, 'e', new King(board, Color.BLACK));
		placeNewPiece(8, 'a', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'h', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'b', new Knight(board, Color.BLACK));
		placeNewPiece(8, 'g', new Knight(board, Color.BLACK));
		placeNewPiece(8, 'c', new Bishop(board, Color.BLACK));
		placeNewPiece(8, 'f', new Bishop(board, Color.BLACK));
		placeNewPiece(7, 'a', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'b', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'c', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'd', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'e', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'f', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'g', new Pawn(board, Color.BLACK));
		placeNewPiece(7, 'h', new Pawn(board, Color.BLACK));
		
		placeNewPiece(2, 'a', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'b', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'c', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'd', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'e', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'f', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'g', new Pawn(board, Color.WHITE));
		placeNewPiece(2, 'h', new Pawn(board, Color.WHITE));
		placeNewPiece(1, 'c', new Bishop(board, Color.WHITE));
		placeNewPiece(1, 'f', new Bishop(board, Color.WHITE));
		placeNewPiece(1, 'b', new Knight(board, Color.WHITE));
		placeNewPiece(1, 'g', new Knight(board, Color.WHITE));
		placeNewPiece(1, 'a', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'h', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'e', new King(board, Color.WHITE));
	}
}