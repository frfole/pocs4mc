package code.frfo.pocs.demo;

import code.frfo.pocs.demo.commands.ChannelCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;

public class Main {

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer spawnInstance = MinecraftServer.getInstanceManager().createInstanceContainer();
        spawnInstance.setGenerator(unit -> unit.modifier().fillHeight(-16, 0, Block.STONE));

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(spawnInstance);
            ChannelCommand.channelAlpha.addViewer(event.getPlayer());
        });

        MinecraftServer.getCommandManager().register(new ChannelCommand("channel", "ch"));

        server.start("localhost", 25565);
    }
}
