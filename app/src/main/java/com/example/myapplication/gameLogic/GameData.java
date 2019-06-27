package com.example.myapplication.gameLogic;

import com.example.myapplication.players.CompPlayer;
import com.example.myapplication.players.HumanPlayer;
import com.example.myapplication.players.Player;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameData implements Serializable {

    // starea jocului
    // starea in care se afla jocul
    public static final int SelectPlayer = 0;
    public static final int ThrowDices = 1;
    public static final int CalculateMoves = 2;
    public static final int CalculateMovesSecond = 3;
    public static final int playingFirstTime = 4;
    public static final int playingSecongTime = 5;
    public static final int endOfFirstMove = 6;
    public static final int endOfSecondMove = 7;
    public static final int outOfMoves = 8;
    public static final int finished = 9;

    // logica care calculeaza posibilele mutari
    private CalculationState[] calculationStates = new CalculationState[3];
    private int[] table = new int[24]; // mutarile pe care le putem avea
    private Player[] players = new Player[2]; // playerii construiti
    private int[] blots = new int[2];
    int gameState = SelectPlayer; //prima stare in care insta gamestate-ul este de selectare a playerului
    private int startingRow;
    private int newRow;
    private int[] doubleDices = null; //situatia de aruncare a dublei

    private int currentPlayer; // cine urmeaza la rand
    private int[] dices = new int[2]; // crearea de zaruri

    private LinkedHashMap<Integer, ArrayList<Integer>> possibleMoves; // din pozitia 3 , poti ajunge in pozitia 5,7,etc.


    public GameData(int compNum, String player1, String player2) {
        calculationStates[0] = new RegularStateCalculation(); // asezarea
        calculationStates[1] = new BlotStateCalculation(); // miscarea
        calculationStates[2] = new BearingOffStateCalculation(); // scoaterea

        //numarul de piese de pe fiecare pozitie
        for (int i = 0; i < 24; i++)
            table[i] = 0;


        players[0] = compNum == 0 ? new CompPlayer(-1, player1) : new HumanPlayer(-1, player1);
        players[1] = compNum == 1 ? new CompPlayer(1, player2) : new HumanPlayer(1, player2);

    }


    public GameData() {
        calculationStates[0] = new RegularStateCalculation();
        calculationStates[1] = new BlotStateCalculation();
        calculationStates[2] = new BearingOffStateCalculation();
    }


    public int[] getTable() {
        return table;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int[] getBlots() {
        return blots;
    }

    public void setBlots(int[] blots) {
        this.blots = blots;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int[] getDices() {
        return dices;
    }

    public void setDices(int diceOne, int diceTwo) {
        dices[0] = diceOne;
        dices[1] = diceTwo;
    }

    public LinkedHashMap<Integer, ArrayList<Integer>> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(LinkedHashMap<Integer, ArrayList<Integer>> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    // seteaza valorile zarurilor
    public void setDiceOne(int diceOne) {
        dices[0] = diceOne;
    }

    public void setDiceTwo(int diceTwo) {
        dices[1] = diceTwo;
    }

    public CalculationState getCalculationState(int calcState) {
        return calculationStates[calcState];
    }

    public void setStartingRow(int startingRow) {
        this.startingRow = startingRow;
    }

    public int getStartingRow() {
        return startingRow;
    }

    //schimbarea de jucator
    public void changeCurrentPlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
    }

    public int countOutsideHomeBoard(int checkers) {
        int count = 0;
        if (checkers == 1) {
            for (int i = 0; i < 18; i++)
                if (table[i] > 0)
                    count += table[i];
        } else {
            for (int i = 6; i < 24; i++)
                if (table[i] < 0)
                    count += table[i];
        }
        return count;
    }

    public int countInHomeBoard(int checkers) {
        int count = 0;
        if (checkers == 1) {
            for (int i = 18; i < 24; i++)
                if (table[i] > 0)
                    count += table[i];
        } else {
            for (int i = 0; i < 6; i++)
                if (table[i] < 0)
                    count += table[i];
        }
        return count;
    }

    public void setNewRow(int newRow) {
        this.newRow = newRow;
    }

    public int getNewRow() {
        return newRow;
    }

    // ia toate valorile din GameData si le pune pe memoria telefonului
    public void saveGame(FileOutputStream fos) throws IOException {

        fos.write((players[0].getName() + "\n").getBytes());
        fos.write((players[1].getName() + "\n").getBytes());
        int compNum = -1;
        if (players[0] instanceof CompPlayer)
            compNum = 0;
        else if (players[1] instanceof CompPlayer)
            compNum = 1;
        fos.write((compNum + "\n").getBytes());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            sb.append(table[i]);
            if (i == 23)
                sb.append("\n");
            else
                sb.append("%%");
        }
        fos.write(sb.toString().getBytes());
        sb = new StringBuilder();
        sb.append(gameState).append("%%");
        sb.append(currentPlayer).append("%%");
        sb.append(players[0].getState()).append("%%");
        sb.append(players[1].getState()).append("%%");
        sb.append(dices[0]).append("%%");
        sb.append(dices[1]).append("%%");
        sb.append(startingRow).append("%%");
        sb.append(newRow).append("%%");
        sb.append(blots[0]).append("%%");
        sb.append(blots[1]);
        if(doubleDices != null){
            sb.append("%%").append(doubleDices[0]);
            sb.append("%%").append(doubleDices[1]);
        }
        sb.append("\n");
        fos.write(sb.toString().getBytes());

        sb = new StringBuilder();
        if (possibleMoves != null && !possibleMoves.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<Integer>> entry : possibleMoves.entrySet()) {
                sb.append(entry.getKey());
                sb.append("%%");
                for (int i = 0; i < entry.getValue().size(); i++) {
                    sb.append(entry.getValue().get(i));
                    if (i == entry.getValue().size() - 1)
                        sb.append("\n");
                    else
                        sb.append("%%");
                }
            }
            fos.write(sb.toString().getBytes());
        }
    }

    // ia din fisierul salvat si interpreteaza textul si seteaza valorile din GameData
    public void loadGame(BufferedReader bufferedReader) throws IOException {


        String player1 = bufferedReader.readLine();
        String player2 = bufferedReader.readLine();

        int compNum = Integer.parseInt(bufferedReader.readLine());
        players[0] = compNum == 0 ? new CompPlayer(-1, player1) : new HumanPlayer(-1, player1);
        players[1] = compNum == 1 ? new CompPlayer(1, player2) : new HumanPlayer(1, player2);


        String[] intString = bufferedReader.readLine().split("%%");
        for (int i = 0; i < intString.length; i++)
            table[i] = Integer.parseInt(intString[i]);

        intString = bufferedReader.readLine().split("%%");

        gameState = Integer.parseInt(intString[0]);
        currentPlayer = Integer.parseInt(intString[1]);

        players[0].setState(Integer.parseInt(intString[2]));
        players[1].setState(Integer.parseInt(intString[3]));

        dices[0] = Integer.parseInt(intString[4]);
        dices[1] = Integer.parseInt(intString[5]);

        startingRow = Integer.parseInt(intString[6]);
        newRow = Integer.parseInt(intString[7]);

        blots[0] = Integer.parseInt(intString[8]);
        blots[1] = Integer.parseInt(intString[9]);
        if(intString.length > 10){
            doubleDices = new int[2];
            doubleDices[0] = Integer.parseInt(intString[10]);
            doubleDices[1] = Integer.parseInt(intString[11]);
        }
        String line;
        possibleMoves = new LinkedHashMap<>();
        while ((line = bufferedReader.readLine()) != null) {
            intString = line.split("%%");
            int key = Integer.parseInt(intString[0]);
            ArrayList<Integer> val = new ArrayList<>();
            for (int i = 1; i < intString.length; i++)
                val.add(Integer.parseInt(intString[i]));
            possibleMoves.put(key, val);
        }
    }


    public void setDoubleDices(int b) {
        doubleDices = new int[2];
        doubleDices[0] = doubleDices[1] = b;
    }

    // zaruri duble
    public void setDoubleDicesForPlaying(){
        dices = doubleDices;
        doubleDices = null;
    }
    public boolean areDoubleDices() {
        return doubleDices != null;
    }
}