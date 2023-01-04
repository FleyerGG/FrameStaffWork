package ru.fleyer.framestaffwork.listeners;


import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ru.fleyer.framestaffwork.FrameStaffWork;
import ru.fleyer.framestaffwork.database.DatabaseConstructor;

import java.util.Date;



public class EventListener implements Listener {

	public static FrameStaffWork instance = FrameStaffWork.getInstance();
	FileConfiguration lang = instance.lang().yaml();
	FileConfiguration config = instance.config().yaml();
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			if (instance.getConfig().getBoolean("settings.hit")) {
				if (damager.hasMetadata("sw_on")) {
					e.setCancelled(true);
					damager.sendMessage(FrameStaffWork.s(lang.getString("hit_deny")));
				}
			}
		}
	}

	@EventHandler
	public void onOpen(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		if (instance.getConfig().getBoolean("settings.chest")) {
			if (p.hasMetadata("sw_on")) {
				if (b == null) {
					return;
				}
				if (b.getType() == null)
					return;
				if (b.getType() != null) {
					if (b.getType() == Material.CHEST || b.getType() == Material.ENDER_CHEST
							|| b.getType() == Material.TRAPPED_CHEST) {
						e.setCancelled(true);
						p.sendMessage(FrameStaffWork.s(lang.getString("chest_deny")));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (!p.hasMetadata("sw_on")) {
			String command = e.getMessage();
			for (String cmd : instance.getConfig().getStringList("settings.deny-commands-list")) {
				if (command.toLowerCase().startsWith(cmd)) {
					e.setCancelled(true);
					p.sendMessage(FrameStaffWork.s(lang.getString("deny")));
					return;
				}
			}
		}
	}
	public static String papi(Player player, String text) {
		if (PlaceholderAPI.containsPlaceholders(text)) {
			text = PlaceholderAPI.setPlaceholders(player, text);
		}

		return text;
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (p.hasMetadata("sw_on")) {
			p.removeMetadata("sw_on", instance);
			p.setMetadata("quit", new FixedMetadataValue(instance, p));
			Bukkit.getScheduler().cancelTask(FrameStaffWork.getInstance().tasks.get(p.getName()));
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (pl.hasPermission(instance.getConfig().getString("perms.staffwork"))) {
					final String group = FrameStaffWork.perms.getPrimaryGroup(p);
					for (String cmd : config.getStringList("groups_off." + group + ".commands")){
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%player%", p.getName()));

					}
					p.sendMessage(papi(p,FrameStaffWork.s(FrameStaffWork.getInstance().config().yaml().getString("groups_off." + group + ".ALLmessage")
							.replace("%player%", p.getName())
							.replace("%prefix%", FrameStaffWork.chat.getPlayerPrefix(p)))));

				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (p.hasMetadata("quit")) {
			p.sendMessage(FrameStaffWork.s(lang.getString("quit")));
			p.removeMetadata("quit", instance);
		}

		String group = FrameStaffWork.perms.getPrimaryGroup(p);
		if (p.hasPermission(instance.config().yaml().getString("perms.staffwork"))){
			Date date = new Date();
			DatabaseConstructor.INSTANCE.updateTimeSeen(p.getName(),date.getTime());
			DatabaseConstructor.INSTANCE.proverka(p.getName(),group,date.getTime());
		}
	}

}
