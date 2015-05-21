package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.Set;

/**
 * Created by voldemarich on 21.04.15.
 * Database controller used for connecting to database and writing/retrieving organization entries
 */
public class OrgsDatabaseController {

    private static OrgsDatabaseController instance;
    private Connection connection;
    private static final String tablename = "orgs_data";

    public OrgsDatabaseController getInstance(){
        if(instance == null) instance = new OrgsDatabaseController();
        return instance;
    }

    public OrgsDatabaseController(){
        try {
            connection = connect();
        }
        catch (Exception e){
            ActionBroadcaster.getInstance().yell("Failed to connect to database!");
        }

    }

    private Connection connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        File dbContainer = new File(Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs").getDataFolder(), "alles.db");
        return DriverManager.getConnection("jdbc:sqlite:"+dbContainer.getAbsolutePath());

    }

    private void safeExecute(String request) throws SQLException{
        Statement stmt = connection.createStatement();
        try{
            stmt.executeUpdate(request);
        }
        catch (SQLException e){
            ActionBroadcaster.getInstance().yell("can't execute " + stmt + ": " + e.getMessage());
            throw e;
        }
    }

    private void createTableIfNotExists(String tableName, String data) throws SQLException {
            if (!tableExists(tableName)) {
                safeExecute("CREATE TABLE " + tableName + "(" + data + ")");
            }
    }

    private boolean tableExists(String table) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null , null, table, null);
        return tables.next();
    }

    private void writeSingleOrg(Organization org) throws SQLException{
        PreparedStatement st = connection.prepareStatement("DELETE FROM ? WHERE string_id='?' ");
    }


    public void writeAll(Set<Organization> organizations){

    }

    public Set<Organization> readAll(){
        return null;
    }




}
