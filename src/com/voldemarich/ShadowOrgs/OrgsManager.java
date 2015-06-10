package com.voldemarich.ShadowOrgs;

import com.google.common.collect.ImmutableBiMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

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


    //BUG wtf returning null on inverse.
    public static final ImmutableBiMap<String, Integer> rightnames =
            new ImmutableBiMap.Builder<String, Integer>()
                    .put("notmember", -1)
                    .put("member", 0)
                    .put("moder", 1)
                    .put("admin", 2)
                    .build();

    private final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs");

    private final ActionBroadcaster logger = ActionBroadcaster.getInstance();

    private final OrgsDatabaseController db = OrgsDatabaseController.getInstance();

    private HashMap<String, Organization> organizations;
    //Todo map<Playername, String[]> orgmember or func retrieving dat

    public OrgsManager() {
        organizations = db.readAll();
    }

    public boolean hasOrgPermission(CommandSender sender, String orgname, int permission) {
        return sender.hasPermission("shadoworgs.admin") || getOrgByName(orgname).getRight((OfflinePlayer) sender) >= permission;
    }

    public String memberLookup(Player player){
        StringBuilder sb = new StringBuilder();
        for(Organization o:organizations.values()){
            if(o.isMember(player)){
                sb.append("§eIn §5");
                sb.append(o.string_id);
                sb.append("§e as §5");
                sb.append(rightnames.inverse().get(o.getRight(player)));
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public Organization createOrganization(String name, OfflinePlayer owner) throws OrganizationException{
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

    public void addMember(String orgname, OfflinePlayer member, int right){
        getOrgByName(orgname).addMember(member, right);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player " + member.getName() + " was added to organization "+orgname+" with right of " + rightnames.inverse().get(right));
    }

    public void removeMember(String orgname, OfflinePlayer member){
        getOrgByName(orgname).removeMember(member);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player " + member.getName() + " was removed from org " + orgname);
    }

    public void changeMemberRight(String orgname, OfflinePlayer member, int right){
        if(right<0){
            getOrgByName(orgname).removeMember(member);
            return;
        }
        getOrgByName(orgname).setRight(member, right);
        db.writeSingleOrg(organizations.get(orgname));
        logger.info("Player " + member.getName() + " was promoted/demoted to the right of " + rightnames.inverse().get(right) + " in the organization " + orgname);

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
