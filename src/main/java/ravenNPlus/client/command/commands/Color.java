package ravenNPlus.client.command.commands;

import ravenNPlus.client.clickgui.RavenNPlus.CommandPrompt;
import ravenNPlus.client.command.Command;

public class Color extends Command {
    public Color() {
        super("color", "change color of the Command Prompt", 1,1, new String[] {}, new String[] {"title", ""});
    }

    @Override
    public void onCall(String[] args) {
        CommandPrompt.titleColor = 0xff088000;
    }

}