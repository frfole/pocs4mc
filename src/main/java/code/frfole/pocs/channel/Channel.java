package code.frfole.pocs.channel;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.minestom.server.Viewable;
import net.minestom.server.crypto.FilterMask;
import net.minestom.server.crypto.LastSeenMessages;
import net.minestom.server.crypto.SignedMessageBody;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.message.Messenger;
import net.minestom.server.network.packet.server.play.PlayerChatMessagePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a channel that can be toggled on the client via social interaction screen.
 * <p>
 * It works by sending messages as a player, whose messages the client can hide.
 */
public class Channel implements Viewable, Identified {
    private final UUID id;
    private final Set<Player> viewers = new CopyOnWriteArraySet<>();
    private final PlayerInfoUpdatePacket addPacket;
    private final PlayerInfoRemovePacket removePacket;
    private final int chatType;

    /**
     * Creates a new channel with given name and UUID.
     * @param name the name
     * @param chatType the type of the chat, if the chat type is not registered {@link IllegalStateException} is thrown
     * @param id the UUID
     */
    private Channel(@NotNull String name, @NotNull String chatType, @NotNull UUID id) {
        this.id = id;

        addPacket = new PlayerInfoUpdatePacket(
                EnumSet.of(
                        PlayerInfoUpdatePacket.Action.ADD_PLAYER,
                        PlayerInfoUpdatePacket.Action.UPDATE_LISTED
                ),
                List.of(
                        new PlayerInfoUpdatePacket.Entry(id, name, List.of(), false, 0, GameMode.CREATIVE, Component.text(name), null),
                        new PlayerInfoUpdatePacket.Entry(id, name, List.of(), false, 0, GameMode.CREATIVE, Component.text(name), null)
                )
        );
        removePacket = new PlayerInfoRemovePacket(id);

        int chatId = -1;
        NBTList<NBTCompound> chatTypes = Messenger.chatRegistry().getList("value");
        assert chatTypes != null;
        for (NBTCompound type : chatTypes) {
            if (chatType.equals(type.getString("name"))) {
                Integer chatId2 = type.getInt("id");
                assert chatId2 != null;
                chatId = chatId2;
                break;
            }
        }
        if (chatId == -1) {
            throw new IllegalStateException("Chat type not registered");
        }
        this.chatType = chatId;
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        if (viewers.add(player)) {
            player.sendPacket(addPacket);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        if (viewers.remove(player)) {
            player.sendPacket(removePacket);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Set<@NotNull Player> getViewers() {
        return viewers;
    }

    @Override
    public @NotNull Identity identity() {
        return Identity.identity(id);
    }

    /**
     * Creates a new {@link PlayerChatMessagePacket} with given text and defined by the channel.
     * If the player is not viewer of this {@link Channel}, he will probably disconnect itself.
     * @param text the text to display
     * @return the packet
     */
    public PlayerChatMessagePacket createMessagePacket(@NotNull Component text) {
        return new PlayerChatMessagePacket(
                this.identity().uuid(),
                0,
                null,
                new SignedMessageBody.Packed("hi", Instant.now(), 0, LastSeenMessages.Packed.EMPTY),
                text,
                new FilterMask(FilterMask.Type.PASS_THROUGH, BitSet.valueOf(new byte[]{0})),
                chatType,
                Component.empty(),
                null
        );
    }

    /**
     * Creates a new {@link Builder} for {@link Channel} with given name.
     * @param name the name
     * @return the builder
     */
    public static Builder newBuilder(@NotNull String name) {
        return new Builder(name);
    }

    /**
     * Builder for {@link Channel}
     * @param name the channel name
     * @param chatType the channel chat type
     * @param id the channel UUID
     */
    public record Builder(@NotNull String name, @NotNull String chatType, @NotNull UUID id) {

        public Builder(@NotNull String name) {
            this(name, "minecraft:chat", UUID.randomUUID());
        }

        /**
         * Sets the {@link #name}.
         * @param name the name
         * @return a new copy of the {@link Builder} with given name
         */
        public Builder name(@NotNull String name) {
            return new Builder(name, chatType, id);
        }

        /**
         * Sets the {@link #chatType}.
         * @param chatType the chat type
         * @return a new copy of the {@link Builder} with given chat type
         */
        public Builder chatType(@NotNull String chatType) {
            return new Builder(name, chatType, id);
        }

        /**
         * Sets the {@link #id}.
         * @param id the UUID
         * @return a new copy of the {@link Builder} with given UUID
         */
        public Builder id(@NotNull UUID id) {
            return new Builder(name, chatType, id);
        }

        /**
         * Builds a new {@link Channel} with information provided by the {@link Builder}.
         * <p>
         * If the chat type is not registered {@link IllegalStateException} is thrown.
         * @return a new {@link Channel}
         */
        public Channel build() {
            return new Channel(name, chatType, id);
        }
    }
}
