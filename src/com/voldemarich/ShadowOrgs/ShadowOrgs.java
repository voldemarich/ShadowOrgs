package com.voldemarich.ShadowOrgs;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by voldemarich on 20.04.15.
 */
public class ShadowOrgs extends JavaPlugin {

    public static Economy econ = null;
    public static OrgsManager manager = null;
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        setupEconomy();
        getLogger().info("ShadowLand Organizations Plugin initialized successfully");
        if(getConfig().getBoolean("Enabled")) {
            //getServer().getPluginManager().registerEvents(new TerritoryClaimListener(), this); //Linking the command listener
            this.getCommand("orgs").setExecutor(new OrgsCommandExecutor(this, OrgsManager.getInstance()));
        }
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }



    @Override
    public void onDisable(){

    }

}
