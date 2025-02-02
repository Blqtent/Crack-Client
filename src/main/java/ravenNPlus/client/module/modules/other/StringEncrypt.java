package ravenNPlus.client.module.modules.other;

import ravenNPlus.client.clickgui.RavenNPlus.ClickGui;
import ravenNPlus.client.module.Module;
import ravenNPlus.client.module.setting.impl.DescriptionSetting;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.module.setting.impl.TickSetting;
import ravenNPlus.client.utils.Utils;

public class StringEncrypt extends Module {
   private static final String m1 = "&k", m2 = "3 char", m3 = "Char shift", m4 = "Blank";
   private static int m3s = 1;
   private boolean m3t = false;
   public static TickSetting ignoreDebug, ignoreAllGui;
   public static SliderSetting value;
   public static DescriptionSetting moduleDesc;

   public StringEncrypt() {
      super("String Encrypt", ModuleCategory.other, "");
      this.addSetting(ignoreDebug = new TickSetting("Ignore debug", false));
      this.addSetting(ignoreAllGui = new TickSetting("Ignore all GUI", false));
      this.addSetting(value = new SliderSetting("Value", 1.0D, 1.0D, 4.0D, 1.0D));
      this.addSetting(moduleDesc = new DescriptionSetting(Utils.md + m1));
   }

   public void onEnable() {
      if (value.getValue() == 3.0D) {
         m3s = Utils.Java.rand().nextInt(10) - 5;
         if (m3s == 0) {
            m3s = 1;
         }
      }
   }

      public void guiUpdate() {
      switch((int) value.getValue()) {
      case 1:
         this.m3t = false;
         moduleDesc.setDesc(Utils.md + m1);
         break;
      case 2:
         this.m3t = false;
         moduleDesc.setDesc(Utils.md + m2);
         break;
      case 3:
         if (!this.m3t) {
            m3s = Utils.Java.rand().nextInt(10) - 5;
            if (m3s == 0) {
               m3s = 1;
            }
         }

         this.m3t = true;
         moduleDesc.setDesc(Utils.md + m3);
         break;
      case 4:
         this.m3t = false;
         moduleDesc.setDesc(Utils.md + m4);
      }
   }

   public static String getUnformattedTextForChat(String s) {
      if (mc.currentScreen instanceof ClickGui) {
         return s;
      } else if (ignoreDebug.isToggled() && mc.gameSettings.showDebugInfo) {
         return s;
      } else if (ignoreAllGui.isToggled() && mc.currentScreen != null) {
         return s;
      } else {
         StringBuilder s2;
         if (StringEncrypt.value.getValue() == 1.0D) {
            s2 = new StringBuilder();
            StringBuilder s3 = new StringBuilder();
            boolean w = false;

            for(int i = 0; i < s.length(); ++i) {
               String c = Character.toString(s.charAt(i));
               if (c.equals("§")) {
                  w = true;
                  s3.append(c);
               } else if (w) {
                  w = false;
                  s3.append(c);
               } else {
                  s2.append(s3).append("§").append("k").append(c);
                  s3 = new StringBuilder();
               }
            }

            return s2.toString();
         } else if (StringEncrypt.value.getValue() == 2.0D) {
            return s.length() > 3 ? s.substring(0, 3) : s;
         } else if (StringEncrypt.value.getValue() != 3.0D) {
            return "";
         } else {
            s2 = new StringBuilder();

            for(int i = 0; i < s.length(); ++i) {
               char c = (char)(s.charAt(i) + m3s);
               s2.append(c);
            }

            return s2.toString();
         }
      }
   }

}