package com.voldemarich.ShadowOrgs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * Created by voldemarich on 20.04.15.
 * Command execution grabbed to separate file
 */
public class OrgsCommandExecutor implements CommandExecutor{

    private final ShadowOrgs plugin;
    private final OrgsManager manager;
    private enum senders {CONSOLE, PLAYER, ANY}

    public OrgsCommandExecutor(ShadowOrgs plugin, OrgsManager manager) {
        this.plugin = plugin; // Store the plugin in situations where you need it.
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0 || label == null) return onCommandEmpty(sender);
        if(label.equals("create") && args.length >=1 && isEligible(sender, senders.ANY, "shadoworgs.basic")) return onCommandOrganization(sender, args, 1);
        if(label.equals("remove") && args.length >=1 && isEligible(sender, senders.ANY, "shadoworgs.basic")) return onCommandOrganization(sender, args, -1);
        if(label.equals("amember") && args.length >=2 && isEligible(sender, senders.ANY, "shadoworgs.basic")) return onCommandMember(sender, args, 1);
        if(label.equals("rmember") && args.length >=2 && isEligible(sender, senders.ANY, "shadoworgs.basic")) return onCommandMember(sender, args, -1);
        if(label.equals("changeright") && args.length >=3 && isEligible(sender, senders.ANY, "shadoworgs.basic")) return onCommandMember(sender, args, 0);
        if(label.equals("donate") && args.length >=2 && isEligible(sender, senders.PLAYER, "shadoworgs.basic")) return onCommandMoney(sender, args, 1);
        if(label.equals("withdraw") && args.length >=2 && isEligible(sender, senders.PLAYER, "shadoworgs.basic")) return onCommandMoney(sender, args, -1);
        if(label.equals("setmoney") && args.length >=2 && isEligible(sender, senders.ANY, "shadoworgs.admin")) return onCommandMoney(sender, args, 0);
        return false;
    }

    private boolean isEligible(CommandSender sender, senders sendertype, String globalright) {
        return (sendertype == senders.CONSOLE || sendertype == senders.ANY) && sender instanceof ConsoleCommandSender || (sendertype == senders.PLAYER || sendertype == senders.ANY) && sender instanceof Player && sender.hasPermission(globalright);
    }

    private boolean hasOrgPermission(Player player, String orgname, int permission){
        return manager.getOrgByName(orgname).getRight(player) >= permission;
    }

    private boolean onCommandEmpty(CommandSender sender){
        sender.sendMessage("Wrong input, man");
        return false;
    }

    private boolean onCommandOrganization(CommandSender sender, String[] args, int action){
        if(action == 1){

            return true;
        }
        if(action == 0){

            return true;
        }
        if(action == -1){

            return true;
        }
        return false;
    }

    private boolean onCommandMember(CommandSender sender, String[] args, int action){
        if(action == 1){

            return true;
        }
        if(action == 0){

            return true;
        }
        if(action == -1){

            return true;
        }
        return false;
    }

    private boolean onCommandMoney(CommandSender sender, String[] args, int action){
        if(action == 1){

            return true;
        }
        if(action == 0){

            return true;
        }
        if(action == -1){

            return true;
        }
        return false;
    }


}
