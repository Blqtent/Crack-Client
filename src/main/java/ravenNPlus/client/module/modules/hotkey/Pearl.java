package ravenNPlus.client.module.modules.hotkey;

import ravenNPlus.client.module.Module;
import ravenNPlus.client.module.setting.impl.SliderSetting;
import ravenNPlus.client.module.setting.impl.TickSetting;
import ravenNPlus.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class Pearl extends Module {
    private final TickSetting preferSlot;
    private final SliderSetting hotbarSlotPreference;
    public static ArrayList<KeyBinding> changedKeybinds = new ArrayList<>();
    public Pearl() {
        super("Pearl", ModuleCategory.hotkey, "");

        this.addSetting(preferSlot = new TickSetting("Prefer a slot", false));
        this.addSetting(hotbarSlotPreference = new SliderSetting("Prefer wich slot", 6, 1, 9, 1));
    }

    public static boolean checkSlot(int slot) {
        ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);

        return itemInSlot != null && itemInSlot.getDisplayName().equalsIgnoreCase("ender pearl");
    }

    @Override
    public void onEnable(){
        if (!Utils.Player.isPlayerInGame()){
            return;
        }

        if (preferSlot.isToggled()) {
            int preferedSlot = (int) hotbarSlotPreference.getValue() - 1;

            if(checkSlot(preferedSlot)) {
                mc.thePlayer.inventory.currentItem = preferedSlot;
                this.disable();
                return;
            }
        }

        for (int slot = 0; slot <= 8; slot++) {
            if (checkSlot(slot)) {
                mc.thePlayer.inventory.currentItem = slot;
                this.disable();
                return;
            }
        }
        this.disable();
    }

}