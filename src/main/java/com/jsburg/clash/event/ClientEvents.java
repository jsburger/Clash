package com.jsburg.clash.event;

import com.jsburg.clash.Clash;
import com.jsburg.clash.particle.SpearCritParticle;
import com.jsburg.clash.particle.SpearStabParticle;
import com.jsburg.clash.registry.AllParticles;
import com.jsburg.clash.weapons.SpearItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particles;
        manager.registerFactory(AllParticles.SPEAR_STAB.get(), SpearStabParticle.Factory::new);
        manager.registerFactory(AllParticles.SPEAR_CRIT.get(), SpearCritParticle.Factory::new);
    }

    public static void fiddleWithHands(RenderHandEvent event) {
        if (event.getItemStack().getItem() instanceof SpearItem) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            if (player != null && event.getHand() == player.getActiveHand() && player.isHandActive()) {
                SpearItem spear = (SpearItem) event.getItemStack().getItem();
                int useCount = player.getItemInUseCount();
                int useTime = spear.getUseDuration(event.getItemStack()) - useCount;
                float chargePercent = (float) Math.pow(Math.min((useTime + event.getPartialTicks())/spear.getMaxCharge(event.getItemStack()), 1), 1);
                int sideFlip = player.getPrimaryHand() == HandSide.LEFT ^ player.getActiveHand() == Hand.OFF_HAND ? -1 : 1;

                float xAngle = -6 * chargePercent;
                float yAngle = 0 * chargePercent;
                float zAngle = 20 * chargePercent * sideFlip;
//                if (useTime > 20) {
//                    zAngle += 10;
//                    yAngle += 3;
//                    xAngle -= 10;
//                    event.getMatrixStack().translate(.1, 0, 0);
//                }
                event.getMatrixStack().rotate(new Quaternion(xAngle, yAngle, zAngle, true));
            }
        }
    }

}
