package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class RenderBoard {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
//    private String teamColor;

    private static final String[][] WHITE_CHESS_START  = {
            {"♜","♞","♝","♛","♚","♝","♞","♜"},
            {"♟","♟","♟","♟","♟","♟","♟","♟"},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {"♙","♙","♙","♙","♙","♙","♙","♙"},
            {"♖","♘","♗","♕","♔","♗","♘","♖"}
    };

    private static final String[][] BLACK_CHESS_START= {
            {"♖","♘","♗","♔","♕","♗","♘","♖"},
            {"♙","♙","♙","♙","♙","♙","♙","♙"},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {" "," "," "," "," "," "," "," "},
            {"♟","♟","♟","♟","♟","♟","♟","♟"},
            {"♜","♞","♝","♚","♛","♝","♞","♜"}
    };

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String blackHeader = "    h  g  f  e  d  c  b  a    ";

        out.print(ERASE_SCREEN);
        out.print(SET_TEXT_COLOR_WHITE);

        drawHeader(out, blackHeader);
        drawChessBoard(out, "b");
        drawHeader(out, blackHeader);

        // Reset color at the very end
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    public void render(String teamColor){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String whiteHeader = "    a  b  c  d  e  f  g  h    ";
        String blackHeader = "    h  g  f  e  d  c  b  a    ";


        if (teamColor.equalsIgnoreCase("WHITE")){
            drawHeader(out, whiteHeader);
            drawChessBoard(out, "w");
            drawHeader(out, whiteHeader);
        } else if (teamColor.equalsIgnoreCase("BLACK")) {
        drawHeader(out, blackHeader);
        drawChessBoard(out, "b");
        drawHeader(out, blackHeader);}
    }

    // Top and bottom column labels
    private static void drawHeader(PrintStream out, String headerString) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(headerString);
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_BLACK);

        out.println();
    }


    // Draws 8x8 board with row numbers and color reset after each line
    private static void drawChessBoard(PrintStream out, String color) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            // Left-side number (8 down to 1)
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            if (Objects.equals(color, "w")){
                out.print(" " + (8 - boardRow) + " ");
            } else {
                out.print(" " + (boardRow + 1) + " ");
            }

            // Draw the 8 columns
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                boolean isWhiteSquare;
                isWhiteSquare = (boardRow + boardCol) % 2 == 0;
                setWhite(out);


                if (isWhiteSquare){setWhite(out);}
                else {setBlue(out);}


                if (Objects.equals(color, "w")){
                    out.print(" " + WHITE_CHESS_START[boardRow][boardCol] + " ");
                } else{
                    out.print(" " + BLACK_CHESS_START[boardRow][boardCol] + " ");
                }
            }

            // Right-side number
            out.print(SET_BG_COLOR_BLACK);
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(" " + (8 - boardRow) + " ");

            // ✅ Reset background and text to white after the row ends
            out.print(RESET_BG_COLOR);
            out.print(SET_TEXT_COLOR_BLACK);

            out.println();
        }
    }

    private static void setWhite(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
