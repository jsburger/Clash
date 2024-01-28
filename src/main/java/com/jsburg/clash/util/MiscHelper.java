package com.jsburg.clash.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import static net.minecraft.util.Mth.DEG_TO_RAD;

public class MiscHelper {

    public static InteractionHand getHandFromSide(Player player, HumanoidArm side) {
        if (player.getMainArm() == HumanoidArm.LEFT) {
            return side == HumanoidArm.LEFT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        }
        return side == HumanoidArm.LEFT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public static Vec3 extractHorizontal(Vec3 vector) {
        if (vector.y * vector.y == vector.lengthSqr()) return Vec3.ZERO;
        Vec3 n = new Vec3(vector.x, 0, vector.z);
        return n.normalize();
    }

    public static float dr(float degrees) {
        return degrees * DEG_TO_RAD;
    }
    public static void rotate(PoseStack stack, float xDeg, float yDeg, float zDeg) {
        stack.mulPose(new Quaternionf().rotateXYZ(dr(xDeg), dr(yDeg), dr(zDeg)));
    }
    public static void rotateX(PoseStack stack, float deg) {
        stack.mulPose(new Quaternionf().rotateX(dr(deg)));
    }
    public static void rotateY(PoseStack stack, float deg) {
        stack.mulPose(new Quaternionf().rotateY(dr(deg)));
    }
    public static void rotateZ(PoseStack stack, float deg) {
        stack.mulPose(new Quaternionf().rotateZ(dr(deg)));
    }

//    public static <T extends LivingEntity, M extends EntityModel<T> & ArmedModel> void offsetItemForThirdPerson(M model, HumanoidArm handSide, PoseStack stack) {
//        model.translateToHand(handSide, stack);
//        stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
//        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//        boolean flag = handSide == HumanoidArm.LEFT;
//        stack.translate((flag ? -1 : 1) / 16.0F, 0.125D, -0.625D);
//
//    }
}
