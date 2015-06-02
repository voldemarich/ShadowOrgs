package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Created by voldemarich on 17.05.15.
 * Bridge-like class used for managing organizations
 */
public class OrgsManager {
    private static OrgsManager ourInstance = new OrgsManager();

    public static OrgsManager getInstance() {
        return ourInstance;
    }

    private final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs");

    private final ActionBroadcaster logger = ActionBroadcaster.getInstance();

    private final OrgsDatabaseController db = OrgsDatabaseController.getInstance();

    private HashMap<String, Organization> organizations;

    public OrgsManager() {
        organizations = db.readAll();
    }

    public boolean hasOrgPermission(CommandSender sender, String orgname, int permission) {
        return !(sender instanceof Player) || getOrgByName(orgname).getRight((Player) sender) >= permission;
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
            organizations.remove(name);
            logger.info("Organization " + name + " deleted.");
        }
        else throw new OrganizationException("Dat org does not exist, man");
    }

    public void addMember(String orgname, Player member, int right) throws OrganizationException{
        getOrgByName(orgname).addMember(member, right);
        db.writeSingleOrg(organizations.get(orgname));
    }

    public void removeMember(String orgname, Player member) throws OrganizationException{
        getOrgByName(orgname).removeMember(member);
        db.writeSingleOrg(organizations.get(orgname));
    }

    public void changeMemberRight(String orgname, Player member, int right) throws OrganizationException{
        getOrgByName(orgname).setRight(member, right);
        db.writeSingleOrg(organizations.get(orgname));
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
