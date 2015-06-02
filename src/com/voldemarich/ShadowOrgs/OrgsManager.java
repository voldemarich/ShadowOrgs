package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by voldemarich on 17.05.15.
 * Bridge-like class used for managing organizations
 */

//TODO normal logging
public class OrgsManager {
    private static OrgsManager ourInstance = new OrgsManager();

    public static OrgsManager getInstance() {
        return ourInstance;
    }

    public static final Map<Integer, String> rightnames = createMap();

    private static Map<Integer, String> createMap() {
        Map<Integer, String> result = new HashMap<Integer, String>();
        result.put(-1, "§7not a member§f");
        result.put(0, "§bmember§f");
        result.put(1, "§6moder§f");
        result.put(2, "§5admin§f"); //TODO spelling without colours
        return Collections.unmodifiableMap(result);
    }

    private final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs");

    private final ActionBroadcaster logger = ActionBroadcaster.getInstance();

    private final OrgsDatabaseController db = OrgsDatabaseController.getInstance();

    private HashMap<String, Organization> organizations;

    public OrgsManager() {
        organizations = db.readAll();
    }

    public boolean hasOrgPermission(CommandSender sender, String orgname, int permission) {
        return sender.hasPermission("shadoworgs.admin") || getOrgByName(orgname).getRight((Player) sender) >= permission;
    }

    public Organization createOrganization(String name, Player owner) throws OrganizationException{
        if(!organizations.containsKey(name)){
            Organization org = new Organization(name, owner);
            organizations.put(name, org);
            db.writeSingleOrg(org);
            logger.info("Organization " + name + " created. Owner permissions granted to "+ owner.getName());
            return org;
        }
        else throw new OrganizationException("Dat org already exists, man");
    }

    public void removeOrganization(String name) throws OrganizationException{
        if(organizations.containsKey(name)){
            db.removeSingleOrg(organizations.get(name));
            ShadowOrgs.econ.deleteBank(organizations.get(name).bank);
            organizations.remove(name);
            logger.info("Organization " + name + " deleted.");
        }
        else throw new OrganizationException("Dat org does not exist, man");
    }

    public void addMember(String orgname, Player member, int right){
        getOrgByName(orgname).addMember(member, right);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player "+member.getName()+" was added to org with right of "+rightnames.get(right));
    }

    public void removeMember(String orgname, Player member){
        getOrgByName(orgname).removeMember(member);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player " + member.getName() + " was removed from org " + orgname);
    }

    public void changeMemberRight(String orgname, Player member, int right){
        getOrgByName(orgname).setRight(member, right);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player " + member.getName() + " was promoted/demoted to the right of " + rightnames.get(right) + " in the organization " + orgname);

    }

    public void saveAndSync(){
        db.writeAll(organizations);
    }

    public Organization getOrgByName(String name) throws OrganizationException{
        if(organizations.containsKey(name)){
            return organizations.get(name);
        }
        else throw new OrganizationException("Dat org does not exist, man");
    }
}
