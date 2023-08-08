package code.frfo.pocs.demo.commands;

import code.frfole.pocs.channel.Channel;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PlayerChatMessagePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelCommand extends Command {
    public static Channel channelAlpha = Channel.newBuilder("[alpha]").build();
    public static Channel channelBravo = Channel.newBuilder("[bravo]").build();

    public ChannelCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);

        addSyntax((sender, context) -> {
            if (sender instanceof Player player && channelAlpha.isViewer(player)) {
                PlayerChatMessagePacket packet = channelAlpha.createMessagePacket(Component.text(String.join(" ", context.<String[]>get("message"))));
                player.sendPacket(packet);
            }
        }, new ArgumentLiteral("send"), new ArgumentLiteral("alpha"), new ArgumentStringArray("message"));
        addSyntax((sender, context) -> {
            if (sender instanceof Player player && channelBravo.isViewer(player)) {
                PlayerChatMessagePacket packet = channelBravo.createMessagePacket(Component.text(String.join(" ", context.<String[]>get("message"))));
                player.sendPacket(packet);
            }
        }, new ArgumentLiteral("send"), new ArgumentLiteral("bravo"), new ArgumentStringArray("message"));

        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                channelBravo.addViewer(player);
            }
        }, new ArgumentLiteral("init"), new ArgumentLiteral("bravo"));
        addSyntax((sender, context) -> {
            if (sender instanceof Player player) {
                channelBravo.removeViewer(player);
            }
        }, new ArgumentLiteral("deinit"), new ArgumentLiteral("bravo"));
    }
}
