package org.apocgaming.endreset;

import java.util.LinkedHashMap;

import org.bukkit.entity.Player;

/**
 * Created by Haze on 12/29/2014.
 */
public class ExpierenceDistributerManager extends HashMapManager<Player, Double> {

    public void setup(){
        this.contents = new LinkedHashMap<>();
    }
}
