package dev.manyrandomcompats;

import com.mojang.serialization.MapCodec;
import dev.manyrandomcompats.compat.dragonloot_advancednetherite.DragonLootAdvancedNetheriteCompat;
import dev.manyrandomcompats.compat.easyemerald_advancednetherite.EasyEmeraldAdvancedNetheriteCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Mod de compatibilidades. Ningún otro mod es dependencia obligatoria:
 * cada módulo de compatibilidad solo se activa si TODOS sus mods están instalados.
 *
 * REGLA IMPORTANTE: esta clase (y cualquier clase que se cargue siempre) NO puede
 * importar clases de otros mods. Todo lo que las use va dentro del paquete de su módulo,
 * que solo se carga cuando el módulo se activa.
 */
@Mod(ManyRandomCompats.MODID)
public class ManyRandomCompats {
    public static final String MODID = "manyrandomcompats";

    // Registros compartidos por todos los módulos (los módulos añaden sus items aquí)
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final net.neoforged.neoforge.registries.DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
            CREATIVE_TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.manyrandomcompats.main"))
                    .icon(() -> new ItemStack(getItem("netherite_diamond_paxel", Items.NETHERITE_PICKAXE)))
                    .displayItems((parameters, output) -> addCreativeItems(output))
                    .build());
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);

    public ManyRandomCompats(IEventBus modEventBus) {
        // --- Módulos de compatibilidad (uno por combinación de mods) ---
        if (isLoaded("easy_emerald") && isLoaded("advancednetherite")) {
            EasyEmeraldAdvancedNetheriteCompat.init(modEventBus);
        }
        if (isLoaded("dragonloot") && isLoaded("advancednetherite")) {
            DragonLootAdvancedNetheriteCompat.init(modEventBus);
        }
        // Aquí se añadirán los módulos futuros, por ejemplo:
        // if (isLoaded("otro_mod") && isLoaded("easy_emerald")) { OtroCompat.init(modEventBus); }

        CREATIVE_TABS.register(modEventBus);
        ITEMS.register(modEventBus);
        LOOT_MODIFIERS.register(modEventBus);
    }


    private static Item getItem(String path, Item fallback) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, path));
        return item == Items.AIR ? fallback : item;
    }

    private static void addCreativeItems(CreativeModeTab.Output output) {
        // Orden solicitado: cada tipo completo y, dentro de él, diamante, esmeralda, oro, hierro.
        String[] tiers = {"diamond", "emerald", "gold", "iron"};
        String[] armor = {"helmet", "chestplate", "leggings", "boots"};
        for (String piece : armor) {
            for (String tier : tiers) {
                acceptIfRegistered(output, "netherite_" + tier + "_dragon_" + piece);
            }
        }
        String[] tools = {"sword", "pickaxe", "axe", "hoe"};
        for (String tool : tools) {
            for (String tier : tiers) {
                acceptIfRegistered(output, "netherite_" + tier + "_dragon_" + tool);
            }
        }
        for (String tier : tiers) {
            acceptIfRegistered(output, "netherite_" + tier + "_paxel");
        }
    }

    private static void acceptIfRegistered(CreativeModeTab.Output output, String path) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, path));
        if (item != Items.AIR) output.accept(item);
    }

    private static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
