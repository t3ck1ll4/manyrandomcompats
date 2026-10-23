package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Solo cliente: añade la capa del casco 3D a los jugadores y a las entidades con cabeza. */
public final class DragonLootClient {
    private DragonLootClient() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(DragonLootClient::onAddLayers);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // Jugadores (skin normal y slim)
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer renderer) {
                renderer.addLayer(new DragonHelmetTierLayer<AbstractClientPlayer, net.minecraft.client.model.PlayerModel<AbstractClientPlayer>>(renderer));
            }
        }
        // Soportes de armadura, zombis, esqueletos y demás entidades con cabeza
        for (EntityType<?> type : event.getEntityTypes()) {
            Object renderer = event.getRenderer((EntityType) type);
            if (renderer instanceof LivingEntityRenderer living && living.getModel() instanceof HeadedModel) {
                living.addLayer(new DragonHelmetTierLayer(living));
            }
        }
    }
}
