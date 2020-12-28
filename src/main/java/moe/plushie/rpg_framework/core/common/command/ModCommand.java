package moe.plushie.rpg_framework.core.common.command;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class ModCommand extends CommandBase {

    private final ModCommand parent;
    private final String name;

    public ModCommand(ModCommand parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public int getParentCount() {
        if (parent != null) {
            return parent.getParentCount() + 1;
        }
        return 0;
    }

    @Override
    public String getUsage(ICommandSender commandSender) {
        return "commands." + LibModInfo.ID + ":" + getFullName() + ".usage";
    }
    
    public String getFullName() {
        if (parent != null) {
            return parent.getFullName() + "." + name;
        }
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    protected String[] getPlayers(MinecraftServer server) {
        return server.getOnlinePlayerNames();
    }
    
    public String[] mergeArgs(String[] args) {
        args = mergeArgs(args, "\"");
        args = mergeArgs(args, "'");
        return args;
    }
    
    public String[] mergeArgs(String[] args, String mergeChar) {
        ArrayList<String> argsList = new ArrayList<String>();
        boolean inMergeChar = false;
        String buildArg = "";
        
        for (int i = 0; i < args.length; i++) {

            if (!inMergeChar) {
                if (args[i].startsWith(mergeChar)) {
                    inMergeChar = true;
                    buildArg += args[i];
                    if (args[i].endsWith(mergeChar)) {
                        inMergeChar = false;
                        buildArg = buildArg.substring(1, buildArg.length() - 1);
                        argsList.add(buildArg);
                        buildArg = "";
                    }
                    continue;
                } else {
                    argsList.add(args[i]);
                }
            }

            if (inMergeChar) {
                if (args[i].endsWith(mergeChar)) {
                    inMergeChar = false;
                    buildArg += " " + args[i];
                    buildArg = buildArg.substring(1, buildArg.length() - 1);
                    argsList.add(buildArg);
                    buildArg = "";
                } else {
                    buildArg += " " + args[i];
                }
            }
        }

        return argsList.toArray(new String[argsList.size()]);
    }
}
