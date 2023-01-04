package ru.fleyer.framestaffwork.listeners;

import com.ubivashka.vk.bukkit.BukkitVkApiPlugin;
import com.ubivashka.vk.bukkit.events.VKMessageEvent;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.fleyer.framestaffwork.FrameStaffWork;
import ru.fleyer.framestaffwork.database.DatabaseConstructor;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VkEventsListener implements Listener {

    private final static VkApiClient CLIENT = BukkitVkApiPlugin.getPlugin(BukkitVkApiPlugin.class).getVkApiProvider()
            .getVkApiClient();
    private final static GroupActor ACTOR = BukkitVkApiPlugin.getPlugin(BukkitVkApiPlugin.class).getVkApiProvider()
            .getActor();
    private final static Random RANDOM = new Random();

    public String msg(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    FileConfiguration lang = FrameStaffWork.getInstance().lang().yaml();
    @EventHandler
    public void onMessage(VKMessageEvent e) {
        try {

            String[] words = e.getMessage().getText().split("\\s+");
            if (e.getMessage().getText().equalsIgnoreCase(".peerID")) {
                CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message("peerID: " + e.getMessage().getPeerId()).execute();
                return;
            }
            if (words[0].equalsIgnoreCase(".help")){
                CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer())
                        .message(lang.getString("vk_messages.help")).execute();
            }
            if (words[0].equalsIgnoreCase(".addVk")) {

                int from = e.getMessage().getFromId();
                if (DatabaseConstructor.INSTANCE.getPlayerOwner(from).equals("admin")) {

                    //todo: добавить else
                    if (words.length == 4) {
                        if (isNumeric(words[2])) {
                            CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.isNumeric")).execute();
                            return;
                        }
                        if (words[2].length() == 9) {
                            if (DatabaseConstructor.INSTANCE.getPlayerAddVk(words[1]) == Integer.parseInt(words[2]) &&
                                    DatabaseConstructor.INSTANCE.getPlayerNameOrVkId(Integer.parseInt(words[2])).equals(words[1])){

                                CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer())
                                        .message(lang.getString("vk_messages.updateGroup")).execute();
                                DatabaseConstructor.INSTANCE.setPlayerGroup(words[3], words[1]);
                                return;
                            }

                            DatabaseConstructor.INSTANCE.createPlayer(words[1], words[3], Integer.parseInt(words[2]),new Date().getTime());
                            CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer())
                                    .message(lang.getString("vk_messages.addPlayerVk")
                                            .replace("%name%", words[1])
                                            .replace("%group%", words[3])
                                            .replace("%id%",words[2])).execute();
                        } else {
                            CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.isIDlenght")).execute();
                        }
                        return;
                    }
                } else {
                    CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer())
                            .message(lang.getString("vk_messages.noadmin")).execute();
                    return;

                }

            }

            if (words[0].equalsIgnoreCase(".stats")) {


                if (words.length == 1) {
                    String name = DatabaseConstructor.INSTANCE.getPlayerNameOrVkId(e.getMessage().getFromId());
                    if (name == null || name.equals("")) {
                        CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.YouNullDB")).execute();
                        return;
                    }



                    test(e, name,e.getMessage().getFromId());


                } else {
                    if (DatabaseConstructor.INSTANCE.getPlayerOwner(e.getMessage().getFromId()).equals("admin")) {
                        if (isNumeric(words[1])) {
                            CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.stats.isNumeric")).execute();
                            return;
                        }
                        int targetint = Integer.parseInt(words[1]);
                        if (words[1].length() == 9) {
                            String target = DatabaseConstructor.INSTANCE.getPlayerNameOrVkId(targetint);
                            if (target == null || target.equals("")) {
                                CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.stats.targetNullDB")
                                        .replace("%targetID%", String.valueOf(targetint))).execute();
                                return;
                            }
                            test(e, target,targetint);

                        } else {
                            CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(lang.getString("vk_messages.stats.isIDlenght")).execute();
                        }



                    }else {
                        CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer())
                                .message(lang.getString("vk_messages.noadmin")).execute();


                    }

                }
            }
        } catch (ApiException | ClientException e1) {
            e1.printStackTrace();
        }
    }

    private void test(VKMessageEvent e, String target, int vkId) throws ApiException, ClientException {
        DatabaseConstructor inst = DatabaseConstructor.INSTANCE;
        int time = (int) inst.getTimeStaff(target);


        long days = TimeUnit.SECONDS.toDays(time);
        time -= TimeUnit.DAYS.toSeconds(days);
        long hours = TimeUnit.SECONDS.toHours(time);
        time -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = TimeUnit.SECONDS.toSeconds(time);
        String times = msg(FrameStaffWork.getInstance().config().yaml().getString("vk_time_format")
                .replace("%days%",String.valueOf(days))
                .replace("%hours%",String.valueOf(hours))
                .replace("%minutes%",String.valueOf(minutes))
                .replace("%seconds%",String.valueOf(seconds)));
        CLIENT.messages().send(ACTOR).randomId(RANDOM.nextInt()).peerId(e.getPeer()).message(
                lang.getString("vk_messages.stats.info")
                        .replace("%time%", times)
                        .replace("%bans%", String.valueOf(inst.getBans(target)))
                        .replace("%mutes%", String.valueOf(inst.getMute(target)))
                        .replace("%kicks%", String.valueOf(inst.getKicks(target)))
                        .replace("%unbans%", String.valueOf(inst.getUnBans(target)))
                        .replace("%unmute%", String.valueOf(inst.getUnMutes(target)))
                        .replace("%name%", "@id" + vkId + " ("+target+")")
                        .replace("%seen%",new Date(inst.getTimeSeen(target)).toString())
                        .replace("%status%", DatabaseConstructor.INSTANCE.getPlayerWorked(target)
                                ? "Активен"
                                : "Не активен")).execute();

    }

    public boolean isNumeric(String str) {
        return !str.matches("-?\\d+(\\.\\d+)?");
    }
}
