package org.apocgaming.endreset.game;

import org.apocgaming.endreset.config.Config;
import org.apocgaming.endreset.world.WorldManager;

/**
 * Created by thomas15v on 11/03/15.
 */
public class GameHandler {

    private Game runningGame;
    private Config config;
    private WorldManager worldManager;

    public GameHandler(Config config, WorldManager worldManager){
        this.config = config;
        this.worldManager = worldManager;
        newGame();
    }

    public Game newGame(){
        return runningGame = new Game(new ExperienceManager(config.getTotalExp(), config.isRewardEgg()));
    }

    public boolean isGameRunning() {
        return runningGame.isGameRunning();
    }

    public Game getRunningGame() {
        return runningGame;
    }

    public void stopGame() {
        if (isGameRunning()) {
            getRunningGame().stopGame();
            worldManager.getGameWorld().reset();
            worldManager.getGameWorld().lock(config.getResetDelay());
            newGame();
        }
    }
}
