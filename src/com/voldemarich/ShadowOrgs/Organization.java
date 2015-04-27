package com.voldemarich.ShadowOrgs;

import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Created by voldemarich on 21.04.15.
 * General organization class. Contains registry of members, id, balance and methods
 */
public class Organization {
    public String string_id;
    private Set<Player> admins;
    private Set<Player> moders;
    private Set<Player> members; //Todo dictionary/tuple array (player, right)
    private String bank;
    private double money;


    public Organization(String string_id, Player owner/*,  Set<Player> admins, Set<Player> moders, Set<Player> members*/){ //TODO resolve nulls
        this.string_id = string_id;
        addAdmin(owner);
        bank = "orgbank_"+ string_id;
        ShadowOrgs.econ.createBank(bank, owner.getName());
        money = ShadowOrgs.econ.bankBalance(bank).balance;

    }

    public boolean isMember(Player player){
        return members.contains(player);
    }

    public boolean isModer(Player player){
        return moders.contains(player);
    }

    public boolean isAdmin(Player player){
        return admins.contains(player);
    }

    public void addMember(Player player){
        if(!members.contains(player)){
            members.add(player);
        }
    }
    public void addModer(Player player){
        if(!moders.contains(player)){
            addMember(player);
            moders.add(player);
        }
    }
    public void addAdmin(Player player){
        if(!admins.contains(player)){
            addMember(player);
            removeModer(player);
            admins.add(player);
        }
    }

    public void removeMember(Player player){
        if(members.contains(player)){
            members.remove(player);
        }
    }

    public void removeModer(Player player){
        if(moders.contains(player)){
            moders.remove(player);
        }
    }

    public void removeAdmin(Player player){
        if(admins.contains(player)){
            admins.remove(player);
        }
    }

    //Here goes economy

    public boolean withdrawFunds(Player player, double amount){
        if(ShadowOrgs.econ.bankBalance(bank).balance >= amount){
            ShadowOrgs.econ.bankWithdraw(bank, amount);
            ShadowOrgs.econ.depositPlayer(player.getName(), amount);
            money = ShadowOrgs.econ.bankBalance(bank).balance;
            return true;
        }
        else return false;
    }

    public boolean depositFunds(Player player, double amount){
        if(ShadowOrgs.econ.has(player.getName(), amount)){
            ShadowOrgs.econ.withdrawPlayer(player.getName(), amount);
            ShadowOrgs.econ.bankDeposit(bank, amount);
            money = ShadowOrgs.econ.bankBalance(bank).balance;
            return true;
        }
        else return false;
    }

    public void setFunds(double amount){
        if(money <= amount) {
            ShadowOrgs.econ.bankDeposit(bank, amount-money);
        }
        else{
            ShadowOrgs.econ.bankWithdraw(bank, money-amount);
        }
        money = ShadowOrgs.econ.bankBalance(bank).balance;
    }
}
