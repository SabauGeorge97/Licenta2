package com.example.myapplication.gameLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RegularStateCalculation implements CalculationState {
    @Override
    public void calculateMoves(GameData gameData) {
        LinkedHashMap<Integer, ArrayList<Integer>> result = new LinkedHashMap<>();
        int[] dices = gameData.getDices();
        int[] board = gameData.getTable();
        int currentPlayer = gameData.getPlayers()[gameData.getCurrentPlayer()].getCheckers();
        if (currentPlayer > 0) {
            for (int i = 0; i < 24; i++)
                if (board[i] > 0) {
                    ArrayList<Integer> temp = new ArrayList<>();
                    if (dices[0] > 0 && i + dices[0] < 24 && board[i + dices[0]] >= -1)
                        temp.add(i + dices[0]);
                    if (dices[1] > 0 && i + dices[1] < 24 && board[i + dices[1]] >= -1)
                        temp.add(i + dices[1]);
                    if (dices[0] > 0 && dices[1] > 0 && i + dices[1] + dices[0] < 24 && board[i + dices[0] + dices[1]] >= -1)
                        temp.add(i + dices[0] + dices[1]);
                    if (!temp.isEmpty())
                        result.put(i, temp);
                }
        } else {
            for (int i = 0; i < 24; i++)
                if (board[i] < 0) {
                    ArrayList<Integer> temp = new ArrayList<>();
                    if (dices[0] > 0 && i - dices[0] > -1 && board[i - dices[0]] <= 1)
                        temp.add(i - dices[0]);
                    if (dices[1] > 0 && i - dices[1] > -1 && board[i - dices[1]] <= 1)
                        temp.add(i - dices[1]);
                    if (dices[0] > 0 && dices[1] > 0 && i - dices[1] - dices[0] > -1 && board[i - dices[0] - dices[1]] <= 1)
                        temp.add(i - dices[0] - dices[1]);
                    if (!temp.isEmpty())
                        result.put(i, temp);
                }
        }
        gameData.setPossibleMoves(result);
    }
}