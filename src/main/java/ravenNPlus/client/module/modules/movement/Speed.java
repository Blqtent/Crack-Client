package ravenNPlus.client.module.modules.movement;

import ravenNPlus.client.module.Module;
import ravenNPlus.client.module.setting.impl.DescriptionSetting;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.module.setting.impl.TickSetting;
import ravenNPlus.client.utils.Utils;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {

   public static DescriptionSetting desc;
   public static SliderSetting speed;
   public static TickSetting strafe;

   public Speed() {
      super("Speed", ModuleCategory.movement, "Modifies your world speed");
      this.addSetting(desc = new DescriptionSetting("Hypixel max: 1.13"));
      this.addSetting(speed = new SliderSetting("Speed", 1.2D, 1.0D, 2.5D, 0.01D));
      this.addSetting(strafe = new TickSetting("Strafe only", false));
   }

   public void update() {
      double csp = Utils.Player.pythagorasMovement();
      if (csp != 0.0D) {
         if (mc.thePlayer.onGround && !mc.thePlayer.capabilities.isFlying) {
            if (!strafe.isToggled() || mc.thePlayer.moveStrafing != 0.0F) {
               if (mc.thePlayer.hurtTime != mc.thePlayer.maxHurtTime || mc.thePlayer.maxHurtTime <= 0) {
                  if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                     double val = speed.getValue() - (speed.getValue() - 1.0D) * 0.5D;
                     Utils.Player.fixMovementSpeed(csp * val, true);
                  }
               }
            }
         }
      }
   }

}