package dev.manyrandomcompats.compat.easyemerald_advancednetherite;

import com.autovw.advancednetherite.config.ConfigHelper;
import com.autovw.advancednetherite.core.util.ModTags;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Copia de la lógica de OreDropsLootModifier de Advanced Netherite, pero para el paxel.
 * (El original solo funciona si la herramienta es un AdvancedPickaxeItem.)
 * Usa las mismas etiquetas, la misma config y la misma exclusión de Toque de seda.
 */
public class PaxelOreDropsLootModifier extends LootModifier {
    public static final MapCodec<PaxelOreDropsLootModifier> CODEC = RecordCodecBuilder.mapCodec(
            instance -> codecStart(instance).apply(instance, PaxelOreDropsLootModifier::new));

    public PaxelOreDropsLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (tool == null || state == null) {
            return generatedLoot;
        }
        if (!ConfigHelper.get().getCommon().getAdditionalDrops().enableAdditionalOreDrops()) {
            return generatedLoot;
        }
        if (!(tool.getItem() instanceof AdvancedPaxelItem)
                || EnchantmentHelper.hasTag(tool, ModTags.PREVENTS_ADDITIONAL_ORE_DROPS)) {
            return generatedLoot;
        }

        Block block = state.getBlock();
        RandomSource random = context.getRandom();
        var drops = ConfigHelper.get().getServer().getAdditionalDropProperties();

        if ((block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE)
                && tool.is(ModTags.DROPS_ADDITIONAL_IRON)
                && (double) random.nextFloat() <= drops.getAdditionalRawIronDropChance()) {
            generatedLoot.add(new ItemStack(Items.RAW_IRON, random.nextIntBetweenInclusive(1, 2)));
        }
        if ((block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE)
                && tool.is(ModTags.DROPS_ADDITIONAL_GOLD)
                && (double) random.nextFloat() <= drops.getAdditionalRawGoldDropChance()) {
            generatedLoot.add(new ItemStack(Items.RAW_GOLD, 1));
        }
        if ((block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE)
                && tool.is(ModTags.DROPS_ADDITIONAL_EMERALD)
                && (double) random.nextFloat() <= drops.getAdditionalEmeraldDropChance()) {
            generatedLoot.add(new ItemStack(Items.EMERALD, 1));
        }
        if ((block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE)
                && tool.is(ModTags.DROPS_ADDITIONAL_DIAMOND)
                && (double) random.nextFloat() <= drops.getAdditionalDiamondDropChance()) {
            generatedLoot.add(new ItemStack(Items.DIAMOND, 1));
        }
        if (block == Blocks.NETHER_GOLD_ORE
                && tool.is(ModTags.DROPS_ADDITIONAL_GOLD)
                && (double) random.nextFloat() <= drops.getAdditionalGoldNuggetDropChance()) {
            generatedLoot.add(new ItemStack(Items.GOLD_NUGGET, random.nextIntBetweenInclusive(1, 3)));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
