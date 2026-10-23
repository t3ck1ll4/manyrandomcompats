package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.common.item.AdvancedShovelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/** Herramienta Dragon con tier de Advanced Netherite. Hereda de AdvancedShovelItem para activar sus perks. */
public class DragonAdvancedShovelItem extends AdvancedShovelItem {
    private final DragonAdvancedTier dragonTier;

    public DragonAdvancedShovelItem(DragonAdvancedTier tier, Item.Properties properties) {
        super(tier, properties);
        this.dragonTier = tier;
    }

    // Advanced Netherite identifica el tier por el valor de su enum (velocidad, color de la barra...),
    // así que devolvemos ese enum aunque la durabilidad y el daño vengan del tier combinado.
    @Override
    public Tier getTier() {
        return dragonTier != null ? dragonTier.getAdvancedTier() : super.getTier();
    }
}
