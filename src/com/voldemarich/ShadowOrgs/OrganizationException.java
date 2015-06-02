package com.voldemarich.ShadowOrgs;

/**
 * Created by voldemarich on 21.04.15.
 */
public class OrganizationException extends RuntimeException {


    public OrganizationException(String message){
        super("§c"+message);
    }
}
