package com.example.myapplication.players;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.example.myapplication.R;
import com.example.myapplication.gameLogic.CalculationState;
import com.example.myapplication.gameLogic.GameLogic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CompPlayer extends Player {
    public CompPlayer(int checkers, String name) {
        super(checkers, name);
    }

    @Override
    public void play(GameLogic gameLogic) {

        new Movement(gameLogic).execute();
    }

    @Override
    public void rollDices(final GameLogic gameLogic) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    MediaPlayer mediaPlayer = MediaPlayer.create(gameLogic.getContext(), R.raw.dices);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    Thread.sleep(1000);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                gameLogic.dicesThrown();
            }
        }.execute();
    }

    private class Movement extends AsyncTask<Void, int[], Integer> {
        private GameLogic gameLogic;

        public Movement(GameLogic gameLogic) {
            this.gameLogic = gameLogic;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LinkedHashMap<Integer, ArrayList<Integer>> moves = gameLogic.getPossibleMoves();
            ArrayList<Integer> possibleMoves = null;
            int oldPosition = -1;
            int newPosition = 0;
            if (state == CalculationState.BearingOffState) {
                for(Map.Entry<Integer, ArrayList<Integer>> entry: moves.entrySet()){
                    if(entry.getValue().contains(24)){
                        oldPosition = entry.getKey();
                        break;
                    }
                }
                newPosition = possibleMoves.size() - 1;
            }
            if(oldPosition == -1) {
                while ((possibleMoves = moves.get(oldPosition)) == null)
                    oldPosition = (int) (Math.random() * 24);
                newPosition = (int) (Math.random() * possibleMoves.size());
            }

            if (oldPosition == -1)
                newPosition = 0;
            int res[] = new int[2];
            res[0] = oldPosition;
            res[1] = possibleMoves.get(newPosition);
            publishProgress(res);
            gameLogic.setStartingRow(oldPosition);
            return possibleMoves.get(newPosition);
        }


        @Override
        protected void onProgressUpdate(int[]... positions) {
            gameLogic.moveGUIChecker(positions[0][0], positions[0][1]);
        }

        @Override
        protected void onPostExecute(Integer newPosition) {
            gameLogic.setNewRow(newPosition);
            gameLogic.endOfMove();
        }
    }
}