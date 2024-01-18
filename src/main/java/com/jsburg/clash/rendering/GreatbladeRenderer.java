package com.jsburg.clash.rendering;

import com.jsburg.clash.Clash;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class GreatbladeRenderer extends ItemStackTileEntityRenderer {
    private final GreatbladeModel model = new GreatbladeModel();
    public static final GreatbladeRenderer instance = new GreatbladeRenderer();

    private static int renderTicks = 1;

    public static GreatbladeRenderer getInstance() {
        return instance;
    }

    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        matrixStack.scale(1.0F, -1.0F, -1.0F);
        IVertexBuilder ivertexbuilder1 = ItemRenderer.getEntityGlintVertexBuilder(buffer, this.model.getRenderType(GreatbladeModel.TEXTURE_LOCATION), false, stack.hasEffect());
        renderTicks++;
//        matrixStack.rotate(Vector3f.ZP.rotation(((float)renderTicks)/20));
        this.model.render(matrixStack, ivertexbuilder1, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();

    }

    public static class GreatbladeModel extends Model {
        private final ModelRenderer renderer;
        public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(Clash.MOD_ID, "textures/entity/greatblade.png");

        public GreatbladeModel() {
            super(RenderType::getEntitySolid);
            textureWidth = 64;
            textureHeight = 64;

            renderer = new ModelRenderer(this);
            renderer.setRotationPoint(0.0F, 6F, 0.0F);
            renderer.setTextureOffset(4, 40).addBox(-1.25F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F, 0.0F, false);
            renderer.setTextureOffset(0, 34).addBox(-6.25F, -11.0F, -1.0F, 12.0F, 2.0F, 2.0F, 0.0F, false);
            renderer.setTextureOffset(0, 3).addBox(-4.75F, -39.0F, -0.5F, 9.0F, 28.0F, 1.0F, 0.0F, false);
        }

        @Override
        public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
            renderer.render(matrixStack, buffer, packedLight, packedOverlay);
        }

    }
}
