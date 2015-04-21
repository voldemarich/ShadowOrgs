package com.voldemarich.ShadowOrgs;

import com.voldemarich.ShadowOrgs.ShadowOrgs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by voldemarich on 20.04.15.
 */
public class OrgsCommandExecutor implements CommandExecutor{

    private final ShadowOrgs plugin;


    public OrgsCommandExecutor(ShadowOrgs plugin) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.toString().equalsIgnoreCase("orgs")){
            try {
                if (ShadowOrgs.manager.resolve(sender, label, args)) {
                    return true;
                } else {
                    ActionBroadcaster.getInstance().tell(sender, "Failed to identify the command");

                }
            }
            catch (Exception e){

            }
        }
        return false;
    }
}
