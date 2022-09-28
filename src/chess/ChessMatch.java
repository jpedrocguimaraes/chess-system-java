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
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
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
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
		
		if(testCheck(currentPlayer) || board.piece(target) instanceof King && target.getColumn() == source.getColumn() + 2 && testCheckRook(((ChessPiece) board.piece(target)).getColor(), new Position(source.getRow(), source.getColumn() + 1)) || board.piece(target) instanceof King && target.getColumn() == source.getColumn() - 2 && testCheckRook(((ChessPiece) board.piece(target)).getColor(), new Position(source.getRow(), source.getColumn() - 1))) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em cheque");
		}
		
		ChessPiece movedPiece = (ChessPiece) board.piece(target);
		
		//promotion
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece) board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = testCheck((opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();
		}
		
		//en passant
		if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2) || target.getRow() == source.getRow() + 2) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("Não há peça para ser promovida");
		}
		
		if(!type.equals("R") && !type.equals("N") && !type.equals("B") && !type.equals("Q")) {
			return promoted;
		}
		
		Position position = promoted.getChessPosition().toPosition();
		Piece piece = board.removePiece(position);
		piecesOnTheBoard.remove(piece);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, position);
		piecesOnTheBoard.add(newPiece);
		
		check = testCheck(currentPlayer) ? true : false;
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("R")) return new Rook(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("B")) return new Bishop(board, color);
		return new Queen(board, color);
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
		
		//rook direito
		if(piece instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//rook esquerdo
		if(piece instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//en passant
		if(piece instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				
				if(piece.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
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
		
		//rook direito
		if(piece instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		//rook esquerdo
		if(piece instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		//en passant
		if(piece instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				ChessPiece pawn = (ChessPiece) board.removePiece(target);
				Position pawnPosition;
				
				if(piece.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				
				board.placePiece(pawn, pawnPosition);
			}
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
	
	private boolean testCheckRook(Color color, Position position) {
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		
		for(Piece piece : opponentPieces) {
			boolean[][] moves = piece.possibleMoves();
			
			if(moves[position.getRow()][position.getColumn()]) {
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
		placeNewPiece(8, 'e', new King(board, Color.BLACK, this));
		placeNewPiece(8, 'd', new Queen(board, Color.BLACK));
		placeNewPiece(8, 'a', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'h', new Rook(board, Color.BLACK));
		placeNewPiece(8, 'b', new Knight(board, Color.BLACK));
		placeNewPiece(8, 'g', new Knight(board, Color.BLACK));
		placeNewPiece(8, 'c', new Bishop(board, Color.BLACK));
		placeNewPiece(8, 'f', new Bishop(board, Color.BLACK));
		placeNewPiece(7, 'a', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'b', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'c', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'd', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'e', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'f', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'g', new Pawn(board, Color.BLACK, this));
		placeNewPiece(7, 'h', new Pawn(board, Color.BLACK, this));
		
		placeNewPiece(2, 'a', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'b', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'c', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'd', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'e', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'f', new Pawn(board, Color.WHITE, this));
		placeNewPiece(2, 'g', new Pawn(board, Color.WHITE, this));
		placeNewPiece(6, 'h', new Pawn(board, Color.WHITE, this));
		placeNewPiece(1, 'c', new Bishop(board, Color.WHITE));
		placeNewPiece(1, 'f', new Bishop(board, Color.WHITE));
		placeNewPiece(1, 'b', new Knight(board, Color.WHITE));
		placeNewPiece(1, 'g', new Knight(board, Color.WHITE));
		placeNewPiece(1, 'a', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'h', new Rook(board, Color.WHITE));
		placeNewPiece(1, 'd', new Queen(board, Color.WHITE));
		placeNewPiece(1, 'e', new King(board, Color.WHITE, this));
	}
}