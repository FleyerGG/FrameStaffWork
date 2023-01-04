package ru.fleyer.framestaffwork.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import ru.fleyer.framestaffwork.FrameStaffWork;

import java.sql.*;
import java.util.Date;

public class DatabaseConstructor {
    public static DatabaseConstructor INSTANCE = new DatabaseConstructor();
    HikariDataSource hikari = FrameStaffWork.getInstance().getHikari();
    String table = FrameStaffWork.getInstance().config().yaml().getString("mysql.table");
    public String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS " + table + " (`id` INT NOT NULL AUTO_INCREMENT , " +
            "`NickName` VARCHAR(16) NULL DEFAULT NULL , " +
            "`group` TEXT NULL DEFAULT NULL , " +
            "`bans` TEXT NULL DEFAULT NULL , " +
            "`mutes` INT NULL DEFAULT NULL ," +
            "`kicks` INT NULL DEFAULT NULL ," +
            "`unbans` INT NULL DEFAULT NULL ," +
            "`unmutes` INT NULL DEFAULT NULL ," +
            "`time` INT NULL DEFAULT NULL ," +
            "`worked` INT NULL DEFAULT NULL ," +
            "`vkID` INT NULL DEFAULT NULL ," +
            "`seen` VARCHAR(200) NULL DEFAULT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";

    public void createTable(){
        try (Connection connection = FrameStaffWork.getInstance().getHikari().getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_TABLES)){
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String CREATE_PLAYER = "INSERT INTO " + table + " (`NickName`,`group`,`bans`,`mutes`,`kicks`,`unbans`,`unmutes`,`time`,`worked`,vkID,seen) VALUES  (?,?,?,?,?,?,?,?,?,?,?)";
    public void createPlayer(String player,String group,int vkID, long seen){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_PLAYER)){
            statement.setString(1,player);
            statement.setString(2,group);
            statement.setInt(3,0);
            statement.setInt(4,0);
            statement.setInt(5,0);
            statement.setInt(6,0);
            statement.setInt(7,0);
            statement.setLong(8,0);
            statement.setBoolean(9,false);
            statement.setInt(10,vkID);
            statement.setLong(11, seen);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public boolean proverka(String NickName,String group,long seen){
        boolean proverka = false;
        try(Connection connection = hikari.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
        ) {
            statement.setString(1,NickName);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                proverka = true;
                return true;
            }else {
                createPlayer(NickName,group,0,seen);
                proverka = true;
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return proverka;
    }

    public int getBans(String player){
        int bans = 0;
        try (Connection connection = hikari.getConnection(); 
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                bans = resultSet.getInt("bans");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bans;
    }
    public int getMute(String player){
        int mutes = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                mutes = resultSet.getInt("mutes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutes;
    }
    public int getKicks(String player){
        int kiks = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                kiks = resultSet.getInt("kicks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kiks;
    }
    public int getUnMutes(String player){
        int unmutes = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                unmutes = resultSet.getInt("unmutes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unmutes;
    }
    public int getUnBans(String player){
        int unbans = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                unbans = resultSet.getInt("unbans");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unbans;
    }
    public boolean getPlayerWorked(String NickName){
        boolean worked = false;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,NickName);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                worked = resultSet.getBoolean("worked");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return worked;
    }
    public int getPlayerAddVk (String NickName){
        int id = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,NickName);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                id = resultSet.getInt("vkID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    //
    public void setPlayerVKid(int vkID, String NickName){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET vkID=? WHERE NickName=?")){
            statement.setInt(1,vkID);
            statement.setString(2,NickName);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setBanss(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statementget = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
             PreparedStatement statementset = connection.prepareStatement("UPDATE " + table + " SET `bans`=? WHERE NickName=?")){
            statementget.setString(1,player);
            statementget.execute();

            ResultSet resultSet = statementget.executeQuery();
            while (resultSet.next()){
                statementset.setInt(1,resultSet.getInt("bans") + 1);
                statementset.setString(2,player);
                statementset.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setUnBanss(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statementget = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
             PreparedStatement statementset = connection.prepareStatement("UPDATE " + table + " SET `unbans`=? WHERE NickName=?")){
            statementget.setString(1,player);
            statementget.execute();

            ResultSet resultSet = statementget.executeQuery();
            while (resultSet.next()){
                statementset.setInt(1,resultSet.getInt("unbans") + 1);
                statementset.setString(2,player);
                statementset.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setMutes(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statementget = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
             PreparedStatement statementset = connection.prepareStatement("UPDATE " + table + " SET `mutes`=? WHERE NickName=?")){
            statementget.setString(1,player);
            statementget.execute();

            ResultSet resultSet = statementget.executeQuery();
            while (resultSet.next()){
                statementset.setInt(1,resultSet.getInt("mutes") + 1);
                statementset.setString(2,player);
                statementset.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setUnMutes(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statementget = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
             PreparedStatement statementset = connection.prepareStatement("UPDATE " + table + " SET `unmutes`=? WHERE NickName=?")){
            statementget.setString(1,player);
            statementget.execute();

            ResultSet resultSet = statementget.executeQuery();
            while (resultSet.next()){
                statementset.setInt(1,resultSet.getInt("unmutes") + 1);
                statementset.setString(2,player);
                statementset.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setKicks(String player){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statementget = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?");
             PreparedStatement statementset = connection.prepareStatement("UPDATE " + table + " SET `kicks`=? WHERE NickName=?")){
            statementget.setString(1,player);
            statementget.execute();

            ResultSet resultSet = statementget.executeQuery();
            while (resultSet.next()){
                statementset.setInt(1,resultSet.getInt("kicks") + 1);
                statementset.setString(2,player);
                statementset.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerGroup(String group, String NickName){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET `group`=? WHERE NickName=?")){
            statement.setString(1,group);
            statement.setString(2,NickName);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setwork(String player, boolean work){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET `worked`=? WHERE NickName=?")){
            statement.setBoolean(1,work);
            statement.setString(2,player);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getPlayerOwner(int vkid){
        String group = "";
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE vkID=?")){
            statement.setInt(1,vkid);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                group = resultSet.getString("group");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return group;
    }
    public String getPlayerNameOrVkId(int vkid){
        String playerName = "";
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE vkID=?")){
            statement.setInt(1,vkid);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                playerName = resultSet.getString("NickName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerName;
    }
    public void setTime(String player, int time){
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET `time`=? WHERE NickName=?")){
            statement.setLong(1,time);
            statement.setString(2,player);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public long getTimeStaff(String player){
        long time = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                time = resultSet.getInt("time");
                FrameStaffWork.getInstance().time.put(player, (int) time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void updateTimeSeen(String player,long seen){
        try (Connection connection = hikari.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE " + table + " SET `seen`=? WHERE NickName=?")){
            statement.setLong(1,seen);
            statement.setString(2,player);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getTimeSeen (String player){
        long dates = 0;
        try (Connection connection = hikari.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT  * FROM " + table + " WHERE NickName=?")){
            statement.setString(1,player);
            statement.execute();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                dates = resultSet.getLong("seen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }
}
