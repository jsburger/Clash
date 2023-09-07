package com.jsburg.clash.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class MiscHelper {

    public static Hand getHandFromSide(PlayerEntity player, HandSide side) {
        if (player.getPrimaryHand() == HandSide.LEFT) {
            return side == HandSide.LEFT ? Hand.MAIN_HAND : Hand.OFF_HAND;
        }
        return side == HandSide.LEFT ? Hand.OFF_HAND : Hand.MAIN_HAND;
    }

    public static Vector3d extractHorizontal(Vector3d vector) {
        if (vector.y * vector.y == vector.lengthSquared()) return Vector3d.ZERO;
        Vector3d n = new Vector3d(vector.x, 0, vector.z);
        return n.normalize();
    }

    public static <T extends LivingEntity, M extends EntityModel<T> & IHasArm> void offsetItemForThirdPerson(M model, HandSide handSide, MatrixStack stack) {
        model.translateHand(handSide, stack);
        stack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
        stack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        boolean flag = handSide == HandSide.LEFT;
        stack.translate((flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);

    }
}
