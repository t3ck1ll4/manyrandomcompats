package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.common.item.AdvancedPickaxeItem;
import com.autovw.advancednetherite.config.ConfigHelper;
import com.autovw.advancednetherite.core.util.ModTags;
import com.autovw.advancednetherite.core.util.ModTooltips;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/** Herramienta Dragon con tier de Advanced Netherite. Hereda de AdvancedPickaxeItem para activar sus perks. */
public class DragonAdvancedPickaxeItem extends AdvancedPickaxeItem {
    private final DragonAdvancedTier dragonTier;

    public DragonAdvancedPickaxeItem(DragonAdvancedTier tier, Item.Properties properties) {
        super(tier, properties);
        this.dragonTier = tier;
    }

    // Advanced Netherite identifica el tier por el valor de su enum (velocidad, color de la barra...),
    // así que devolvemos ese enum aunque la durabilidad y el daño vengan del tier combinado.
    @Override
    public Tier getTier() {
        return dragonTier != null ? dragonTier.getAdvancedTier() : super.getTier();
    }

    // Advanced Netherite solo muestra los perks en los items de su propio mod, así que los añadimos aquí.
    // (Los perks en sí funcionan por las etiquetas advancednetherite:drops_additional_*.)
    @Override
    public void addTooltips(ItemStack stack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag flag) {
        if (!ConfigHelper.get().getCommon().getAdditionalDrops().enableAdditionalOreDrops()) {
            return;
        }
        boolean iron = stack.is(ModTags.DROPS_ADDITIONAL_IRON);
        boolean gold = stack.is(ModTags.DROPS_ADDITIONAL_GOLD);
        boolean emerald = stack.is(ModTags.DROPS_ADDITIONAL_EMERALD);
        boolean diamond = stack.is(ModTags.DROPS_ADDITIONAL_DIAMOND);
        if (!(iron || gold || emerald || diamond)) {
            return;
        }
        if (Screen.hasShiftDown()) {
            if (iron) tooltips.add(ModTooltips.IRON_ORE_DROP_TOOLTIP);
            if (gold) tooltips.add(ModTooltips.GOLD_ORE_DROP_TOOLTIP);
            if (emerald) tooltips.add(ModTooltips.EMERALD_ORE_DROP_TOOLTIP);
            if (diamond) tooltips.add(ModTooltips.DIAMOND_ORE_DROP_TOOLTIP);
        } else {
            tooltips.add(ModTooltips.SHIFT_KEY_TOOLTIP);
        }
    }
}
