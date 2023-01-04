package ru.fleyer.framestaffwork.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;
import ru.fleyer.framestaffwork.FrameStaffWork;
import ru.fleyer.framestaffwork.database.DatabaseConstructor;

public class LocalizedUtils {


 public static String msg(String msg) {
     return ChatColor.translateAlternateColorCodes('&', msg);
 }


    static FileConfiguration config = FrameStaffWork.getInstance().config().yaml();

    public static void StaffWork(Player player) {
        String group = FrameStaffWork.perms.getPrimaryGroup(player);

        if (!player.hasPermission("staffwork")){
            player.sendMessage("Ты не состав");
            return;
        }
        if (player.hasMetadata("sw_on")) {
            player.removeMetadata("sw_on", FrameStaffWork.getInstance());
            DatabaseConstructor.INSTANCE.setwork(player.getName(),false);
            Bukkit.getScheduler().cancelTask(FrameStaffWork.getInstance().tasks.get(player.getName()));
            player.sendMessage(msg(config.getString("groups_off." + group + ".message")));

            for (String cmd : config.getStringList("groups_off." + group + ".commands")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%", player.getName()));

            }
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()){

                if (onlinePlayers.hasPermission(config.getString("perms.view"))){
                    onlinePlayers.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("groups_off." + group + ".ALLmessage")
                                            .replace("%player%", player.getName())
                                            .replace("%prefix%", FrameStaffWork.chat.getPlayerPrefix(player))));
                }

            }

        }

        else {
            player.setMetadata("sw_on", new FixedMetadataValue(FrameStaffWork.getInstance(), player));
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(FrameStaffWork.getInstance(),
                    new ServerUtils(player.getName()), 0L, 20L);
            DatabaseConstructor.INSTANCE.setwork(player.getName(),true);

            FrameStaffWork.getInstance().tasks.put(player.getName(), task.getTaskId());

            player.sendMessage(msg(config.getString("groups_on." + group + ".message")));
            for (String cmd : config.getStringList("groups_on." + group + ".commands")){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%", player.getName()));

            }
            for (Player onlinePlayers : Bukkit.getOnlinePlayers()){

                if (onlinePlayers.hasPermission(config.getString("perms.view"))){
                    onlinePlayers.sendMessage(
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("groups_on." + group + ".ALLmessage")
                                            .replace("%player%", player.getName())
                                            .replace("%prefix%", FrameStaffWork.chat.getPlayerPrefix(player))));
                }
            }
        }
    }


}
