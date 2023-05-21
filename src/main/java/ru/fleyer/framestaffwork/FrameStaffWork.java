package ru.fleyer.framestaffwork;

import com.zaxxer.hikari.HikariDataSource;
import litebans.api.Entry;
import litebans.api.Events;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.fleyer.framestaffwork.commands.StaffWorkCmd;
import ru.fleyer.framestaffwork.database.DatabaseConstructor;
import ru.fleyer.framestaffwork.listeners.EventListener;
import ru.fleyer.framestaffwork.listeners.VkEventsListener;
import ru.fleyer.framestaffwork.utils.ConfigurationGeneration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class FrameStaffWork extends JavaPlugin {
    private static FrameStaffWork instance;
    private HikariDataSource hikari;
    public Map<String, Integer> time;
    public Map<String, Integer> tasks;
    public static Economy econ;
    public static Chat chat;
    public static Permission perms;
    private ConfigurationGeneration config;
    private ConfigurationGeneration lang;

   public static FrameStaffWork getInstance(){
       return instance;
   }
    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigurationGeneration(this,"config.yml");
        lang = new ConfigurationGeneration(this,"lang.yml");
        registerEvents();
        setupEconomy();

        this.time = new HashMap<>();
        this.tasks = new HashMap<>();
        this.setupPermissions();

        getCommand("staffwork").setExecutor(new StaffWorkCmd());
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(new VkEventsListener(),this);
        long start = System.currentTimeMillis();
        this.getLogger().info(s("&7Успешно загружен и &2готов!&7 &8" + (System.currentTimeMillis() - start) + " ms"));

        FrameStaffWork.perms = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
        FrameStaffWork.chat = Bukkit.getServicesManager().getRegistration(Chat.class).getProvider();
        Bukkit.getScheduler().runTaskTimer(this, new BukkitRunnable() {
            @Override
            public void run() {
                time.forEach((player, times) -> DatabaseConstructor.INSTANCE.setTime(player, times));
            }
        }, 100L,0);


        //Plugin();
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", getConfig().getString("mysql.ip"));
        hikari.addDataSourceProperty("port",  getConfig().getInt("mysql.port"));
        hikari.addDataSourceProperty("databaseName",  getConfig().getString("mysql.database"));
        hikari.addDataSourceProperty("user",  getConfig().getString("mysql.username"));
        hikari.addDataSourceProperty("password",  getConfig().getString("mysql.password"));
        DatabaseConstructor.INSTANCE.createTable();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        super.onDisable();
    }
    public ConfigurationGeneration config() {
        return config;
    }

    public ConfigurationGeneration lang() {
        return lang;
    }
    public static String s(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
    public static void registerEvents() {
        Events.get().register(new Events.Listener() {
            public void entryAdded(Entry entry) {
                if (entry.getType().equals("ban")) {
                    DatabaseConstructor.INSTANCE.setBanss(entry.getExecutorName());
                }
                if (entry.getType().equals("mute")) {
                    DatabaseConstructor.INSTANCE.setMutes(entry.getExecutorName());

                }
                if (entry.getType().equals("kick")) {
                    DatabaseConstructor.INSTANCE.setKicks(entry.getExecutorName());

                }
            }
        });

        Events.get().register(new Events.Listener() {
            public void entryRemoved(Entry entry) {
                if (entry.getType().equals("mute")) {
                    DatabaseConstructor.INSTANCE.setUnMutes(entry.getExecutorName());

                }
                if (entry.getType().equals("ban")) {
                    DatabaseConstructor.INSTANCE.setUnBanss(entry.getExecutorName());

                }
            }
        });
    }
    private void setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        final RegisteredServiceProvider<Economy> rsp = this.getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        FrameStaffWork.econ = rsp.getProvider();
    }

    private void setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = this.getServer()
                .getServicesManager().getRegistration(Permission.class);
        FrameStaffWork.perms = rsp.getProvider();
    }
    public HikariDataSource getHikari() {
        return hikari;
    }
}
