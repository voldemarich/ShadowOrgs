package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    private enum senders {OfflinePlayer, ANY};


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
            if (args[0].equals("donate") && args.length >= 3 && isEligible(sender, senders.OfflinePlayer, "shadoworgs.basic"))
                return onCommandMoney(sender, args, 1);
            if (args[0].equals("withdraw") && args.length >= 3 && isEligible(sender, senders.OfflinePlayer, "shadoworgs.basic"))
                return onCommandMoney(sender, args, -1);
            if (args[0].equals("setmoney") && args.length >= 3 && isEligible(sender, senders.ANY, "shadoworgs.admin"))
                return onCommandMoney(sender, args, 0);
            if (args[0].equals("info") && args.length >= 1 && isEligible(sender, senders.ANY, "shadoworgs.basic"))
                return onCommandInfo(sender, args);
            if (args[0].equals("synchronize") && isEligible(sender, senders.ANY, "shadoworgs.admin"))
                return onCommandSync();
            return false;
        }
        catch (OrganizationException e) {
            ActionBroadcaster.getInstance().tell(sender, e.getMessage());
            return true;
        }
    }

    private boolean isEligible(CommandSender sender, senders sendertype, String globalright) {
        return sender.hasPermission(globalright) && ((sendertype == senders.OfflinePlayer && sender instanceof OfflinePlayer)||sendertype == senders.ANY);
    }


    private boolean onCommandEmpty(CommandSender sender){
        ActionBroadcaster.getInstance().tell(sender, "§cNo input, man, read help:");
        return false;
    }

    private boolean onCommandOrganization(CommandSender sender, String[] args, int action){
            if (action == 1) {
                if (sender instanceof OfflinePlayer) {
                    manager.createOrganization(args[1], (OfflinePlayer) sender);
                    ActionBroadcaster.getInstance().tell(sender, "§aYou've just created organization named " + args[1]);
                }
                if(sender instanceof ConsoleCommandSender){
                    manager.createOrganization(args[1], Bukkit.getOfflinePlayer(args[2]));
                }
                return true;
            }
            if (action == -1) {
                if (manager.hasOrgPermission(sender, args[1], 2)) {
                    manager.removeOrganization(args[1]);
                    ActionBroadcaster.getInstance().tell(sender, "§eYou've just deleted organization named " + args[1]);
                }
                return true;
            }
            return false;

    }

    private boolean onCommandMember(CommandSender sender, String[] args, int action) throws OrganizationException{
        if (action == 1) {
            if (manager.hasOrgPermission(sender, args[1], 1) || (args.length>=4 && manager.hasOrgPermission(sender, args[1], 2))) {
                if (args.length >= 4) {
                    try {
                        int rt = Integer.parseInt(args[3]);
                        manager.addMember(args[1], Bukkit.getOfflinePlayer(args[2]), rt);
                        ActionBroadcaster.getInstance().tell(sender, "§aYou've just added a member §5" + args[2] + "§a to the organization §5"+args[1]+"§a with the right of §5"+manager.rightnames.inverse().get(rt));
                    } catch (Exception e) {
                        try {
                            int rt = manager.rightnames.get(args[3]);
                            manager.addMember(args[1], Bukkit.getOfflinePlayer(args[2]), rt);
                            ActionBroadcaster.getInstance().tell(sender, "§aYou've just added a member §5" + args[2] + "§a to the organization §5" + args[1] + "§a with the right of §5" + args[3]);


                        } catch (Exception k) {
                            throw new OrganizationException("Invalid right parameter!");
                        }
                    }
                    return true;
                } else{
                    manager.addMember(args[1], Bukkit.getOfflinePlayer(args[2]), 0);
                    ActionBroadcaster.getInstance().tell(sender, "§aYou've just added a member §5" + args[2] + "§a to the organization §5" + args[1] + "§a with the right of §5member");
                }
                return true;
            }
            else throw new OrganizationException("Permission denied! Insufficient rights!");
        }
        if (action == 0) {
            if (manager.hasOrgPermission(sender, args[1], 2)) {
                try {
                    int rt = Integer.parseInt(args[3]);
                    manager.changeMemberRight(args[1], Bukkit.getOfflinePlayer(args[2]), rt);
                    ActionBroadcaster.getInstance().tell(sender, "§eYou've just changed the rights of the member §5" + args[2] + "§e in the organization §5" + args[1] + "§e to §5" + manager.rightnames.inverse().get(rt));
                }
                catch (Exception e){
                    try {
                        int rt = manager.rightnames.get(args[3]);
                        manager.changeMemberRight(args[1], Bukkit.getOfflinePlayer(args[2]), rt);
                        ActionBroadcaster.getInstance().tell(sender, "§eYou've just changed the rights of the member §5" + args[2] + "§e in the organization §5" + args[1] + "§e to §5" + args[3]);


                    }
                    catch (Exception k){
                        throw new OrganizationException("Invalid right parameter!");
                    }
                }
                return true;
            }
            else throw new OrganizationException("Permission denied! Insufficient rights!");
        }
        if (action == -1) {
            if (manager.hasOrgPermission(sender, args[1], 2) || sender.hasPermission("shadoworgs.admin")){
                manager.removeMember(args[1], Bukkit.getOfflinePlayer(args[2]));
                ActionBroadcaster.getInstance().tell(sender, "§eYou've just deleted the member §5" + args[2] + "§e from the organization §5" + args[1]);
                return true;
            }
            else throw new OrganizationException("Permission denied! Insufficient rights!");
        }
        return false;
        }

    private boolean onCommandMoney(CommandSender sender, String[] args, int action){
            if (action == 1) {
                manager.getOrgByName(args[1]).depositFunds((OfflinePlayer)sender, Double.parseDouble(args[2]));
                return true;
            }
            if (action == 0) {
                manager.getOrgByName(args[1]).setFunds(Double.parseDouble(args[2]));
                return true;
            }
            if (action == -1) {
                manager.getOrgByName(args[1]).withdrawFunds((OfflinePlayer) sender, Double.parseDouble(args[2]));
                return true;
            }
            return false;
        }

    private boolean onCommandSync(){
        manager.saveAndSync();
        return true;
    }

    
    //TODO let the organization's admins define the permissions for viewing the info themselves
    private boolean onCommandInfo(CommandSender sender, String[] args) {
        StringBuilder sb = new StringBuilder();
        if(args.length > 1){
            Organization in = manager.getOrgByName(args[1]);
            sb.append("§eYou requested info on ");
            sb.append(in.string_id);
            sb.append("\n===================================================\n");
            sb.append("Your access level here is: §5");
            if (sender.hasPermission("shadoworgs.admin")) {
                sb.append(manager.rightnames.inverse().get(2));
            } else sb.append(manager.rightnames.inverse().get(in.getRight((OfflinePlayer) sender)));
            sb.append("\n");
            if (args.length == 2)
                sb.append("§eSpecify what you wanna know about this organization\nTry such keywords:\nmembers, rights, economy§f\n");
            else {
                if (args[2].equals("members") && manager.hasOrgPermission(sender, in.string_id, 0)) { //right here
                    sb.append("§eHere's the list of members in this organization:§b\n");
                    int cnt = 1;
                    for (Object o : in.members.keySet()) {
                        sb.append((String) o);
                        if (cnt % 5 == 0) sb.append("\n");
                        else sb.append(", ");
                        cnt++;
                    }

                }
                if (args[2].equals("rights") && manager.hasOrgPermission(sender, in.string_id, 1)) { //and here
                    int cnt = 1;
                    sb.append("§eHere's the list of members:rights in this organization:\n");
                    for (Object o : in.members.keySet()) {
                        sb.append("§b");
                        sb.append((String) o);
                        sb.append("§e:§5");
                        sb.append(manager.rightnames.inverse().get(in.members.get(o)));
                        if (cnt % 3 == 0) sb.append("\n");
                        else sb.append(", ");
                        cnt++;
                    }
                }
                if (args[2].equals("economy") && manager.hasOrgPermission(sender, in.string_id, 0)) { //and here
                    sb.append("§eAccount of the organization is named: §4");
                    sb.append(in.bank);
                    sb.append("§e\n");
                    sb.append("Available funds:§a ");
                    sb.append(ShadowOrgs.econ.bankBalance(in.bank).balance);
                    sb.append("§f\n");
                }
                if (!manager.hasOrgPermission(sender, in.string_id, 0)) sb.append("§cInsufficient rights§f");
            }
    }
        else {
            sb.append("§eYou requested general info\n");
            sb.append("=======================================\n");
            if (sender instanceof Player){
            sb.append("You are now member of such orgs as:\n");
            sb.append(manager.memberLookup((Player)sender));
            }
            else {
                sb.append("You are a console. Watch db on your own.\n");
            }
            sb.append("§eFor more specific info put the name of org after the command.");
        }
        ActionBroadcaster.getInstance().tell(sender, sb.toString());
        return true;
    }
}