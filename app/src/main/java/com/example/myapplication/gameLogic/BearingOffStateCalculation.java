package com.example.myapplication.gameLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class BearingOffStateCalculation implements CalculationState {
    @Override
    public void calculateMoves(GameData gameData) {
        LinkedHashMap<Integer, ArrayList<Integer>> result = new LinkedHashMap<>();
        int[] dices = gameData.getDices();
        int[] board = gameData.getTable();
        int currentPlayer = gameData.getPlayers()[gameData.getCurrentPlayer()].getCheckers();
        if (currentPlayer > 0) { //black
            for (int i = 18; i < 24; i++)
                if (board[i] > 0) {
                    HashSet<Integer> temp = new HashSet<>();
                    if (dices[0] > 0 && i + dices[0] < 24 && board[i + dices[0]] >= -1)
                        temp.add(i + dices[0]);
                    if (dices[1] > 0 && i + dices[1] < 24 && board[i + dices[1]] >= -1)
                        temp.add(i + dices[1]);
                    if (dices[0] > 0 && dices[1] > 0 && i + dices[1] + dices[0] < 24 && board[i + dices[0] + dices[1]] >= -1)
                        temp.add(i + dices[0] + dices[1]);
                    if (!temp.isEmpty())
                        result.put(i, new ArrayList<Integer>(temp));
                }
            if (dices[0] > 0 && board[24 - dices[0]] > 0) {
                if (result.get(24 - dices[0]) == null)
                    result.put(24 - dices[0], new ArrayList<Integer>());
                result.get(24 - dices[0]).add(24);
            }
            if (dices[1] > 0 && board[24 - dices[1]] > 0) {
                if (result.get(24 - dices[1]) == null)
                    result.put(24 - dices[1], new ArrayList<Integer>());
                result.get(24 - dices[1]).add(24);
            }

            if(result.isEmpty()){
                int i = 18;
                while(i < 24 && board[i] <= 0)
                    i++;
                result.put(i, new ArrayList<Integer>());
                result.get(i).add(24);
            }
        } else { //white
            for (int i = 0; i < 6; i++)
                if (board[i] < 0) {
                    HashSet<Integer> temp = new HashSet<>();
                    if (dices[0] > 0 && i - dices[0] > -1 && board[i - dices[0]] <= 1)
                        temp.add(i - dices[0]);
                    if (dices[1] > 0 && i - dices[1] > -1 && board[i - dices[1]] <= 1)
                        temp.add(i - dices[1]);
                    if (dices[0] > 0 && dices[1] > 0 && i - dices[1] - dices[0] > -1 && board[i - dices[0] - dices[1]] <= 1)
                        temp.add(i - dices[0] - dices[1]);
                    if (!temp.isEmpty())
                        result.put(i, new ArrayList<Integer>(temp));
                }
            if (dices[0] > 0 && board[dices[0] - 1] < 0) {
                if (result.get(dices[0] - 1) == null)
                    result.put(dices[0] - 1, new ArrayList<Integer>());
                result.get(dices[0] - 1).add(24);
            }
            if (dices[1] > 0 && board[dices[1]-1] < 0) {
                if (result.get(dices[1] - 1) == null)
                    result.put(dices[1] - 1, new ArrayList<Integer>());
                result.get(dices[1] - 1).add(24);
            }
            if(result.isEmpty()){
                int i = 5;
                while(i > -1 && board[i] >= 0)
                    i--;
                result.put(i, new ArrayList<Integer>());
                result.get(i).add(24);
            }
        }

        gameData.setPossibleMoves(result);

    }
}