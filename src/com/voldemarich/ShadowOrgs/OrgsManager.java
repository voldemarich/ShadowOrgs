package com.voldemarich.ShadowOrgs;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by voldemarich on 17.05.15.
 */
public class OrgsManager {
    private static OrgsManager ourInstance = new OrgsManager();

    public static OrgsManager getInstance() {
        return ourInstance;
    }

    private HashMap<String, Organization> organizations;

    private OrgsManager() {

    }

    public Organization createOrganization(String name, Player owner) throws OrganizationException{
        if(!organizations.containsKey(name)){
            Organization org = new Organization(name, owner);
            organizations.put(name, org);
            //DATABASE
            return org;
        }
        else throw new OrganizationException("Dat org already exists, man");
    }

    public void deleteOrganization(String name) throws OrganizationException{
        if(organizations.containsKey(name)){
            organizations.remove(name);
            //DATABASE
        }
        else throw new OrganizationException("Dat org does not exist, man");
    }

    public Organization getOrgByName(String name) throws OrganizationException{
        if(organizations.containsKey(name)){
            return organizations.get(name);
        }
        else throw new OrganizationException("Dat org does not exist, man");
    }
}
