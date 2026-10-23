package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.common.item.AdvancedArmorItem;
import net.dragonloot.init.ConfigInit;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Armadura Dragon con tier de Advanced Netherite.
 * Hereda de AdvancedArmorItem: los perks de pacificación salen de las etiquetas
 * advancednetherite:pacify_*_armor. Además, si el perk de armadura de DragonLoot está activado en
 * su config (advanced_netherite_gear_perks_enabled), pacifica a los tres mobs, como la armadura Dragon.
 */
public class DragonAdvancedArmorItem extends AdvancedArmorItem {

    public DragonAdvancedArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    @Override
    public boolean pacifiesEndermen(ItemStack stack) {
        return super.pacifiesEndermen(stack) || ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled;
    }

    @Override
    public boolean pacifiesPiglins(ItemStack stack) {
        return super.pacifiesPiglins(stack) || ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled;
    }

    @Override
    public boolean pacifiesPhantoms(ItemStack stack) {
        return super.pacifiesPhantoms(stack) || ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled;
    }
}
