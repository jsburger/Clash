package com.jsburg.clash.network;

import com.jsburg.clash.Clash;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

//NOTE; none of this is used.
public class ClashPacketHandler {
    //Used to determine if the client and server are handling packets the same way
    private static final String CLASH_PACKET_VERSION = "1";
    //The channel itself, I think
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            //Channel location
            new ResourceLocation(Clash.MOD_ID, "main"),
            //Getting the protocol version to check
            () -> CLASH_PACKET_VERSION,
            //Check to see if the client version has an acceptable version (simply being equal)
            CLASH_PACKET_VERSION::equals,
            //Check to see if the server version is acceptable
            CLASH_PACKET_VERSION::equals
    );

    public static void registerPackets() {
        int id = 0;
        INSTANCE.registerMessage(id++, ThrustAttackPacket.class, ThrustAttackPacket::encode, ThrustAttackPacket::decode, ThrustAttackPacket::handle);
    }

}
