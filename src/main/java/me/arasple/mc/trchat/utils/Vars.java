package me.arasple.mc.trchat.utils;

import io.izzel.taboolib.module.inject.TSchedule;
import me.arasple.mc.trchat.TrChat;
import me.arasple.mc.trchat.TrChatFiles;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.cloud.CloudExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arasple
 * @date 2019/11/29 21:29
 */
public class Vars {

    @TSchedule(delay = 20 * 15)
    public static void downloadExpansions() {
        downloadExpansions(TrChatFiles.getSettings().getStringList("GENERAL.DEPEND-EXPANSIONS"));
    }

    /**
     * 自动下载 PlaceholderAPI 拓展变量并注册
     *
     * @param expansions 拓展
     */
    public static void downloadExpansions(List<String> expansions) {
        if (PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().getCloudExpansions() == null) {
            return;
        }
        try {
            if (expansions != null && expansions.size() > 0) {
                if (PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().getCloudExpansions().isEmpty()) {
                    PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().fetch(false);
                }
                List<String> unInstalled = expansions.stream().filter(d -> PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansions().stream().noneMatch(e -> e.getName().equalsIgnoreCase(d)) && PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().findCloudExpansionByName(d).isPresent() && !PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().isDownloading(PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().findCloudExpansionByName(d).get())).collect(Collectors.toList());
                if (unInstalled.size() > 0) {
                    unInstalled.forEach(ex -> {
                        CloudExpansion cloudExpansion = PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().getCloudExpansions().get(ex);
                        PlaceholderAPIPlugin.getInstance().getCloudExpansionManager().downloadExpansion(cloudExpansion, cloudExpansion.getVersion());
                    });
                    Bukkit.getScheduler().runTaskLater(TrChat.getPlugin(), () -> PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansions().forEach((it) -> PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().register(it)), 20);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    /**
     * 使用 PlaceholderAPI 变量替换
     *
     * @param player  玩家
     * @param strings 内容
     * @return 替换后内容
     */
    public static List<String> replace(Player player, List<String> strings) {
        List<String> results = new ArrayList<>();
        strings.forEach(str -> results.add(replace(player, str)));
        return results;
    }

    /**
     * 使用 PlaceholderAPI 变量替换
     *
     * @param player 玩家
     * @param string 内容
     * @return 替换后内容
     */
    public static String replace(Player player, String string) {
        if (string == null || player == null) {
            return string;
        }
        if (player instanceof Player) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }
        return string;
    }

}
