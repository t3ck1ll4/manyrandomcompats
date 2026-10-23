package dev.manyrandomcompats.compat.easyemerald_advancednetherite;

import com.autovw.advancednetherite.core.ModItems;
import com.autovw.advancednetherite.core.util.ModToolTiers;
import dev.manyrandomcompats.ManyRandomCompats;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Módulo: Easy Emerald Tools + Advanced Netherite.
 * Esta clase solo se carga si ambos mods están presentes (ver ManyRandomCompats).
 */
public final class EasyEmeraldAdvancedNetheriteCompat {
    private EasyEmeraldAdvancedNetheriteCompat() {}

    /**
     * Fuerza la inicialización de ModItems de Advanced Netherite antes de tocar ModToolTiers.
     * Ver la nota completa en DragonLootAdvancedNetheriteCompat: ModToolTiers <-> ModItems es
     * una dependencia circular en AN 2.3.1, y ModItems debe inicializarse primero.
     */
    private static final Item FORCE_ADVANCED_NETHERITE_INIT = ModItems.NETHERITE_IRON_INGOT;

    // Daño base igual al de las hachas de Advanced Netherite de cada tier (5 / 6 / 6 / 7).
    public static final DeferredItem<Item> NETHERITE_IRON_PAXEL = ManyRandomCompats.ITEMS.register("netherite_iron_paxel",
            () -> new AdvancedPaxelItem(ModToolTiers.NETHERITE_IRON, 0, 5.0F, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_GOLD_PAXEL = ManyRandomCompats.ITEMS.register("netherite_gold_paxel",
            () -> new AdvancedPaxelItem(ModToolTiers.NETHERITE_GOLD, 1, 6.0F, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_EMERALD_PAXEL = ManyRandomCompats.ITEMS.register("netherite_emerald_paxel",
            () -> new AdvancedPaxelItem(ModToolTiers.NETHERITE_EMERALD, 2, 6.0F, new Item.Properties()));
    public static final DeferredItem<Item> NETHERITE_DIAMOND_PAXEL = ManyRandomCompats.ITEMS.register("netherite_diamond_paxel",
            () -> new AdvancedPaxelItem(ModToolTiers.NETHERITE_DIAMOND, 3, 7.0F, new Item.Properties()));

    public static void init(IEventBus modEventBus) {
        // Al llamar a este método ya se cargó la clase, así que los items de arriba quedan registrados.
        // El loot modifier de minerales solo existe cuando este módulo está activo.
        ManyRandomCompats.LOOT_MODIFIERS.register("paxel_ore_drops", () -> PaxelOreDropsLootModifier.CODEC);
        modEventBus.addListener(EasyEmeraldAdvancedNetheriteCompat::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(NETHERITE_IRON_PAXEL.get());
            event.accept(NETHERITE_GOLD_PAXEL.get());
            event.accept(NETHERITE_EMERALD_PAXEL.get());
            event.accept(NETHERITE_DIAMOND_PAXEL.get());
        }
    }
}
