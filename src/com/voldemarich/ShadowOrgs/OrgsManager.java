package com.voldemarich.ShadowOrgs;

import java.util.Set;

/**
 * Created by voldemarich on 17.05.15.
 */
public class OrgsManager {
    private static OrgsManager ourInstance = new OrgsManager();

    public static OrgsManager getInstance() {
        return ourInstance;
    }

    private Set<Organization> organizations;

    private OrgsManager() {

    }

    public void createOrganization() throws OrganizationException{

    }

    public boolean createOrganization(){

    }

    public Organization getOrgByName(String name){
        //search
        return null;
    }
}
