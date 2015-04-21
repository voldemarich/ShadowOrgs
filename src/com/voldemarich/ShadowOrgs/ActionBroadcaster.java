package com.voldemarich.ShadowOrgs;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

/**
 * Created by voldemarich on 21.04.15.
 */
public class ActionBroadcaster {
    private static ActionBroadcaster instance;
    private Logger logger;
    public static ActionBroadcaster getInstance(){
        if (instance == null) instance = new ActionBroadcaster();
        return instance;
    }
    private ActionBroadcaster(){
        logger = Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs").getLogger();
    }

    public void tell(CommandSender player, String message){
        player.sendMessage(message);
    }

    public void log(String message){
        logger.info(message);
    }

    public void yell(String message){
        logger.severe(message);
    }
}