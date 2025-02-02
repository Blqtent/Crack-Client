package ravenNPlus.client.module.modules.other;

import ravenNPlus.client.module.Module;
import ravenNPlus.client.module.setting.impl.DescriptionSetting;
import ravenNPlus.client.module.modules.minigames.DuelsStats;
import ravenNPlus.client.utils.Utils;

public class NameHider extends Module {
   public static DescriptionSetting a;
   public static String n = "ravenb+";

   public NameHider() {
      super("Name Hider", ModuleCategory.other, "Not working");
      this.addSetting(a = new DescriptionSetting(Utils.Java.capitalizeWord("command") + ": cname [name]"));
   }

   public static String getUnformattedTextForChat(String s) {
      if (mc.thePlayer != null) {
         s = DuelsStats.playerNick.isEmpty() ? s.replace(mc.thePlayer.getName(), n) : s.replace(DuelsStats.playerNick, n);
      }

      return s;
   }
}
