package com.voldemarich.ShadowOrgs;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by voldemarich on 21.04.15.
 * General organization class. Contains registry of members, id, balance and methods
 */
public class Organization {

    public String string_id;

    public HashMap<String, Integer> members;
    public String bank;

    public Organization(String string_id, Player owner){ //As the command of console
        members = new HashMap<String, Integer>();
        this.string_id = string_id;
        addMember(owner, 2); //Add admin
        bank = "orgbank_"+ string_id;
        ShadowOrgs.econ.createBank(bank, owner.getName());
    }

    public Organization(String string_id, String bank, HashMap<String, Integer> members){ //As database loader object
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

    //Declarative commands, runner's rights should be checked before running.

    public void setRight(Player player, int right){
        if(isMember(player)) {
            members.put(player.getName(), right);
        }
    }

    public void addMember(Player player, int right){
        if(!isMember(player)) members.put(player.getName(), right);
    }



    public void removeMember(Player player) throws OrganizationException{
        if(isMember(player)) members.remove(player.getName());
        else throw new OrganizationException("Dat player is not in this organization, man");
    }


    //Here goes economy
    //Economy uses it's own protection by right, so can be used without checking it one more time.

    public boolean withdrawFunds(Player player, double amount){
        if(ShadowOrgs.econ.bankBalance(bank).balance >= amount && getRight(player)>=0){
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
