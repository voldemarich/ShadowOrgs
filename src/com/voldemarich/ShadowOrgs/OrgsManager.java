package com.voldemarich.ShadowOrgs;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by voldemarich on 21.04.15.
 */

class ResolvableCommand{
    public String command;
    public int securitylevel;
    public int arguments;
    public boolean setargs;

    public ResolvableCommand(String cmd, int sec, int args, boolean set){
        command=cmd;
        securitylevel = sec;
        arguments = args;
        setargs = set;
    }

    public boolean verify(String label, String[] args, int securitylevel){
        if(securitylevel >= this.securitylevel && args.length <= arguments && command.equals(label)){
            if(args.length != arguments && setargs){
                return false;
            }
            else return true;
        }
        else return false;
    }

}

public class OrgsManager {


    private Set<ResolvableCommand> commandset;

    public Set<Organization> global_orgs;

    public OrgsManager(){
        //global_orgs = OrgsDatabaseController.loaddb();
        commandset.add(new ResolvableCommand("create", 0, 1, false));
        commandset.add(new ResolvableCommand("delete", 3, 1, true));
        commandset.add(new ResolvableCommand("addmember", 2, 2, false));
        commandset.add(new ResolvableCommand("addmoder", 3, 2, false));
        commandset.add(new ResolvableCommand("addadmin", 3, 2, false));
        commandset.add(new ResolvableCommand("removemember", 2, 2, false));
        commandset.add(new ResolvableCommand("removemoder", 3, 2, false));
        commandset.add(new ResolvableCommand("removeadmin", 3, 2, false));
        commandset.add(new ResolvableCommand("moneygive", 1, 2, false));
        commandset.add(new ResolvableCommand("moneyget", 1, 2, false));
        commandset.add(new ResolvableCommand("moneyset", 4, 2, false));
    }

    public boolean resolve(CommandSender sender, String command, String[] args){
        int sl = securityLevel(sender, args[0]);
        for(Iterator<ResolvableCommand> i = commandset.iterator(); i.hasNext(); ) {
            if(i.next().verify(command, args, sl)){
                specificExecutor(sender, command, args);
            }
        }
        ActionBroadcaster.getInstance().tell(sender, "Command unresolved, try again");
        ActionBroadcaster.getInstance().log(sender.getName()+" failed to use orgs");
        return false;
    }

    private Organization findOrganizationByName(String name){
        for(Iterator<Organization> i = global_orgs.iterator(); i.hasNext(); ) {
            if(i.next().name == name){
                return i.next();
            }
        }
        return null; //TODO exception
    }

    private int securityLevel(CommandSender sender, String orgname){
        if(sender instanceof Player){
            if(sender.hasPermission("shadoworgs.admin") || sender.isOp()){
                return 4;
            }
            if(findOrganizationByName(orgname) != null) {
                if (findOrganizationByName(orgname).isAdmin((Player) sender)) {
                    return 3;
                }
                if (findOrganizationByName(orgname).isModer((Player) sender)) {
                    return 2;
                }
                if (findOrganizationByName(orgname).isMember((Player) sender)) {
                    return 1;
                }
            }
            else return 0;
        }
        return 4;
    }

    private void specificExecutor(CommandSender sender, String command, String[] args){
        //Organization wt = findOrganizationByName(args[0]);
        if(command.equals("create") && findOrganizationByName(args[0]) == null && (sender instanceof Player)){
            global_orgs.add(new Organization(args[0], (Player)sender)); //TODO console resolving

        }
        if(command.equals("delete") && findOrganizationByName(args[0]) != null){
            global_orgs.remove(findOrganizationByName(args[0])); //TODO console resolving

        }
    }
}
