package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.core.util.ModToolTiers;
import net.dragonloot.init.ConfigInit;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Material de las herramientas Dragon con un tier de Advanced Netherite.
 * Parte de los valores de DragonLoot (según su config) y le suma la diferencia
 * que tiene cada tier de Advanced Netherite respecto a la netherita normal.
 */
public class DragonAdvancedTier implements Tier {
    // Valores de la netherita vanilla, para calcular la diferencia
    private static final int NETHERITE_USES = 2031;
    private static final float NETHERITE_DAMAGE_BONUS = 4.0F;

    /** Durabilidad extra por cada nivel de tier: iron +4%, gold +8%, emerald +12%, diamond +16%. */
    private static final float DURABILITY_BONUS_PER_TIER = 0.04F;

    private final ModToolTiers anTier;
    private final int tierIndex; // 0 = iron, 1 = gold, 2 = emerald, 3 = diamond

    public DragonAdvancedTier(ModToolTiers anTier, int tierIndex) {
        this.anTier = anTier;
        this.tierIndex = tierIndex;
    }

    public ModToolTiers getAdvancedTier() {
        return anTier;
    }

    @Override
    public int getUses() {
        int multiplier = ConfigInit.CONFIG.dragon_item_durability_multiplier;
        if (ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled) {
            multiplier = Math.max(multiplier, 47); // mismo mínimo que usa DragonLoot
        }
        int base = 67 * multiplier + (anTier.getUses() - NETHERITE_USES);
        return Math.round(base * (1.0F + DURABILITY_BONUS_PER_TIER * (tierIndex + 1)));
    }

    // Se deja la velocidad de Advanced Netherite (1.0): su multiplicador por tier se aplica aparte.
    @Override
    public float getSpeed() {
        return anTier.getSpeed();
    }

    @Override
    public float getAttackDamageBonus() {
        return ConfigInit.CONFIG.dragon_item_base_damage + (anTier.getAttackDamageBonus() - NETHERITE_DAMAGE_BONUS);
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return anTier.getIncorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue() {
        return Math.max(anTier.getEnchantmentValue(), ConfigInit.CONFIG.dragon_tool_enchantability);
    }

    @Override
    public Ingredient getRepairIngredient() {
        return anTier.getRepairIngredient();
    }
}
