package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.common.item.AdvancedHoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/**
 * Herramienta Dragon con tier de Advanced Netherite. Hereda de AdvancedHoeItem para activar sus perks.
 * Los tooltips de perks ya los muestra AdvancedHoeItem (no hace falta añadirlos aquí).
 */
public class DragonAdvancedHoeItem extends AdvancedHoeItem {
    private final DragonAdvancedTier dragonTier;

    public DragonAdvancedHoeItem(DragonAdvancedTier tier, Item.Properties properties) {
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
