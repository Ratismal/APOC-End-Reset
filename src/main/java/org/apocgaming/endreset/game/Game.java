package org.apocgaming.endreset.game;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas15v on 11/03/15.
 */
public class Game {

    private List<String> playingplayers;
    private boolean running;
    private ExperienceManager experienceManager;
    private boolean gameRunning;

    public Game(ExperienceManager experienceManager){
        playingplayers = new ArrayList<>();
        this.experienceManager = experienceManager;
        this.gameRunning = true;
    }

    public void addPlayer(Player player) {
        playingplayers.add(player.getName());
    }

    public void removePlayer(Player player) {
        playingplayers.remove(player.getName());
    }

    public ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    public void stopGame() {
        running = false;
        getExperienceManager().rewardPlayers();
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean hasPlayer(Player player) {
        return playingplayers.contains(player.getName());
    }
}
