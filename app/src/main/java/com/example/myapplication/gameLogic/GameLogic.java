package com.example.myapplication.gameLogic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.widget.Toast;

import com.example.myapplication.GUIComponents.ImageData;
import com.example.myapplication.GameActivity;
import com.example.myapplication.players.Player;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;


public class GameLogic {
    public Context getContext(){
       return ((GameActivity)gameInterface);
    }

    public interface GameInterface{
        void setShakeEnable(boolean shakeEnable);

        void setClickEnable(boolean clickEnable);

        void refresh(String message,boolean renderImage);

        void changeActivePlayer();

        void setDices(ArrayList<Integer> one, ArrayList<Integer> two);

        void finishGame(String player1, String player2, String winner);

        void setPlayersData(String player1, String player2);

        void setActivePlayer(int num);
    }

    private GameData gameData;
    private GameInterface gameInterface;
    private ImageData imageData;
    private boolean continuedGame = false;
    private ArrayList<Integer> [] diceNumber = new ArrayList[6];

    public GameLogic(int compNum, String player1, String player2, GameInterface gameInterface, ImageData imageData) {
        this.gameInterface = gameInterface;
        this.imageData = imageData;
        gameData = new GameData(compNum, player1, player2);

        ((GameActivity) gameInterface).deleteFile("savedGame.txt");
        initDiceNumbers();
    }

    public GameLogic(GameInterface gameInterface, ImageData imageData) {
        continuedGame = true;
        this.gameInterface = gameInterface;
        this.imageData = imageData;

        gameData = new GameData();
        try (FileInputStream fis = ((GameActivity) gameInterface).openFileInput("savedGame.txt");
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            gameData.loadGame(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        this.gameInterface.setPlayersData(gameData.getPlayers()[0].getName(), gameData.getPlayers()[1].getName());
        this.gameInterface.setActivePlayer(gameData.getCurrentPlayer());
        this.gameInterface.changeActivePlayer();
        ((GameActivity) gameInterface).deleteFile("savedGame.txt");
        initDiceNumbers();
    }
    private void initDiceNumbers() {
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(4);
        diceNumber[0] = temp;

        temp = new ArrayList<>();
        temp.add(2); temp.add(6);
        diceNumber[1] = temp;

        temp = new ArrayList<>();
        temp.add(4); temp.add(2); temp.add(6);
        diceNumber[2] = temp;

        temp = new ArrayList<>();
        temp.add(0); temp.add(2); temp.add(6); temp.add(8);
        diceNumber[3] = temp;

        temp = new ArrayList<>();
        temp.add(0); temp.add(4); temp.add(2); temp.add(6); temp.add(8);
        diceNumber[4] = temp;

        temp = new ArrayList<>();
        temp.add(0); temp.add(5); temp.add(2); temp.add(6); temp.add(8);temp.add(3);
        diceNumber[5] = temp;


    }

    public void startGame(){
        if(continuedGame)
            continuePlaying();
        else
            throwDices();
    }

    private void setTable() {
        int[] table = gameData.getTable();
        if (continuedGame) {
            for (int i = 0; i < 24; i++) {
                if (table[i] != 0)
                    imageData.setCheckers(i, Math.abs(table[i]), table[i] > 0 ? Color.DKGRAY : Color.WHITE);
            }
            int[] blot = gameData.getBlots();
            if (blot[0] > 0)
                imageData.addToBlot(blot[0], Color.WHITE);
            if (blot[1] > 0)
                imageData.addToBlot(blot[1], Color.DKGRAY);
        } else {

            table[0] = 2;
            table[5] = -5;
            table[7] = -3;
            table[11] = 5;
            table[12] = -5;
            table[16] = 3;
            table[18] = 5;
            table[23] = -2;

            imageData.setCheckers(0, 2, Color.DKGRAY);
            imageData.setCheckers(5, 5, Color.WHITE);
            imageData.setCheckers(7, 3, Color.WHITE);
            imageData.setCheckers(11, 5, Color.DKGRAY);
            imageData.setCheckers(12, 5, Color.WHITE);
            imageData.setCheckers(16, 3, Color.DKGRAY);
            imageData.setCheckers(18, 5, Color.DKGRAY);
            imageData.setCheckers(23, 2, Color.WHITE);
        }
        startGame();
    }

    private void continuePlaying() {
        gameInterface.setShakeEnable(false);
        int[] dices = gameData.getDices();
        gameInterface.setDices(dices[0] > 0? diceNumber[dices[0]-1]: null, dices[1]>0?diceNumber[dices[1]-1]: null);
        switch (gameData.gameState) {
            case GameData.CalculateMoves:
            case GameData.CalculateMovesSecond:
                calculateMoves();
                break;
            case GameData.endOfFirstMove:
            case GameData.endOfSecondMove:
                endOfMove();
                break;
            case GameData.outOfMoves:
                outOfMoves();
                break;
            case GameData.playingFirstTime:
            case GameData.playingSecongTime:
                play();
                break;
            case GameData.SelectPlayer:
            case GameData.ThrowDices:
                gameInterface.setShakeEnable(true);
                throwDices();
                break;
        }
    }

    public void sizeSet() {
        setTable();
    }

    private void determineOrder(int diceValue) {
        /*un jucator arunca zarurile
         * urmatorul arunca si el
         * se determina 'currentPlayer'
         * apoi apelam throwDices*/
        if (gameData.getDices()[0] == 0) {
            gameData.setDiceOne(diceValue);
            gameInterface.setDices(diceNumber[diceValue-1], null);
            gameInterface.changeActivePlayer();
            gameData.changeCurrentPlayer();
            throwDices();
        } else {
            gameData.setDiceTwo(diceValue);
            gameInterface.setDices(null, diceNumber[diceValue-1]);
            int[] dices = gameData.getDices();
            if (dices[0] > dices[1]) {
                gameData.setCurrentPlayer(0);

            } else
                gameData.setCurrentPlayer(1);
            gameInterface.setActivePlayer(gameData.getCurrentPlayer());
            gameInterface.changeActivePlayer();
            gameData.gameState = GameData.ThrowDices;
            throwDices();
        }
    }

    private void throwDices() {
        /*trazi od igraca da baci kocke
         * (za sad radi random)*/
        if(gameData.gameState == GameData.SelectPlayer)
            gameInterface.refresh("Shake the device to determine playing order", false);
        else
            gameInterface.refresh("Shake the device to throw the dices", true);
        gameData.getPlayers()[gameData.getCurrentPlayer()].rollDices(this);
    }

    public void dicesThrown() {
        gameInterface.setShakeEnable(false);
        if (gameData.gameState == GameData.SelectPlayer)
            determineOrder((int) (Math.random() * 5) + 1);
        else {
            /*poziva se kada korisnik baci kocke
             * odavde se postavi modelu sta treba i
             * prelazi na calculateMoves*/
            int diceOne = (int) (Math.random() * 5) + 1;
            int diceTwo = (int) (Math.random() * 5) + 1;
            gameInterface.setDices(diceNumber[diceOne-1], diceNumber[diceTwo-1]);
            if(diceOne == diceTwo)
                gameData.setDoubleDices(diceOne);
            gameData.setDices(diceOne, diceTwo);
            gameData.gameState = GameData.CalculateMoves;
            calculateMoves();
        }
    }

    private void calculateMoves() {
        /*trazi od trenutnog stanja da ti
         * izracuna poteze, pa renderuj sliku opet
         * i predji na sledece stanje play
         * ako nema poteza, predji na out of moves*/
        int calcState = gameData.getPlayers()[gameData.getCurrentPlayer()].getState();
        gameData.getCalculationState(calcState).calculateMoves(gameData);
        if (gameData.getPossibleMoves().isEmpty()) {
            gameData.gameState = GameData.outOfMoves;
            outOfMoves();
        } else {
            if (gameData.gameState == GameData.CalculateMoves)
                gameData.gameState = GameData.playingFirstTime;
            else
                gameData.gameState = GameData.playingSecongTime;
            play();
        }
    }

    private void play() {
        /*kazi igracu da igra*/
        if (gameData.getPossibleMoves().size() == 1 && gameData.getPossibleMoves().get(-1) != null)
            imageData.highlightFromBlot(-1, gameData.getPlayers()[gameData.getCurrentPlayer()].getCheckers());
        else
            imageData.highlightCheckers(gameData.getPossibleMoves().keySet());
        gameInterface.refresh("Make your move. You can move highlighted checkers.", true);
        gameData.getPlayers()[gameData.getCurrentPlayer()].play(this);
    }

    public void endOfMove() {
        /*igrac zavrsio potez. proveri ako je prvi potez,
         * da li ima jos poteza. Ako je drugi potez, da li opet
         * baca kocke. Ako nista, predji na sledeceg i opet sve
         * */
        gameInterface.setClickEnable(false);
        int newPos = gameData.getNewRow();
        int old = gameData.getStartingRow();
        gameData.setStartingRow(-2);
        if (newPos < -1) {
            if (old == -1)
                imageData.highlightFromBlot(-1, gameData.getPlayers()[gameData.getCurrentPlayer()].getCheckers());
            else
                imageData.highlightCheckers(gameData.getPossibleMoves().keySet());
            gameInterface.refresh("Make your move. You can move highlighted checkers.", true);
            gameData.gameState = gameData.gameState == GameData.endOfFirstMove ? GameData.playingFirstTime : GameData.playingSecongTime;
            play();
        } else {
            Player currPlayer = gameData.getPlayers()[gameData.getCurrentPlayer()];
            if (currPlayer.getState() == CalculationState.BearingOffState
                    && gameData.countInHomeBoard(currPlayer.getCheckers()) == 0) {
                gameData.gameState = GameData.finished;
                finishGame();
                return;
            }
            if (gameData.gameState == GameData.endOfSecondMove) {
                switchPlayers();
                return;
            }
            int move = Math.abs(newPos - old);
            if (old == -1 && gameData.getCurrentPlayer() == 0)
                move = 24 - newPos;
            if (newPos == 24 && gameData.getCurrentPlayer() == 0)
                move = old + 1;
            int[] dices = gameData.getDices();
            boolean oneMove;
            if (oneMove = dices[0] == move)
                dices[0] = -1;
            else if (oneMove = dices[1] == move)
                dices[1] = -1;
            else if (dices[0] + dices[1] == move)
                dices[0] = dices[1] = -1;
            if (dices[0] > 0 && dices[1] > 0 && currPlayer.getState() == CalculationState.BearingOffState && newPos == 24) {
                oneMove = true;
                dices[0] = -1;
            }
            if (oneMove && (dices[0] > -1 || dices[1] > -1)) {
                gameData.gameState = GameData.CalculateMovesSecond;
                calculateMoves();
            } else
                switchPlayers();


        }
    }

    private void switchPlayers() {
        if(gameData.areDoubleDices()){
            gameData.gameState = GameData.CalculateMoves;
            gameData.setDoubleDicesForPlaying();
            calculateMoves();

        }else {
            gameData.gameState = GameData.ThrowDices;
            gameData.changeCurrentPlayer();
            gameInterface.changeActivePlayer();
            throwDices();
        }
    }

    private void finishGame() {
       /* EndGameDialog dialog = new EndGameDialog();
        Bundle arg = new Bundle();
        arg.putString("winner", gameData.getPlayers()[gameData.getCurrentPlayer()].getName());
        dialog.setArguments(arg);
        dialog.show(((Activity) gameInterface).getFragmentManager(), "endGame");*/
        ((GameActivity) gameInterface).deleteFile("savedGame.txt");
        ArrayList<String> names = new ArrayList<>();
        names.add(gameData.getPlayers()[0].getName());
        names.add(gameData.getPlayers()[1].getName());
        Collections.sort(names);
        dbModel.saveData(names.get(0), names.get(1), gameData.getPlayers()[gameData.getCurrentPlayer()].getName());
        gameInterface.finishGame(names.get(0), names.get(1), gameData.getPlayers()[gameData.getCurrentPlayer()].getName());

    }

    private void outOfMoves() {
        Toast.makeText((Context) gameInterface, "No more moves.", Toast.LENGTH_LONG).show();
        gameData.gameState = GameData.ThrowDices;
        gameData.changeCurrentPlayer();
        throwDices();
    }

    public void setClickEnable() {
        gameInterface.setClickEnable(true);
    }

    public void setShakeEnable() {
        gameInterface.setShakeEnable(true);
    }

    public void fingerDown(PointF position) {
        int id = imageData.highlightedClicked(position);
        gameData.setStartingRow(id);
        if (id > -2) {
            ArrayList<Integer> possibleFields = gameData.getPossibleMoves().get(id);
            imageData.highlightTriangles(possibleFields);
            imageData.resetColorChecker();
            gameInterface.refresh("Make your move. You can drop on highlighted triangles.", true);
        }
    }

    public void fingerMove(PointF position) {
        if (gameData.getStartingRow() > -2)
            imageData.moveChecker(position);
        gameInterface.refresh("Make your move. You can drop on highlighted triangles.", true);
    }

    public void fingerUp(PointF position) {
        Player currPlayer = gameData.getPlayers()[gameData.getCurrentPlayer()];
        if (gameData.getStartingRow() > -2) {
            int r = imageData.dropOnTriangle(position, gameData.getPossibleMoves().get(gameData.getStartingRow()));
            if (r >= 0) {
                moveChecker(gameData.getStartingRow(), r);
                imageData.putSelectedCheckerOnBoard(r);

            } else if (currPlayer.getState() == CalculationState.BearingOffState && imageData.droppedOnHomeBar(position)) {
                imageData.removeSelectedChecker();
                r = currPlayer.getCheckers() == 1 ? 24 : -1;
                moveChecker(gameData.getStartingRow(), r);
            } else {
                imageData.putSelectedCheckerOnBoard(gameData.getStartingRow());
                //imageData.highlightCheckers(gameData.getPossibleMoves().keySet());
            }
            imageData.resetColorTriangles(gameData.getPossibleMoves().get(gameData.getStartingRow()));
            if (gameData.gameState == GameData.playingFirstTime)
                gameData.gameState = GameData.endOfFirstMove;
            else
                gameData.gameState = GameData.endOfSecondMove;
            gameData.setNewRow(r);
            endOfMove();

        }
    }


    public LinkedHashMap<Integer, ArrayList<Integer>> getPossibleMoves() {
        return gameData.getPossibleMoves();
    }

    public void moveChecker(int oldPosition, int newPosition) {
        Player currentPlayer = gameData.getPlayers()[gameData.getCurrentPlayer()];
        int[] table = gameData.getTable();
        if (newPosition < 24 && newPosition > -1)
            table[newPosition] += currentPlayer.getCheckers();
        if (oldPosition > -1) {
            table[oldPosition] -= currentPlayer.getCheckers();
            int num = gameData.countOutsideHomeBoard(currentPlayer.getCheckers());
            if (num == 0)
                currentPlayer.setState(CalculationState.BearingOffState);
        } else {
            gameData.getBlots()[gameData.getCurrentPlayer()]--;
            if (!imageData.hasMoreOnBlot(currentPlayer.getCheckers()))
                currentPlayer.setState(CalculationState.RegularState);
        }
        if (newPosition < 24 && newPosition > -1 && table[newPosition] == 0) {
            gameData.getBlots()[((gameData.getCurrentPlayer() + 1) % 2)]++;
            table[newPosition] = currentPlayer.getCheckers();
            imageData.moveToBlot(newPosition);
            gameData.getPlayers()[(gameData.getCurrentPlayer() + 1) % 2].setState(CalculationState.BlotState);
        }
    }

    public void moveGUIChecker(int oldPosition, int newPosition) {
        int checkerColor = gameData.getCurrentPlayer() == 0 ? Color.WHITE : Color.DKGRAY;
        imageData.resetColorChecker();
        if (oldPosition == -1)
            imageData.removeFromBlot(checkerColor);
        else
            imageData.removeChecker(oldPosition);
        moveChecker(oldPosition, newPosition);
        if (newPosition < 24 && newPosition > -1)
            imageData.setCheckers(newPosition, 1, checkerColor);
        if (gameData.gameState == GameData.playingFirstTime)
            gameData.gameState = GameData.endOfFirstMove;
        else
            gameData.gameState = GameData.endOfSecondMove;
    }


    public void setStartingRow(int startingRow) {
        gameData.setStartingRow(startingRow);
    }

    public void setNewRow(Integer newRow) {
        gameData.setNewRow(newRow);
    }

    public void saveGame() {
        if (gameData.gameState != GameData.finished) {
            try (FileOutputStream fos = ((GameActivity) gameInterface).openFileOutput("savedGame.txt", Context.MODE_PRIVATE)) {
                gameData.saveGame(fos);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}

}
