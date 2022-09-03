package a.b.module.modules.combat;

import a.b.module.Module;
import a.b.module.setting.impl.DescriptionSetting;
import a.b.module.setting.impl.SliderSetting;
import a.b.module.setting.impl.TickSetting;
import a.b.utils.InvUtils;
import a.b.utils.RenderUtils;
import a.b.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {

    public static SliderSetting mode;
    public static DescriptionSetting modeMode;
    public static SliderSetting range;
    public static SliderSetting entityX;
    public static SliderSetting entityY;
    public static TickSetting mouse;
    public static TickSetting background;
    public static SliderSetting entitySize;
    public static TickSetting onlySprint;
    public static TickSetting onlySword;
    public static TickSetting swing;
    public static TickSetting drawEntity;
    public static TickSetting drawHUD;

    public KillAura() {
        super("KillAura", ModuleCategory.combat);
        this.registerSetting(mode = new SliderSetting("Mode", 1D, 1D, 3D, 1D));
        this.registerSetting(modeMode = new DescriptionSetting(Utils.md +""));
        this.registerSetting(range = new SliderSetting("Range", 3.7D, 2D, 8D, 0.1D));
        this.registerSetting(swing = new TickSetting("Swing", true));
        //    this.registerSetting(cps = new DoubleSliderSetting("CPS", 7D, 20D, 1D, 40D, 0.1D));
        this.registerSetting(drawEntity = new TickSetting("Draw Entity", false));
        this.registerSetting(drawHUD = new TickSetting("Draw HUD", true));
        this.registerSetting(entityX = new SliderSetting("Entity X", 80D, 20D, mc.displayWidth+50, 1D));
        this.registerSetting(entityY = new SliderSetting("Entity Y", 90D, 20D, mc.displayHeight+50, 1D));
        this.registerSetting(entitySize = new SliderSetting("Entity Size", 35D, 10D, 100D, 1D));
        this.registerSetting(background = new TickSetting("Background", true));
        this.registerSetting(mouse = new TickSetting("Mouse X and Y", false));
        this.registerSetting(onlySword = new TickSetting("OnlySword", true));
        this.registerSetting(onlySprint = new TickSetting("OnlySprint", false));
    }

    @SubscribeEvent
    public void r(TickEvent.PlayerTickEvent p) {
        if(!Utils.Player.isPlayerInGame()) return;

        List<Entity> targets = (List<Entity>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
        targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < (int)range.getInput() && entity != mc.thePlayer && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0).collect(Collectors.toList());
        targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
        if(targets.isEmpty()) return;
        EntityLivingBase target = (EntityLivingBase) targets.get(0);

        if(onlySword.isToggled())
            if(!InvUtils.isPlayerHoldingWeapon()) return;

        if(onlySprint.isToggled())
            if(!mc.thePlayer.isSprinting()) return;

        if(mode.getInput() == 1D) {
            //this is just targetHUD
        } else if(mode.getInput() == 2D) {
            Utils.Player.aim(target, 0.0F, false, false);
            Utils.Player.swing();
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        } else if(mode.getInput() == 3D) {
            Utils.Player.aim(target, 0.0F, false, true);
            Utils.Player.swing();
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        }
    }

    @SubscribeEvent
    public void p(TickEvent.RenderTickEvent e) {
        if(!Utils.Player.isPlayerInGame()) return;

        List<Entity> targets = (List<Entity>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
        targets = targets.stream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < (int)range.getInput() && entity != mc.thePlayer && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0).collect(Collectors.toList());
        targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
        if(targets.isEmpty()) return;
        EntityLivingBase target = (EntityLivingBase) targets.get(0);

        int x = (int) entityX.getInput();
        int y = (int) entityY.getInput();
        int rang = (int) range.getInput();
        int size = (int) entitySize.getInput();

        if(onlySword.isToggled())
            if(!InvUtils.isPlayerHoldingWeapon()) return;

        if(onlySprint.isToggled())
            if(!mc.thePlayer.isSprinting()) return;

        if(drawHUD.isToggled())
            RenderUtils.drawStringHUD(x, y, rang, background.isToggled());

        if(drawEntity.isToggled())
            RenderUtils.drawEntityHUD(target, x, y, size, rang, true, background.isToggled(), mouse.isToggled());
    }

    public void guiUpdate() {
        switch((int) mode.getInput()) {
            case 1:
                modeMode.setDesc(Utils.md + "HUD Only");
                break;
            case 2:
                modeMode.setDesc(Utils.md + "Legit");
                break;
            case 3:
                modeMode.setDesc(Utils.md + "Silent");
                break;
        }
    }

}