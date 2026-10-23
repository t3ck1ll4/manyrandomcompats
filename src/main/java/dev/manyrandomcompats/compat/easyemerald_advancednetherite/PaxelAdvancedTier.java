package dev.manyrandomcompats.compat.easyemerald_advancednetherite;

import com.autovw.advancednetherite.core.util.ModToolTiers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Tier de los paxels: igual al de Advanced Netherite, pero con +3% de durabilidad por cada nivel
 * (iron +4%, gold +8%, emerald +12%, diamond +16%).
 */
public class PaxelAdvancedTier implements Tier {
    private static final float DURABILITY_BONUS_PER_TIER = 0.04F;

    private final ModToolTiers anTier;
    private final int tierIndex; // 0 = iron, 1 = gold, 2 = emerald, 3 = diamond

    public PaxelAdvancedTier(ModToolTiers anTier, int tierIndex) {
        this.anTier = anTier;
        this.tierIndex = tierIndex;
    }

    @Override
    public int getUses() {
        return Math.round(anTier.getUses() * (1.0F + DURABILITY_BONUS_PER_TIER * (tierIndex + 1)));
    }

    @Override
    public float getSpeed() {
        return anTier.getSpeed();
    }

    @Override
    public float getAttackDamageBonus() {
        return anTier.getAttackDamageBonus();
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return anTier.getIncorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue() {
        return anTier.getEnchantmentValue();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return anTier.getRepairIngredient();
    }
}
