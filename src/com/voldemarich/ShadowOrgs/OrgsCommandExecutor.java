package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;
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

    private final OrgsManager manager;
    private enum senders {CONSOLE, PLAYER, ANY}

    public OrgsCommandExecutor(OrgsManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        try {
            if (args.length == 0 || args[0] == null) return onCommandEmpty(sender);
            if (args[0].equals("create") && args.length >= 2 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandOrganization(sender, args, 1);
            if (args[0].equals("remove") && args.length >= 2 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandOrganization(sender, args, -1);
            if (args[0].equals("amember") && args.length >= 3 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandMember(sender, args, 1);
            if (args[0].equals("rmember") && args.length >= 3 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandMember(sender, args, -1);
            if (args[0].equals("changeright") && args.length >= 4 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandMember(sender, args, 0);
            if (args[0].equals("donate") && args.length >= 3 && isEligible(sender, senders.PLAYER, "shadoworgs.basic"))
                return onCommandMoney(sender, args, 1);
            if (args[0].equals("withdraw") && args.length >= 3 && isEligible(sender, senders.PLAYER, "shadoworgs.basic"))
                return onCommandMoney(sender, args, -1);
            return args[0].equals("setmoney") && args.length >= 3 && isEligible(sender, senders.ANY, "shadoworgs.admin") && onCommandMoney(sender, args, 0);
        }
        catch (OrganizationException e) {
            sender.sendMessage(e.getMessage());
            return false;
        }
    }

    private boolean isEligible(CommandSender sender, senders sendertype, String globalright) {
        return sender.hasPermission(globalright) && ((sendertype == senders.PLAYER && sender instanceof Player)||sendertype == senders.ANY);
    }


    private boolean onCommandEmpty(CommandSender sender){
        sender.sendMessage("Wrong input, man");
        return false;
    }

    private boolean onCommandOrganization(CommandSender sender, String[] args, int action){
            if (action == 1) {
                if (sender instanceof Player) {
                    manager.createOrganization(args[1], (Player) sender);
                }
                if(sender instanceof ConsoleCommandSender){
                    manager.createOrganization(args[1], Bukkit.getPlayer(args[2]));
                }
                return true;
            }
            if (action == -1) {
                if (manager.hasOrgPermission(sender, args[1], 2)) {
                    manager.removeOrganization(args[1]);
                }
                return true;
            }
            return false;

    }

    private boolean onCommandMember(CommandSender sender, String[] args, int action) {
        if (action == 1) {
            if (manager.hasOrgPermission(sender, args[1], 1) || (args[3] != null && manager.hasOrgPermission(sender, args[1], 2))) {
                if (args[3] != null) manager.addMember(args[1], Bukkit.getPlayer(args[2]), Integer.parseInt(args[3]));
                else manager.addMember(args[1], Bukkit.getPlayer(args[2]), 0);
                return true;
            }
        }
        if (action == 0) {
            if (manager.hasOrgPermission(sender, args[1], 2)) {
                manager.changeMemberRight(args[1], Bukkit.getPlayer(args[2]), Integer.parseInt(args[3]));
                return true;
            }
            return true;
        }
        if (action == -1) {
            if (manager.hasOrgPermission(sender, args[1], 1) && !manager.hasOrgPermission(Bukkit.getPlayer(args[2]), args[1], 1)) {
                manager.removeMember(args[1], Bukkit.getPlayer(args[2]));
                return true;
            }
            return true;
        }
        return false;
        }

    private boolean onCommandMoney(CommandSender sender, String[] args, int action){
            if (action == 1) {
                manager.getOrgByName(args[1]).depositFunds((Player)sender, Double.parseDouble(args[2]));
                return true;
            }
            if (action == 0) {
                manager.getOrgByName(args[1]).withdrawFunds((Player) sender, Double.parseDouble(args[2]));
                return true;
            }
            if (action == -1) {
                manager.getOrgByName(args[1]).setFunds(Double.parseDouble(args[2]));
                return true;
            }
            return false;
        }


}