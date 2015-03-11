package org.apocgaming.endreset.game;

import org.apocgaming.endreset.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomas15v on 11/03/15.
 */
public class ExperienceManager {

    private Map<String, Integer> data;
    private int totalrewardxp;
    private int totaldamage;
    private String mostdamagingplayer;
    private int mostdamage;
    private boolean rewardegg;

    public ExperienceManager(int totalrewardxp, boolean rewardegg){
        this.rewardegg = rewardegg;
        this.data = new HashMap<>();
        this.totalrewardxp = totalrewardxp;
    }

    public void givePlayer(Player player, int damage){
        if (data.containsKey(player.getName()))
            damage += data.get(player.getName());
        if (mostdamage < damage) {
            mostdamage = damage;
            mostdamagingplayer = player.getName();
        }
        data.put(player.getName(), damage);
        this.totaldamage = damage;
    }

    public void rewardPlayers(){
        for (String playername : data.keySet())
        {
            int damage = data.get(playername);
            int reward = (damage / totaldamage) * totalrewardxp;
            Player player = Bukkit.getPlayer(playername);
            if (reward == 0){
                MessageUtil.sendMessage(player, "You did no damage to the ender dragon enough to get an award.");
                return;
            }
            player.giveExp(reward);
            MessageUtil.sendMessage(player, "You have been rewarded " + reward + " exp points for doing " + (reward / totalrewardxp)*100
                    + "% of the damage.");
            if (playername.equals(mostdamagingplayer) && rewardegg)
                awardDragonEgg(player);
        }
    }

    private void awardDragonEgg(Player player){
        for (ItemStack itemStack: player.getInventory().getContents())
            if (itemStack == null) {
                player.getInventory().addItem(new ItemStack(Material.DRAGON_EGG));
                MessageUtil.sendMessage(player, "You did the most damage to the ender dragon. "
                        + "Therefore you have been rewarded the dragon egg!");
                return;
            }
        player.getWorld().dropItem(player.getLocation(), new ItemStack(Material.DRAGON_EGG));
        MessageUtil.sendMessage(player, "You did the most damage to the ender dragon. "
                + "Your inventory is full, the dragon egg dropped on the ground.");
    }
}
