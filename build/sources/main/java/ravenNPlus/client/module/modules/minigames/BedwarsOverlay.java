package ravenNPlus.client.module.modules.minigames;

import com.google.gson.JsonObject;
import ravenNPlus.client.main.Client;
import ravenNPlus.client.module.Module;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.HashMap;

import static ravenNPlus.client.utils.Utils.Java.round;

public class BedwarsOverlay extends Module {

    public static SliderSetting overlayX, overlayY, margin, marginTextY, marginTextX;
    public static boolean active, reset;
    public static double overlayWidth, overlayHeight, textY;
    public static int mainTextColour, backgroundColour, linesDrawn, errorColour;
    public static HashMap<String, int[]> playerStats = new HashMap<>();
    public static HashMap<StatType, Integer> statStart = new HashMap<>();
    static boolean render = false;

    public BedwarsOverlay() {
        super("Bedwars Overlay", ModuleCategory.minigame, "");
        overlayHeight = 170;
        overlayWidth = 300;
        this.addSetting(overlayX = new SliderSetting("X", 4, 0, mc.displayWidth, 1));
        this.addSetting(overlayY = new SliderSetting("Y", 4, 0, mc.displayHeight, 1));
        this.addSetting(margin = new SliderSetting("Margin", 4, 0, 100, 1));
        this.addSetting(marginTextX = new SliderSetting("Margin Text X", 21, 0, 100, 1));
        this.addSetting(marginTextY = new SliderSetting("Margin Text Y", 8, 0, 100, 1));
        mainTextColour  = 0xffFEC5E5;
        backgroundColour = 0x903c3f41;
        errorColour = 0xffff0033;
    }

    @SubscribeEvent
    public void onChatMessageRecieved(ClientChatReceivedEvent event) {
        if(Utils.Player.isPlayerInGame()) {
            if(Utils.Java.str(event.message.getUnformattedText()).startsWith("Sending you to")) {
                if(mc.isSingleplayer()) {
                    playerStats.clear();
                    render = true;
                }
            }
        } else {
            render = false;
        }
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent e) {
        if(!Utils.Player.isPlayerInGame()) return;
        if(!active) return;

        if (!mc.inGameHasFocus || mc.gameSettings.showDebugInfo) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        linesDrawn = 0;
        drawMain(sr, fr);
        linesDrawn++;

        if(drawError(sr, fr)) {
            linesDrawn++;
        }

        for(NetworkPlayerInfo player : Utils.Client.getPlayers()){
            drawStats(player, fr);
        }
        overlayHeight = margin.getValue() * 2 + fr.FONT_HEIGHT * linesDrawn + marginTextY.getValue() * --linesDrawn;

        fr.drawString("", 0, 0, 0xffffffff);
    }

    private void drawStats(NetworkPlayerInfo player, FontRenderer fr) {
        String name = player.getGameProfile().getName();
        String UUID = player.getGameProfile().getId().toString();
        if (Utils.URLS.hypixelApiKey.isEmpty()){
            fr.drawString(name, statStart.get(StatType.NAME), (int)textY, 0xff05C3DD);
            textY += fr.FONT_HEIGHT + marginTextY.getValue();
            linesDrawn++;
        } else {
            double bbblr, wlr, fkdr;
            if(!playerStats.containsKey(UUID)){
                Client.getExecutor().execute(() -> getBedwarsStats(UUID));
                playerStats.put(UUID, new int[] {-16});
                return;
            }

            int[] stats = playerStats.get(UUID);

            if(stats.length == 1 && stats[0] == -16){
                return;
            }

            bbblr = stats[4] != 0 ? round((double)stats[3] / (double)stats[4], 2) : stats[3];
            fkdr = stats[6] != 0 ? round((double)stats[5] / (double)stats[6], 2) : stats[5];
            wlr = stats[8] != 0 ? round((double)stats[7] / (double)stats[8], 2) : stats[7];
            fr.drawString(stats[0] + "", statStart.get(StatType.LEVEL), (int)textY, getStarColour(stats[0]));
            fr.drawString(name, statStart.get(StatType.NAME), (int)textY, Colours.WHITE);
            if(stats[1] == 0) {
                fr.drawString("  -", statStart.get(StatType.NICKED), (int)textY, Colours.GREY);
            } else {
                fr.drawString("  +", statStart.get(StatType.NICKED), (int)textY, Colours.RED);
            }
            fr.drawString(stats[2] + "", statStart.get(StatType.WS), (int)textY, getWSColour(stats[2]));

            fr.drawString(bbblr + "", statStart.get(StatType.BBBLR), (int)textY, getBBBLRColour(bbblr));
            fr.drawString(fkdr + "", statStart.get(StatType.FKDR), (int)textY, getFKDRColour(fkdr));
            fr.drawString(wlr + "", statStart.get(StatType.WLR), (int)textY, getWLRColour(wlr));
            fr.drawString(stats[6] + "", statStart.get(StatType.FINALS), (int)textY, getFinalColour(stats[6]));
            fr.drawString(stats[7] + "", statStart.get(StatType.WINS), (int)textY, getFinalColour(stats[7]));

            textY += marginTextY.getValue() + fr.FONT_HEIGHT;
            linesDrawn++;
        }
    }

    private int getTreatColour(String bad) {
        //"&4VERY HIGH", "&cHIGH", "&6MODERATE", "&aLOW", "&2VERY LOW"
        if(bad.equalsIgnoreCase("very high")) {
            return Colours.RED;
        } else if(bad.equalsIgnoreCase("high")) {
            return Colours.ORANGE;
        }else if(bad.equalsIgnoreCase("moderate")) {
            return Colours.YELLOW;
        }else if(bad.equalsIgnoreCase("LOW")) {
            return Colours.GREEN;
        }else if(bad.equalsIgnoreCase("very low")) {
            return Colours.GREY;
        }

        return Colours.GREY;
    }

    private int getFinalColour(int stat) {
        if(stat < 50){
            return Colours.GREY;
        } else if(stat < 100) {
            return Colours.WHITE;
        } else if(stat < 150) {
            return Colours.GREEN;
        } else if(stat < 200) {
            return Colours.AQUA;
        } else if(stat < 500) {
            return Colours.YELLOW;
        } else if(stat < 1000) {
            return Colours.ORANGE;
        } else if(stat < 5000) {
            return Colours.RED;
        } else if(stat >= 5000) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    private int getFKDRColour(double stat) {
        if(stat < 0.31){
            return Colours.GREY;
        } else if(stat < 0.51) {
            return Colours.WHITE;
        } else if(stat < 1) {
            return Colours.GREEN;
        } else if(stat < 1.5) {
            return Colours.AQUA;
        } else if(stat < 2.5) {
            return Colours.YELLOW;
        } else if(stat < 4) {
            return Colours.ORANGE;
        } else if(stat < 10) {
            return Colours.RED;
        } else if(stat >= 20) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    private int getBBBLRColour(double stat) {
        if(stat < 0.31){
            return Colours.GREY;
        } else if(stat < 0.51) {
            return Colours.WHITE;
        } else if(stat < 1) {
            return Colours.GREEN;
        } else if(stat < 1.5) {
            return Colours.AQUA;
        } else if(stat < 2.5) {
            return Colours.YELLOW;
        } else if(stat < 4) {
            return Colours.ORANGE;
        } else if(stat < 10) {
            return Colours.RED;
        } else if(stat >= 20) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    private int getWLRColour(double stat) {
        if(stat < 0.51){
            return Colours.GREY;
        } else if(stat < 1.01) {
            return Colours.WHITE;
        } else if(stat < 1.5) {
            return Colours.GREEN;
        } else if(stat < 2) {
            return Colours.AQUA;
        } else if(stat < 4) {
            return Colours.YELLOW;
        } else if(stat < 8) {
            return Colours.ORANGE;
        } else if(stat < 15) {
            return Colours.RED;
        } else if(stat >= 15) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    private int getWSColour(int stat) {
        if(stat < 5){
            return Colours.GREY;
        } else if(stat < 10) {
            return Colours.WHITE;
        } else if(stat < 15) {
            return Colours.GREEN;
        } else if(stat < 20) {
            return Colours.AQUA;
        } else if(stat < 30) {
            return Colours.YELLOW;
        } else if(stat < 50) {
            return Colours.ORANGE;
        } else if(stat < 100) {
            return Colours.RED;
        } else if(stat >= 100) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    private void getBedwarsStats(String uuid) {

        final int[] stats = new int[9];

        String connection = Utils.URLS.getTextFromURL("https://api.hypixel.net/player?key=" + Utils.URLS.hypixelApiKey + "&uuid=" + uuid);

        if (connection.isEmpty()) {
            return;
        }

        if (connection.equals("{\"success\":true,\"player\":null}")) {

            stats[0] = -1;
            playerStats.put(uuid, stats);
        }

        JsonObject profile, bw, ach;
        try {
            profile = Utils.Java.getStringAsJson(connection).getAsJsonObject("player");
            bw = profile.getAsJsonObject("stats").getAsJsonObject("Bedwars");
            ach = profile.getAsJsonObject("achievements");
        } catch (NullPointerException er) {

            playerStats.put(uuid, stats);
            return;
        }

        stats[0] = Utils.Java.getValue(ach, "bedwars_level");
        stats[1] = stats[0] < 0 ? -1 : 0;
        stats[2] = Utils.Java.getValue(bw, "winstreak");
        stats[3] = Utils.Java.getValue(bw, "beds_broken_bedwars");
        stats[4] = Utils.Java.getValue(bw, "beds_lost_bedwars");
        stats[6] = Utils.Java.getValue(bw, "final_deaths_bedwars");
        stats[5] = Utils.Java.getValue(bw, "final_kills_bedwars");
        stats[7] = Utils.Java.getValue(bw, "wins_bedwars");
        stats[8] = Utils.Java.getValue(bw, "losses_bedwars");
        playerStats.put(uuid, stats);
    }

    private boolean drawError(ScaledResolution sr, FontRenderer fr) {
        String noApiKey = "API key is not set!";
        String noPlayers = "No players in lobby!";
        if(Utils.URLS.hypixelApiKey.isEmpty()){
            if(render)
                fr.drawString(noApiKey, (int)(overlayWidth + overlayX.getValue() - overlayWidth/2 - fr.getStringWidth(noApiKey)/2), (int)textY, errorColour);
            textY += fr.FONT_HEIGHT + marginTextY.getValue();
            return true;
        } else if(!Utils.Client.othersExist()){
            if(render)
                fr.drawString(noPlayers, (int)(overlayWidth + overlayX.getValue() - overlayWidth/2 - fr.getStringWidth(noPlayers)/2), (int)textY, errorColour);
            textY += fr.FONT_HEIGHT + marginTextY.getValue();
            return true;
        }

        return false;
    }

    private void drawMain(ScaledResolution sr, FontRenderer fr) {
        if(render)
            Gui.drawRect((int)overlayX.getValue(), (int)overlayY.getValue(), (int)(overlayWidth + overlayX.getValue()), (int)(overlayHeight + overlayY.getValue()), backgroundColour);

        double textX = margin.getValue() + overlayX.getValue();
        textY = margin.getValue() + overlayY.getValue();
        int stringWidth = 0;
        for(StatType statType : StatType.values()) {
            fr.drawString(statType + "", (int)textX, (int)textY, mainTextColour);
            statStart.put(statType, (int)textX);
            stringWidth = fr.getStringWidth(statType + "");
            textX += stringWidth + marginTextX.getValue();
        }

        textY += marginTextY.getValue() + fr.FONT_HEIGHT;
        if(render)
            overlayWidth = textX + margin.getValue() - (marginTextX.getValue()) - overlayX.getValue();
    }

    public static int getStarColour(int stat){
        if(stat < 20){
            return Colours.GREY;
        } else if(stat < 50) {
            return Colours.WHITE;
        } else if(stat < 150) {
            return Colours.GREEN;
        } else if(stat < 200) {
            return Colours.AQUA;
        } else if(stat < 400) {
            return Colours.YELLOW;
        } else if(stat < 500) {
            return Colours.ORANGE;
        } else if(stat < 1000) {
            return Colours.RED;
        } else if(stat >= 1000) {
            return Colours.PURPLE;
        }

        return Colours.PURPLE;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        render = true;
        active = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        render = false;
        active = false;
    }

    public enum StatType {
        LEVEL, NAME,
        NICKED, WS, BBBLR,
        FKDR, WLR, FINALS,
        WINS, OVERALLTHREAT
    }

    public static class Colours {
        public static final int GREY = 0xffAAAAAA, WHITE = 0xffffffff;
        public static final int GREEN = 0xff00AA00, AQUA = 0xff55FFFF;
        public static final int YELLOW = 0xffFFFF55, ORANGE = 0xffFFAA00;
        public static final int RED = 0xffAA0000, PURPLE = 0xffAA00AA;
    }

}