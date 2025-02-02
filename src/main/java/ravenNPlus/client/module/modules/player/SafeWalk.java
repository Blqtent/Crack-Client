package ravenNPlus.client.module.modules.player;

import ravenNPlus.client.main.Client;
import ravenNPlus.client.module.*;
import ravenNPlus.client.module.setting.impl.DescriptionSetting;
import ravenNPlus.client.module.setting.impl.DoubleSliderSetting;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.module.setting.impl.TickSetting;
import ravenNPlus.client.utils.CoolDown;
import ravenNPlus.client.utils.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.lwjgl.input.Keyboard;
import java.awt.*;

public class SafeWalk extends Module {

   public static TickSetting doShift;
   public static TickSetting blocksOnly;
   public static TickSetting shiftOnJump;
   public static TickSetting onHold;
   public static TickSetting showBlockAmount;
   public static TickSetting lookDown;
   public static DoubleSliderSetting pitchRange;
   public static SliderSetting blockShowMode;
   public static DescriptionSetting blockShowModeDesc;
   public static DoubleSliderSetting shiftTime;
   private static boolean shouldBridge;
   private static boolean isShifting;
   private boolean allowedShift;
   private final CoolDown shiftTimer = new CoolDown(0);

   public SafeWalk() {
      super("SafeWalk", ModuleCategory.player, "Dont fall of blocks (Fix without shift soon)");
      this.addSetting(doShift = new TickSetting("Shift", false));
      this.addSetting(shiftOnJump = new TickSetting("Shift during jumps", false));
      this.addSetting(shiftTime = new DoubleSliderSetting("Shift time: (s)", 140, 200, 0, 280, 5));
      this.addSetting(onHold = new TickSetting("On shift hold", false));
      this.addSetting(blocksOnly = new TickSetting("Blocks only", true));
      this.addSetting(showBlockAmount = new TickSetting("Show amount of blocks", true));
      this.addSetting(blockShowMode = new SliderSetting("Block display info:", 2D, 1D, 2D, 1D));
      this.addSetting(blockShowModeDesc = new DescriptionSetting("Mode: "));
      this.addSetting(lookDown = new TickSetting("Only when looking down", true));
      this.addSetting(pitchRange = new DoubleSliderSetting("Pitch min range:", 70D, 85, 0D, 90D, 1D));
   }

   public void onDisable() {
      if (doShift.isToggled() && Utils.Player.playerOverAir(1)) {
         this.setShift(false);
      }

      shouldBridge = false;
      isShifting = false;
   }

   public void guiUpdate() {
      blockShowModeDesc.setDesc(Utils.md + BlockAmountInfo.values()[(int) blockShowMode.getValue() - 1]);
   }

   @SubscribeEvent
   public void p(PlayerTickEvent e) {
      if (!Utils.Client.currentScreenMinecraft())
         return;

      if (!Utils.Player.isPlayerInGame()) {
         return;
      }

      boolean shiftTimeSettingActive = shiftTime.getInputMax() > 0;

      if (doShift.isToggled()) {
         if (lookDown.isToggled()) {
            if (mc.thePlayer.rotationPitch < pitchRange.getInputMin()
                    || mc.thePlayer.rotationPitch > pitchRange.getInputMax()) {
               shouldBridge = false;
               if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                  setShift(true);
               }
               return;
            }
         }
         if (onHold.isToggled()) {
            if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
               shouldBridge = false;
               return;
            }
         }

         if (blocksOnly.isToggled()) {
            ItemStack i = mc.thePlayer.getHeldItem();
            if (i == null || !(i.getItem() instanceof ItemBlock)) {
               if (isShifting) {
                  isShifting = false;
                  this.setShift(false);
               }

               return;
            }
         }

         if (mc.thePlayer.onGround) {
            if (Utils.Player.playerOverAir(1)) {
               if (shiftTimeSettingActive) { // making sure that the player has set the value so some number
                  shiftTimer.setCooldown(
                          Utils.Java.randomInt(shiftTime.getInputMin(), shiftTime.getInputMax() + 0.1));
                  shiftTimer.start();
               }

               isShifting = true;
               this.setShift(true);
               shouldBridge = true;
            } else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())
                    && onHold.isToggled()) { // if player is sneaking and shiftDown and holdSetting turned on
               isShifting = false;
               shouldBridge = false;
               this.setShift(false);
            } else if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) { // if
               isShifting = false;
               shouldBridge = false;
               this.setShift(false);
            } else if (mc.thePlayer.isSneaking()
                    && (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled())
                    && (!shiftTimeSettingActive || shiftTimer.hasFinished())) {
               isShifting = false;
               this.setShift(false);
               shouldBridge = true;
            } else if (mc.thePlayer.isSneaking() && !onHold.isToggled()
                    && (!shiftTimeSettingActive || shiftTimer.hasFinished())) {
               isShifting = false;
               this.setShift(false);
               shouldBridge = true;
            }
         } else if (shouldBridge && mc.thePlayer.capabilities.isFlying) {
            this.setShift(false);
            shouldBridge = false;
         } else if (shouldBridge && Utils.Player.playerOverAir(1) && shiftOnJump.isToggled()) {
            isShifting = true;
            this.setShift(true);
         } else {
            // rn we are in the air, and we are not flying, meaning that we are in a jump.
            // and since shiftOnJump is turned off, we just un-shift and uhh...
            isShifting = false;
            this.setShift(false);
         }
      }
   }

   @SubscribeEvent
   public void r(TickEvent.RenderTickEvent e) {
      if (!showBlockAmount.isToggled() || !Utils.Player.isPlayerInGame())
         return;
      if (mc.currentScreen == null) {
         if (shouldBridge) {
            ScaledResolution res = new ScaledResolution(mc);

            int totalBlocks = 0;
            if (BlockAmountInfo.values()[(int) blockShowMode.getValue()
                    - 1] == BlockAmountInfo.BLOCKS_IN_CURRENT_STACK) {
               totalBlocks = Utils.Player.getBlockAmountInCurrentStack(mc.thePlayer.inventory.currentItem);
            } else {
               for (int slot = 0; slot < 36; slot++) {
                  totalBlocks += Utils.Player.getBlockAmountInCurrentStack(slot);
               }
            }

            if (totalBlocks <= 0) {
               return;
            }

            int rgb;
            if (totalBlocks < 16.0D) {
               rgb = Color.red.getRGB();
            } else if (totalBlocks < 32.0D) {
               rgb = Color.orange.getRGB();
            } else if (totalBlocks < 128.0D) {
               rgb = Color.yellow.getRGB();
            } else if (totalBlocks > 128.0D) {
               rgb = Color.green.getRGB();
            } else {
               rgb = Color.black.getRGB();
            }

            String t;
            if(totalBlocks == 1)
               t = totalBlocks + " block";
            else
               t = totalBlocks + " blocks";

            int x = res.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(t) / 2;
            int y;
            if (Client.debugger) {
               y = res.getScaledHeight() / 2 + 17 + mc.fontRendererObj.FONT_HEIGHT;
            } else {
               y = res.getScaledHeight() / 2 + 15;
            }
            mc.fontRendererObj.drawString(t, (float) x, (float) y, rgb, false);
         }
      }
   }

   private void setShift(boolean sh) {
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sh);
   }

   public enum BlockAmountInfo {
      BLOCKS_IN_TOTAL, BLOCKS_IN_CURRENT_STACK
   }

}