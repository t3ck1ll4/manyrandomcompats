package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.manyrandomcompats.ManyRandomCompats;
import net.dragonloot.entity.model.DragonHelmetModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Dibuja el casco 3D de DragonLoot (con cuernos) en los cascos de cada tier, con la textura propia del tier:
 * assets/manyrandomcompats/textures/entity/netherite_<tier>_dragon_helmet_3d.png
 * Reutiliza el modelo de DragonLoot (DragonHelmetModel) y copia la posición y escala que usa su casco.
 */
public class DragonHelmetTierLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    private static final String HELMET_SUFFIX = "_dragon_helmet";

    private final DragonHelmetModel helmetModel = new DragonHelmetModel(DragonHelmetModel.createLayerDefinition().bakeRoot());

    public DragonHelmetTierLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    /** Textura 3D del casco si el item es uno de los cascos de tier; si no, null. */
    private static ResourceLocation textureFor(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!ManyRandomCompats.MODID.equals(key.getNamespace()) || !key.getPath().endsWith(HELMET_SUFFIX)) {
            return null;
        }
        return ResourceLocation.fromNamespaceAndPath(ManyRandomCompats.MODID, "textures/entity/" + key.getPath() + "_3d.png");
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation texture = textureFor(stack);
        if (texture == null) {
            return;
        }
        poseStack.pushPose();
        this.getParentModel().getHead().translateAndRotate(poseStack);
        poseStack.translate(0.0, -1.75, 0.0);
        poseStack.scale(1.19F, 1.19F, 1.19F);
        VertexConsumer consumer = ItemRenderer.getArmorFoilBuffer(buffer, this.helmetModel.renderType(texture), stack.hasFoil());
        this.helmetModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }
}
