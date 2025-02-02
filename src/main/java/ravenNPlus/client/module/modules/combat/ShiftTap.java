package ravenNPlus.client.module.modules.combat;

import ravenNPlus.client.module.*;
import ravenNPlus.client.module.setting.impl.DescriptionSetting;
import ravenNPlus.client.module.setting.impl.DoubleSliderSetting;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.module.setting.impl.TickSetting;
import ravenNPlus.client.utils.CoolDown;
import ravenNPlus.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.util.concurrent.ThreadLocalRandom;

public class ShiftTap extends Module {
    public static SliderSetting range, eventType, chance;
    public static DescriptionSetting eventTypeDesc;
    public static TickSetting onlyPlayers;
    public static DoubleSliderSetting actionTicks, onceEvery, postDelay;
    public static boolean comboing, hitCoolDown, alreadyHit, waitingForPostDelay;
    public static int hitTimeout, hitsWaited;
    public static CoolDown actionTimer = new CoolDown(0), postDelayTimer = new CoolDown(0);

    public ShiftTap(){
        super("ShiftTap", ModuleCategory.combat, "");
        this.addSetting(onlyPlayers = new TickSetting("Only combo players", true));
        this.addSetting(actionTicks = new DoubleSliderSetting("Action Time (MS)",  25, 55, 1, 500, 1));
        this.addSetting(onceEvery =  new DoubleSliderSetting("Once every ... hits", 1, 1, 1, 10, 1));
        this.addSetting(postDelay =  new DoubleSliderSetting("Post delay (MS)", 25, 55, 1, 500, 1));
        this.addSetting(chance =  new SliderSetting("Chance %", 100, 0, 100, 1));
        this.addSetting(range = new SliderSetting("Range: ", 3, 1, 6, 0.05));
        this.addSetting(eventType = new SliderSetting("Value: ", 2, 1, 2, 1));
        this.addSetting(eventTypeDesc = new DescriptionSetting("Mode: POST"));
    }
    
    public void guiUpdate() {
        eventTypeDesc.setDesc(Utils.md + Utils.Modes.SprintResetTimings.values()[(int) eventType.getValue() - 1]);
    }


    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent e) {
        if(!Utils.Player.isPlayerInGame())
            return;

        if(waitingForPostDelay){
            if(postDelayTimer.hasFinished()){
                waitingForPostDelay = false;
                comboing = true;
                startCombo();
                actionTimer.start();
            }
            return;
        }

        if(comboing) {
            if(actionTimer.hasFinished()){
                comboing = false;
                finishCombo();
                return;
            }else {
                return;
            }
        }



        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof Entity && Mouse.isButtonDown(0)) {
            Entity target = mc.objectMouseOver.entityHit;
            if(target.isDead) {
                return;
            }

            if (mc.thePlayer.getDistanceToEntity(target) <= range.getValue()) {
                if ((target.hurtResistantTime >= 10 && Utils.Modes.SprintResetTimings.values()[(int) eventType.getValue() - 1] == Utils.Modes.SprintResetTimings.POST) || (target.hurtResistantTime <= 10 && Utils.Modes.SprintResetTimings.values()[(int) eventType.getValue() - 1] == Utils.Modes.SprintResetTimings.PRE)) {

                    if (onlyPlayers.isToggled()){
                        if (!(target instanceof EntityPlayer)){
                            return;
                        }
                    }

                    if(AntiBot.isBot(target)){
                        return;
                    }


                    if (hitCoolDown && !alreadyHit) {
                        hitsWaited++;
                        if(hitsWaited >= hitTimeout){
                            hitCoolDown = false;
                            hitsWaited = 0;
                        } else {
                            alreadyHit = true;
                            return;
                        }
                    }

                    if(!(chance.getValue() == 100 || Math.random() <= chance.getValue() / 100))
                        return;

                    if(!alreadyHit){
                        guiUpdate();
                        if(onceEvery.getInputMin() == onceEvery.getInputMax()) {
                            hitTimeout =  (int)onceEvery.getInputMin();
                        } else {

                            hitTimeout = ThreadLocalRandom.current().nextInt((int)onceEvery.getInputMin(), (int)onceEvery.getInputMax());
                        }
                        hitCoolDown = true;
                        hitsWaited = 0;

                        actionTimer.setCooldown((long)ThreadLocalRandom.current().nextDouble(actionTicks.getInputMin(),  actionTicks.getInputMax()+0.01));

                        if(postDelay.getInputMax() != 0){
                            postDelayTimer.setCooldown((long)ThreadLocalRandom.current().nextDouble(postDelay.getInputMin(),  postDelay.getInputMax()+0.01));
                            postDelayTimer.start();
                            waitingForPostDelay = true;
                        } else {
                            comboing = true;
                            startCombo();
                            actionTimer.start();
                            alreadyHit = true;
                        }
                        alreadyHit = true;
                    }
                } else {
                    if(alreadyHit){
                    }
                    alreadyHit = false;
                }
            }
        }
    }

    private static void finishCombo() {
        if(!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
    }

    private static void startCombo() {
        if(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            KeyBinding.onTick(mc.gameSettings.keyBindSneak.getKeyCode());
        }
    }

}