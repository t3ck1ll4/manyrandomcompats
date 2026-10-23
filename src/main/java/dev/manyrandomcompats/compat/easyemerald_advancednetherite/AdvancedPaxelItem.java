package dev.manyrandomcompats.compat.easyemerald_advancednetherite;

import com.autovw.advancednetherite.common.AdvancedUtil;
import com.autovw.advancednetherite.config.ConfigHelper;
import com.autovw.advancednetherite.core.util.ModTags;
import com.autovw.advancednetherite.core.util.ModToolTiers;
import com.autovw.advancednetherite.core.util.ModTooltips;
import com.kwpugh.easy_emerald.init.TagInit;
import com.kwpugh.easy_emerald.items.tools.ModPaxel;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Paxel de Easy Emerald Tools (ModPaxel) con un tier de Advanced Netherite.
 * - Hereda de ModPaxel: pico + hacha + pala, pelar/raspar/quitar cera, caminos, apagar fogatas.
 * - Usa ModToolTiers (durabilidad, daño, encantabilidad y reparación de Advanced Netherite).
 * - Aplica el multiplicador de velocidad de minado de Advanced Netherite (su config).
 */
public class AdvancedPaxelItem extends ModPaxel {

    private final ModToolTiers anTier;

    public AdvancedPaxelItem(ModToolTiers tier, int tierIndex, float attackDamage, Item.Properties properties) {
        super(new PaxelAdvancedTier(tier, tierIndex), TagInit.PAXEL_MINEABLE,
                properties.fireResistant().attributes(ModPaxel.createAttributes(tier, attackDamage, -3.0F)));
        this.anTier = tier;
    }

    // Advanced Netherite identifica el tier por el valor de su enum (velocidad, color de la barra...),
    // así que devolvemos ese enum aunque la durabilidad venga del tier con el bonus del 3% por nivel.
    @Override
    public Tier getTier() {
        return anTier != null ? anTier : super.getTier();
    }

    // Advanced Netherite define velocidad 1.0 en sus tiers y la multiplica según su config.
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return AdvancedUtil.getDestroySpeed(super.getDestroySpeed(stack, state), stack, state);
    }

    // Barra de durabilidad del color del tier (si está activo en la config de Advanced Netherite).
    @Override
    public int getBarColor(ItemStack stack) {
        return AdvancedUtil.getDurabilityBarColor(super.getBarColor(stack), stack);
    }

    // Mismo comportamiento que los picos de Advanced Netherite: aviso de Shift y, al pulsarlo,
    // los perks con su "+" verde (se usan los textos y estilos de Advanced Netherite).
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (!ConfigHelper.get().getClient().showTooltips()
                || !ConfigHelper.get().getCommon().getAdditionalDrops().enableAdditionalOreDrops()) {
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
            if (iron) tooltip.add(ModTooltips.IRON_ORE_DROP_TOOLTIP);
            if (gold) tooltip.add(ModTooltips.GOLD_ORE_DROP_TOOLTIP);
            if (emerald) tooltip.add(ModTooltips.EMERALD_ORE_DROP_TOOLTIP);
            if (diamond) tooltip.add(ModTooltips.DIAMOND_ORE_DROP_TOOLTIP);
        } else {
            tooltip.add(ModTooltips.SHIFT_KEY_TOOLTIP);
        }
    }
}
