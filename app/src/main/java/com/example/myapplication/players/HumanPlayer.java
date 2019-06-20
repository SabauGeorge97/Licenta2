package com.example.myapplication.players;

import com.example.myapplication.gameLogic.GameLogic;

public class HumanPlayer extends Player {
    public HumanPlayer(int checkers, String name) {
        super(checkers, name);
    }

    @Override
    public void play(GameLogic gameLogic) {
        gameLogic.setClickEnable();
    }

    @Override
    public void rollDices(GameLogic gameLogic) {

    }
}