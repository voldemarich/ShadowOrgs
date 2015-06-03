package com.voldemarich.ShadowOrgs;

import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by voldemarich on 21.04.15.
 * Database controller used for connecting to database and writing/retrieving organization entries
 */
public class OrgsDatabaseController {

    private static OrgsDatabaseController instance;
    private Connection connection;
    private final String tablename = "orgs_data";
    private Statement stmt = null;

    public static OrgsDatabaseController getInstance(){
        if(instance == null) instance = new OrgsDatabaseController();
        return instance;
    }

    public OrgsDatabaseController(){
        try {
            connection = connect();
            stmt = connection.createStatement();
            createOrgsTable();
        }
        catch (Exception e){
            ActionBroadcaster.getInstance().yell("DB failure: "+e.getMessage());
        }

    }

    private Connection connect() throws SQLException, ClassNotFoundException{
        Class.forName("org.sqlite.JDBC");
        File dbContainer = new File(Bukkit.getServer().getPluginManager().getPlugin("ShadowOrgs").getDataFolder(), "alles.db");
        return DriverManager.getConnection("jdbc:sqlite:"+dbContainer.getAbsolutePath());

    }


    private void createTableIfNotExists(String tableName, String data) throws SQLException {
            if (!tableExists(tableName)) {
                stmt.executeUpdate("CREATE TABLE " + tableName + "(" + data + ");");
            }
    }


    private boolean tableExists(String table) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null , null, table, null);
        return tables.next();
    }

    private void createOrgsTable() throws SQLException{
        createTableIfNotExists(tablename, "id INTEGER PRIMARY KEY AUTOINCREMENT, string_id TEXT UNIQUE NOT NULL, bank TEXT NOT NULL, members TEXT NOT NULL");
    }

    public void writeSingleOrg(Organization org){
        try {
            stmt.executeUpdate("INSERT OR REPLACE INTO " + tablename + " (id, string_id, bank, members) VALUES ((select id from " + tablename + " where string_id = \"" + org.string_id + "\"), \"" + org.string_id + "\", \"" + org.bank + "\", \"" + mapStringIntegerToSingleString(org.members) + "\")");
        }
        catch (SQLException e){
            ActionBroadcaster.getInstance().yell("Failed to write org: "+e.getMessage());
        }
    }

    public void removeSingleOrg(Organization org){
        try {
            stmt.executeUpdate("DELETE FROM "+tablename+" WHERE string_id=\""+org.string_id+"\"");
        }
        catch (SQLException e){
            ActionBroadcaster.getInstance().yell("Failed to write org: "+e.getMessage());
        }
    }


    public void writeAll(HashMap<String, Organization> organizations){
        try {
            stmt.executeUpdate("DROP TABLE " + tablename);
            createOrgsTable();
            for (Object o : organizations.values()) {
                writeSingleOrg((Organization) o);
            }
        }
        catch (SQLException e){
            ActionBroadcaster.getInstance().yell("Failed to overwrite organization list: "+e.getMessage());
        }
    }

    private String mapStringIntegerToSingleString(HashMap<String, Integer> hs){
        StringBuilder sb = new StringBuilder();
        Iterator a = hs.entrySet().iterator();
        Map.Entry e;
        while (a.hasNext()){
            e = (Map.Entry)a.next();
            sb.append(e.getKey());
            sb.append("\n");
            sb.append(e.getValue());
            sb.append("\n");
        }
        return sb.toString();

    }

    private HashMap<String, Integer> singleStringtoMapStringInteger(String str){

        String[] srcarr =  str.split("\n");
        HashMap<String, Integer> hs = new HashMap<String, Integer>();
        for(int i = 0; i<srcarr.length; i+=2){
            hs.put(srcarr[i], Integer.parseInt(srcarr[i+1]));
        }
        return hs;
    }

    public HashMap<String, Organization> readAll(){
        HashMap<String, Organization> hs = new HashMap<String, Organization>();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename);
            while (rs.next()) {
                hs.put(rs.getString("string_id"), new Organization(rs.getString("string_id"), rs.getString("bank"), singleStringtoMapStringInteger(rs.getString("members"))));
            }
        }
        catch (SQLException e){
            ActionBroadcaster.getInstance().yell("Failed to load organization list: "+e.getMessage());
        }
        return hs;
    }




}
