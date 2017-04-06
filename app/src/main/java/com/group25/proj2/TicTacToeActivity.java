package com.group25.proj2;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static com.group25.proj2.DoneActivity.setWon;


public class TicTacToeActivity extends AppCompatActivity {

    /* Instructions pop-up text (to be displayed in LastGameActivity) */
    public static final String gameTitle = "TIC TAC TOE";
    public static final String gameInstructions = "Pick X or O. If you are X, the phone is O. Alternate with the phone to place your mark on a 3-by-3 board. The first player to get 3 in a row (vertically, horizontally, or diagonally) wins!";
    public static final String scoreInstructions = "When you tie or win, you get 1 point.";
    public static final String livesInstructions = "You must win or tie 3 times IN A ROW to win the game!";

    /* Views that display score and high score */
    private TextView scoreView;
    private TextView highscoreView;

    public static String playerPiece;
    public static String phonePiece;
    public static boolean won;

    private Button tttButtons[];
    private String currentBoard[];

    private TextView pointsPlayerView;
    private TextView pointsPhoneView;
    private static String pointsPlayer;
    private static String pointsPhone;

    private boolean playerTurn;

    boolean minimax_ai;
    int nextMove;
    private int spotsLeft;

    private int winCombo[];
    private int successCount;
    private TextView gameInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        /* Draw score and high score */
        scoreView = (TextView) findViewById(R.id.scoreTTT);
        highscoreView = (TextView) findViewById(R.id.highscoreTTT);
        Score.drawScores(scoreView, highscoreView);

        initGame();
        initButtons();
        initPoints();

        playerTurn = true;
    }

    @Override
    public void onBackPressed() {
    }

    private boolean buttonIsEmpty(String[] board, int i){
        return board[i].equals("");
    }

    private boolean checkRow(String[] board, int i){
        int row = i / 3 * 3;
        if (board[row].equals(board[row + 1]) && board[row].equals(board[row + 2]) && !buttonIsEmpty(board, row)){
            winCombo[0] = row;
            winCombo[1] = row + 1;
            winCombo[2] = row + 2;
            return true;
        }
        return false;
    }

    private boolean checkCol(String[] board, int i){
        int col = i % 3;
        if (board[col].equals(board[col + 3]) && board[col].equals(board[col + 6]) && !buttonIsEmpty(board, col)){
            winCombo[0] = col;
            winCombo[1] = col + 3;
            winCombo[2] = col + 6;
            return true;
        }
        return false;
    }

    private boolean checkTopLeftDiag(String[] board){
        if (board[0].equals(board[4]) && board[0].equals(board[8]) && !buttonIsEmpty(board, 0)) {
            winCombo[0] = 0;
            winCombo[1] = 4;
            winCombo[2] = 8;
            return true;
        }
        return false;
    }

    private boolean checkTopRightDiag(String[] board){
        if (board[2].equals(board[4]) && board[2].equals(board[6]) && !buttonIsEmpty(board, 2)) {
            winCombo[0] = 2;
            winCombo[1] = 4;
            winCombo[2] = 6;
            return true;
        }
        return false;
    }

    private boolean checkDiag(String[] board, int i){
        if (i == 0 || i == 8){
            return checkTopLeftDiag(board);
        } else if (i == 2 || i == 6){
            return checkTopRightDiag(board);
        } else if (i == 4){
            return checkTopLeftDiag(board) || checkTopRightDiag(board);
        }

        return false;
    }

    private boolean checkWin(String[] board, int i){
        if (checkRow(board, i) || checkCol(board, i) || checkDiag(board, i)){
            return true;
        }
        return false;
    }

    private void launchLoseScreen(){
        setWon(false);
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void launchWinScreen(){
        setWon(true);
        Intent intent = new Intent(this, DoneActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void resetButtons(){
        for (int i = 0; i < 9; i++){
            tttButtons[i].setText("");
            currentBoard[i] = "";
        }
    }

    private void restartGame(){
        resetButtons();
        spotsLeft = 9;
        winCombo = new int[3];
        playerTurn = true;
    }

    private void checkGameOver(){
        if (successCount < 3){
            restartGame();
        } else {
            launchWinScreen();
        }
    }

    private void highlightWin(){
        for (int i = 0; i < 3; i++){
            int winIndex = winCombo[i];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tttButtons[winIndex].setBackgroundColor(getResources().getColor(R.color.colorHighlightWin, getTheme()));
            }else {
                tttButtons[winIndex].setBackgroundColor(getResources().getColor(R.color.colorHighlightWin));
            }
        }
    }

    private void updateInfo(){
        if (successCount == 3){
            gameInfoView.setText("CONGRATS! YOU WIN!");
        } else {
            gameInfoView.setText("CONGRATS! " + Integer.toString(3 - successCount) + " ROUND(S) LEFT");
        }
    }

    private void updatePoints(){
        if (playerTurn){
            int pointsPlayerInt = Integer.parseInt(pointsPlayer) + 1;
            pointsPlayer = Integer.toString(pointsPlayerInt);
            pointsPlayerView.setText(pointsPlayer);
            successCount++;
            updateInfo();
        } else {
            Audio.soundPool.play(Audio.wrongAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

            int pointsPhoneInt = Integer.parseInt(pointsPhone) + 1;
            pointsPhone = Integer.toString(pointsPhoneInt);
            pointsPhoneView.setText(pointsPhone);
            gameInfoView.setText("YOU LOST.");

            Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            launchLoseScreen();
                        }
                    });
                }
            }, LastGameActivity.GAMEOVERDELAY);
        }
    }

    private void win(){
        highlightWin();
        updatePoints();

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkGameOver();
                    }
                });
            }
        }, LastGameActivity.GAMEOVERDELAY);
    }

    private void tie(){
        Audio.soundPool.play(Audio.rightAnswerSound, Audio.convertToVolume(Audio.soundVolumeSteps), Audio.convertToVolume(Audio.soundVolumeSteps), 1, 0, 1);

        successCount++;
        updateInfo();

        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkGameOver();
                    }
                });
            }
        }, LastGameActivity.GAMEOVERDELAY);
    }

    private ArrayList<Integer> getIntArray(ArrayList<String> stringArray){
        ArrayList<Integer> ret = new ArrayList<>();
        for (String s: stringArray){
            try {
                ret.add(Integer.parseInt(s));
            } catch(NumberFormatException nfd){
            }
        }
        return ret;
    }

    private String score(String[] board, int depth, boolean minimax, int i){
        boolean turn = playerTurn;
        if (minimax ){
            turn = minimax_ai;
        }

        if (checkWin(board, i)){
            if (turn){
                return Integer.toString(depth - 10);
            } else {
                return Integer.toString(10 - depth);
            }
        } else {
            if (spotsLeft > 0){
                return "ONGOING";
            } else {
                return "0";
            }
        }
    }

    private ArrayList<Integer> getMoves(String[] board){
        ArrayList<Integer> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            if (buttonIsEmpty(board, i)){
                moves.add(i);
            }
        }
        return moves;
    }

    private String[] getNextState(String[] board, int move){
        if ((playerPiece.equals("X") && !minimax_ai)
                || (playerPiece.equals("O") && minimax_ai)){
            board[move] = "X";
        } else {
            board[move] = "O";
        }

        spotsLeft--;
        minimax_ai = !minimax_ai;
        return board;
    }

    private String[] undoMove(String[] board, int move){
        board[move] = "";
        spotsLeft++;
        minimax_ai = !minimax_ai;
        return board;
    }

    private String minimax(String[] board, int depth, int lastIndex){
        String currentScore = score(board, depth, true, lastIndex);
        if (!currentScore.equals("ONGOING")){
            return currentScore;
        }

        depth++;
        ArrayList<String> scores = new ArrayList<>();
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Integer> availableMoves = getMoves(board);
        for (int i = 0; i < availableMoves.size(); i++){
            int move = availableMoves.get(i);
            String state[] = getNextState(board, move);
            scores.add(minimax(state, depth, move));
            moves.add(move);
            board = undoMove(board, move);
        }

        ArrayList<Integer> scoresInt = getIntArray(scores);
        if (minimax_ai == true){
            int maxScore = Collections.max(scoresInt);
            int i = scoresInt.indexOf(maxScore);
            nextMove = moves.get(i);
            return scores.get(i);
        } else {
            int minScore = Collections.min(scoresInt);
            int i = scoresInt.indexOf(minScore);
            nextMove = moves.get(i);
            return scores.get(i);
        }
    }

    private void nextGameState(int i){
        spotsLeft--;
        String currentScore = score(currentBoard, 0, false, i);
        if (currentScore.equals("ONGOING")){
            playerTurn = !playerTurn;
            if (!playerTurn){
                drawPhonePiece(i);
            }
        } else {
            Score.updateScore(1, scoreView, highscoreView);
            if (currentScore.equals("10") || currentScore.equals("-10")){
                win();
            } else if (currentScore.equals("0")){
                tie();
            }
        }
    }

    private void drawPhonePiece(int lastIndex){
        minimax_ai = true;
        minimax(currentBoard, 0, lastIndex);
        if (playerPiece.equals("X")){
            tttButtons[nextMove].setText("O");
            currentBoard[nextMove] = "O";
        } else {
            tttButtons[nextMove].setText("X");
            currentBoard[nextMove] = "X";
        }
        nextGameState(nextMove);
    }

    private void drawPlayerPiece(Button button, int i){
        if (playerTurn == true && buttonIsEmpty(currentBoard, i)){
            button.setText(playerPiece);
            currentBoard[i] = playerPiece;
            nextGameState(i);
        }
    }

    private void setButtonClickListeners(int i){
        final int index = i;
        tttButtons[i].setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                drawPlayerPiece((Button) v, index);
            }
        });
    }

    private void initButtons(){
        tttButtons = new Button[9];
        tttButtons[0] = (Button) findViewById(R.id.ttt0);
        tttButtons[1] = (Button) findViewById(R.id.ttt1);
        tttButtons[2] = (Button) findViewById(R.id.ttt2);
        tttButtons[3] = (Button) findViewById(R.id.ttt3);
        tttButtons[4] = (Button) findViewById(R.id.ttt4);
        tttButtons[5] = (Button) findViewById(R.id.ttt5);
        tttButtons[6] = (Button) findViewById(R.id.ttt6);
        tttButtons[7] = (Button) findViewById(R.id.ttt7);
        tttButtons[8] = (Button) findViewById(R.id.ttt8);
        for (int i = 0; i < 9; i++){
            setButtonClickListeners(i);
        }

        currentBoard = new String[9];
        Arrays.fill(currentBoard, "");
    }

    private void initPoints(){
        pointsPlayerView = (TextView) findViewById(R.id.pointsPlayer);
        pointsPhoneView = (TextView) findViewById(R.id.pointsPhone);
        pointsPlayer = "0";
        pointsPhone = "0";

        pointsPlayerView.setText(pointsPlayer);
        pointsPhoneView.setText(pointsPhone);
    }

    private void initInfo(){
        gameInfoView = (TextView) findViewById(R.id.tttInfo);
        gameInfoView.setText("WIN OR TIE 3 TIMES IN A ROW");
    }

    private void initGame(){
        initButtons();
        initPoints();
        initInfo();

        spotsLeft = 9;
        winCombo = new int[3];
        playerTurn = true;
    }

    protected static void setPlayerPiece(String playerPieceString){
        if (playerPieceString.equals("X")){
            playerPiece = "X";
            phonePiece = "O";
        } else {
            playerPiece = "O";
            phonePiece = "X";
        }
    }
}
