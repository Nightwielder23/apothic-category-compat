package com.nightwielder.apothiccompat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

public final class ReloadCommand {
    private ReloadCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> root = dispatcher.register(buildRoot("apothiccompat"));
        dispatcher.register(Commands.literal("ac").requires(src -> src.hasPermission(2)).redirect(root));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> buildRoot(String literal) {
        return Commands.literal(literal)
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("reload").executes(ctx -> {
                    if (!ModList.get().isLoaded("apotheosis")) {
                        ctx.getSource().sendFailure(Component.literal(
                                "Apotheosis is not loaded; nothing to apply."));
                        return 0;
                    }
                    int count = ApothicCompatConfig.reload();
                    ctx.getSource().sendSuccess(() -> Component.literal("Config reloaded."), true);
                    return count;
                }));
    }
}
