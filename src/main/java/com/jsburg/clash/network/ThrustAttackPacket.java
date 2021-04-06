package com.jsburg.clash.network;

import com.jsburg.clash.weapons.SpearItem;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ThrustAttackPacket {
    private final UUID id;
    private final int chargeTime;

    public ThrustAttackPacket(UUID uuid, int chargeTimeIn) {
        this.id = uuid;
        this.chargeTime = chargeTimeIn;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeUniqueId(this.id);
        buffer.writeInt(this.chargeTime);
    }

    public static ThrustAttackPacket decode(PacketBuffer buffer) {
        return new ThrustAttackPacket(buffer.readUniqueId(), buffer.readInt());
    }

    public static void handle(ThrustAttackPacket packet, Supplier<NetworkEvent.Context> con) {
//        NetworkEvent.Context context = con.get();
//        context.enqueueWork(() -> {
//            for (ServerWorld world : context.getSender().server.getWorlds()) {
//                Entity target = packet.getEntity(world);
//                if (target != null) {
//                    SpearItem.doSpearAttack(context.getSender(), target, packet.chargeTime);
//                    break;
//                }
//            }
//        });
    }

    private Entity getEntity(ServerWorld worldIn) {
        return worldIn.getEntityByUuid(this.id);
    }

}
