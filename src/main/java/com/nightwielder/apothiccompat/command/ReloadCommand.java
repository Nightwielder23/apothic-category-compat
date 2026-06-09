package com.nightwielder.apothiccompat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nightwielder.apothiccompat.config.ApothicCompatConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

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
                    ApothicCompatConfig.ReloadResult result = ApothicCompatConfig.reload();
                    ctx.getSource().sendSuccess(() -> Component.literal(result.message()), true);
                    return result.count();
                }));
    }
}
