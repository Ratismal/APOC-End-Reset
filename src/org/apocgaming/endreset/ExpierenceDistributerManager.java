package org.apocgaming.endreset;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Haze on 12/29/2014.
 */
public class ExpierenceDistributerManager extends HashMapManager<Player, Double> {

    public void setup(){
        this.contents = new LinkedHashMap<>();
    }


}
