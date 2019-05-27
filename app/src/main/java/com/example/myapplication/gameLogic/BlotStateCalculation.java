package com.example.myapplication.gameLogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class BlotStateCalculation implements CalculationState {
    @Override
    public void calculateMoves(GameData gameData) {
        LinkedHashMap<Integer, ArrayList<Integer>> result = new LinkedHashMap<>();
        int[] dices = gameData.getDices();
        int[] board = gameData.getTable();
        int currentPlayer =  gameData.getPlayers()[gameData.getCurrentPlayer()].getCheckers();
        HashSet<Integer> temp = new HashSet<>();
        if(currentPlayer < 0){ //white
            for(int i = 18; i < 24; i++){
                if (dices[0] > 0  && board[24 - dices[0]] <= 1)
                    temp.add(24 - dices[0]);
                if (dices[1] > 0 && board[24 - dices[1]] <= 1)
                    temp.add(24 - dices[1]);
                if (dices[0] > 0 && dices[1] > 0 && (dices[0] + dices[1] < 7) && board[24 - dices[0] - dices[1]] <= 1)
                    temp.add(24 - dices[0] - dices[1]);
            }
        }else{ //black
            for(int i = 0; i < 6; i++){
                if (dices[0] > 0 && board[dices[0]-1] >= -1)
                    temp.add(dices[0]-1);
                if (dices[1] > 0 && board[dices[1]-1] >= -1)
                    temp.add(dices[1]-1);
                if (dices[0] > 0 && dices[1] > 0 && (dices[1] + dices[0] -1 < 6) && board[dices[0] + dices[1] -1] >= -1)
                    temp.add(dices[0] + dices[1] - 1);
            }
        }
        if(!temp.isEmpty())
            result.put(-1, new ArrayList<Integer>(temp));
        gameData.setPossibleMoves(result);
    }
}