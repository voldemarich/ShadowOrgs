package com.voldemarich.ShadowOrgs;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by voldemarich on 21.04.15.
 * General organization class. Contains registry of members, id, balance and methods
 */
public class Organization {

    public String string_id;

    private HashMap<String, Integer> members;
    private String bank;

    public Organization(String string_id, Player owner){ //As the command of console
        this.string_id = string_id;
        addMember(owner, 2); //Add admin
        bank = "orgbank_"+ string_id;
        ShadowOrgs.econ.createBank(bank, owner.getName());
    }

    public Organization(String string_id, HashMap<String, Integer> members, String bank){ //As database loader object
        this.string_id = string_id;
        this.members = members;
        this.bank = bank;
    }

    public boolean isMember(Player player){
            return members.containsKey(player.getName());
    }

    public int getRight(Player player){
        if(isMember(player)){
            return members.get(player.getName());
        }
        else return -1;
    }

    public void addMember(Player player, int right){
        if(!isMember(player)) members.put(player.getName(), right);
    }

    public void addMember(Player player){
        if(!isMember(player)) members.put(player.getName(), 0);
    }


    public void removeMember(Player player){
        if(isMember(player)) members.remove(player.getName());
    }


    //Here goes economy

    public boolean withdrawFunds(Player player, double amount){
        if(ShadowOrgs.econ.bankBalance(bank).balance >= amount){
            ShadowOrgs.econ.bankWithdraw(bank, amount);
            ShadowOrgs.econ.depositPlayer(player.getName(), amount);
            return true;
        }
        else return false;
    }

    public boolean depositFunds(Player player, double amount){
        if(ShadowOrgs.econ.has(player.getName(), amount)){
            ShadowOrgs.econ.withdrawPlayer(player.getName(), amount);
            ShadowOrgs.econ.bankDeposit(bank, amount);
            return true;
        }
        else return false;
    }

    public void setFunds(double amount){
        ShadowOrgs.econ.bankWithdraw(bank, ShadowOrgs.econ.bankBalance(bank).balance);
        ShadowOrgs.econ.bankDeposit(bank, amount);
    }
}
