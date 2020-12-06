package com.lielamar.deathswap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathSwap extends JavaPlugin implements CommandExecutor {

    private boolean enabled;

    @Override
    public void onEnable() {
        this.enabled = false;

        getCommand("deathswap").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
        if(!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "You must be a player to do that!");
            return false;
        }

        Player player = (Player) cs;
        if(!player.hasPermission("deathswap.command")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions to do that!");
            return false;
        }

        if(cmd.getName().equalsIgnoreCase("deathswap")) {
            if(args.length == 0 && !enabled) {
                player.sendMessage(ChatColor.RED + "Usage: /DeathSwap <Minutes>");
                return false;
            } else if(args.length == 0) {
                enabled = false;
                return true;
            }

            int minutes;

            try {
                minutes = Integer.parseInt(args[0]);
                if(!enabled) {
                    enabled = true;
                    startTimer(minutes);
                    player.sendMessage(ChatColor.GREEN + "Started deathswap!");
                } else {
                    enabled = false;
                    player.sendMessage(ChatColor.RED + "Stopped deathswap!");
                }
            } catch(Exception e) {
                player.sendMessage(ChatColor.RED + "Minutes must be an integer!");
                return false;
            }
        }
        return false;
    }

    public void startTimer(int minutes) {
        new BukkitRunnable() {

            int count = minutes*60;

            @Override
            public void run() {
                if(!enabled)
                    this.cancel();

                if(count <= 10) {
                    Bukkit.broadcastMessage(ChatColor.RED + "" + count + " seconds to swap!");
                }

                if(count == 0) {
                    count = minutes*60;
                    Player tmp = null;
                    Location firstLoc = null;

                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(tmp != null) {
                            if(firstLoc == null) {
                                firstLoc = tmp.getLocation();
                            }
                            tmp.teleport(player);
                        }
                        tmp = player;
                    }

                    if(firstLoc != null) {
                        tmp.teleport(firstLoc);
                    }
                }

                count--;
            }
        }.runTaskTimer(this, 0L, 20L);
    }
}
