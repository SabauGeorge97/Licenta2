package com.example.myapplication.gameLogic;

public interface CalculationState {
    public static final int RegularState = 0;
    public static final int BlotState = 1;
    public static final int BearingOffState = 2;

    public void calculateMoves(GameData gameData);
}