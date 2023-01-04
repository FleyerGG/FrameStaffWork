package ru.fleyer.framestaffwork.commands;

import litebans.api.Database;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.fleyer.framestaffwork.FrameStaffWork;
import ru.fleyer.framestaffwork.database.DatabaseConstructor;
import ru.fleyer.framestaffwork.utils.LocalizedUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StaffWorkCmd implements CommandExecutor {
    public String msg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    FrameStaffWork instance = FrameStaffWork.getInstance();
    FileConfiguration lang = instance.lang().yaml();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player player = (Player) sender;
        FrameStaffWork instance = FrameStaffWork.getInstance();
        final FileConfiguration cfg = instance.getConfig();
        if (args.length == 0 && player.hasPermission(instance.getConfig().getString("perms.staffwork"))) {
            LocalizedUtils.StaffWork(player);
            return true;
        }
        switch (args[0]) {
            case "addvk":{
                if (!player.hasPermission(cfg.getString("perms.admin"))){
                    player.sendMessage(msg(lang.getString("noperm")));
                    return false;
                }
                // /addvk Fleyer001 admin 123456789
                if (args.length < 4){
                    player.sendMessage("Введите правильно айди. А именно /addVK [Ник на сервере] [группа] [вк айди в цифренном виде] (длина айди 9 символов)");
                    return false;
                }
                if (args[3].length() < 9){
                    player.sendMessage("Введите правильно айди. А именно /addVK [Ник на сервере] [группа] [вк айди в цифренном виде] (длина айди 9 символов)");
                    return false;
                }
                if (!isNumeric(args[3])){
                    player.sendMessage("Введите правильно айди. А именно /addVK [Ник на сервере] [группа] [вк айди в цифренном виде] (длина айди 9 символов)");
                    return false;
                }
                java.util.Date date = new java.util.Date();
                DatabaseConstructor.INSTANCE.createPlayer(args[1],args[2], Integer.parseInt(args[3]), new Date().getTime());
                player.sendMessage("Успешно добавлен");

            }
            case "reload": {
                if (player.hasPermission(cfg.getString("perms.reload"))) {
                    instance.lang().reloadConfiguration();
                    instance.config().reloadConfiguration();
                    player.sendMessage(msg(lang.getString("reloaded")));
                    return true;
                } else return false;
            }
            case "help": {
                if (player.hasPermission(cfg.getString("perms.admin"))) {
                    lang.getStringList("help").forEach(line -> {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
                    });
                    return true;
                }else {
                    player.sendMessage(msg(lang.getString("noperm")));
                    return false;
                }
            }
            case "stats": {
                if (!player.hasPermission(cfg.getString("perms.admin"))){
                    player.sendMessage(msg(lang.getString("noperm")));
                    return false;
                }
                if (args.length == 1) {
                    if (player.hasMetadata("sw_on")) {
                        sendStatistic(player,true, player.getName());
                    } else {
                        sendStatistic(player,false, player.getName());
                    }
                    return false;

                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null || !target.isOnline() && !target.hasMetadata("sw_on")){
                        sendStatistic(player,false, args[1]);

                    }else {
                        sendStatistic(player,true, args[1]);
                    }
                    return true;

                }

            }
        }

        return false;
    }
    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
    public void sendStatistic(Player sender,boolean worked, String name) {
        DatabaseConstructor inst = DatabaseConstructor.INSTANCE;


        int time = (int) DatabaseConstructor.INSTANCE.getTimeStaff(name);


        long days = TimeUnit.SECONDS.toDays(time);
        time -= TimeUnit.DAYS.toSeconds(days);
        long hours = TimeUnit.SECONDS.toHours(time);
        time -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = TimeUnit.SECONDS.toSeconds(time);
        String times = msg(FrameStaffWork.getInstance().config().yaml().getString("time_format")
                .replace("%days%",String.valueOf(days))
                .replace("%hours%",String.valueOf(hours))
                .replace("%minutes%",String.valueOf(minutes))
                .replace("%seconds%",String.valueOf(seconds)));


        for (String s : lang.getStringList("stats.info")){
            sender.sendMessage( msg(s.replace("%target%", name)
                    .replace("%status", worked
                            ? lang.getString("stats.work")
                            : lang.getString("stats.nowork"))
                    .replace("%time%", times)
                    .replace("%bans%", String.valueOf(inst.getBans(name)))
                    .replace("%mutes%", String.valueOf(inst.getMute(name)))
                    .replace("%kicks%", String.valueOf(inst.getKicks(name))))
                    .replace("%unbans%", String.valueOf(inst.getUnBans(name)))
                    .replace("%unmute%", String.valueOf(inst.getUnMutes(name)))
                    .replace("%seen%",new Date(inst.getTimeSeen(name)).toString()));


        }

    }
}
