package dev.manyrandomcompats.compat.dragonloot_advancednetherite;

import com.autovw.advancednetherite.core.ModItems;
import com.autovw.advancednetherite.core.util.ModToolTiers;
import dev.manyrandomcompats.ManyRandomCompats;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.dragonloot.init.ConfigInit;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Módulo: DragonLoot + Advanced Netherite.
 * Crea, para cada tier de Advanced Netherite (iron, gold, emerald, diamond), una versión Dragon de:
 * casco, peto, pantalones, botas, pico, hacha, pala, azada y espada.
 * (No incluye arco, ballesta, tridente, peto alado ni armadura de caballo.)
 * Esta clase solo se carga si DragonLoot y Advanced Netherite están instalados.
 */
public final class DragonLootAdvancedNetheriteCompat {
    private DragonLootAdvancedNetheriteCompat() {}

    /**
     * Fuerza la inicialización estática de ModItems de Advanced Netherite en el orden correcto.
     *
     * Advanced Netherite 2.3.1 tiene una dependencia circular de inicialización estática entre
     * ModToolTiers <-> ModItems (y ModArmorMaterials <-> ModItems). Si ModToolTiers se inicializa
     * PRIMERO, ModItems intenta construir sus herramientas y lee un Tier (ModToolTiers) que todavía
     * es null, lanzando ExceptionInInitializerError en DiggerItem.createAttributes ("getAttackDamageBonus()
     * porque p_330688_ es null").
     *
     * Al leer primero un campo estático de ModItems (el lingote, que se declara antes que las
     * herramientas), ModItems se inicializa en el orden seguro (lingotes -> armaduras -> herramientas),
     * y con ello ModToolTiers y ModArmorMaterials quedan ya inicializados de forma válida antes de
     * que este módulo los utilice.
     */
    private static final Item FORCE_ADVANCED_NETHERITE_INIT = ModItems.NETHERITE_IRON_INGOT;

    /** Un tier de Advanced Netherite: nombre, enum de AN, multiplicador de durabilidad de armadura y sus valores. */
    /** Daño extra de la espada Dragon por tier (iron, gold, emerald, diamond) sobre la espada Dragon normal. */
    private static final int[] SWORD_DAMAGE_BONUS = {1, 2, 3, 5};

    public enum Tier {
        // Importante: el tier de Advanced Netherite se referencia de forma "lazy" (Supplier), NO directa.
        // Referenciar ModToolTiers.NETHERITE_IRON aquí de forma directa fuerza la inicialización estática
        // de ModToolTiers antes que la de ModItems. Advanced Netherite 2.3.1 tiene una dependencia circular
        // entre ambas (ModToolTiers -> ModItems -> ModToolTiers), lo que provoca un NullPointerException en
        // DiggerItem.createAttributes (el Tier llega null). Con el Supplier, ModToolTiers solo se resuelve
        // cuando se registran los items (durante RegisterEvent), cuando ModItems ya está inicializado.
        IRON("iron", () -> ModToolTiers.NETHERITE_IRON, 39, 2, 0, 0, 2, 1.0F),
        GOLD("gold", () -> ModToolTiers.NETHERITE_GOLD, 41, 2, 2, 2, 2, 2.0F),
        EMERALD("emerald", () -> ModToolTiers.NETHERITE_EMERALD, 43, 2, 2, 2, 2, 3.0F),
        DIAMOND("diamond", () -> ModToolTiers.NETHERITE_DIAMOND, 47, 4, 2, 2, 4, 4.0F);

        public final String id;
        public final Supplier<ModToolTiers> tools;
        public final int armorDurabilityMultiplier;
        // Puntos que se suman a la armadura Dragon de DragonLoot (casco, peto, pantalones, botas) y a su tenacidad
        final int helmet, chest, legs, boots;
        final float toughness;

        Tier(String id, Supplier<ModToolTiers> tools, int armorDurabilityMultiplier, int helmet, int chest, int legs, int boots, float toughness) {
            this.id = id;
            this.tools = tools;
            this.armorDurabilityMultiplier = armorDurabilityMultiplier;
            this.helmet = helmet;
            this.chest = chest;
            this.legs = legs;
            this.boots = boots;
            this.toughness = toughness;
        }
    }

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, ManyRandomCompats.MODID);

    private static final Map<Tier, DeferredHolder<ArmorMaterial, ArmorMaterial>> MATERIALS = new EnumMap<>(Tier.class);
    private static final List<DeferredItem<Item>> ALL_ITEMS = new ArrayList<>();

    static {
        for (Tier tier : Tier.values()) {
            MATERIALS.put(tier, ARMOR_MATERIALS.register("netherite_" + tier.id + "_dragon", () -> createMaterial(tier)));
            registerArmor(tier, ArmorItem.Type.HELMET, "helmet");
            registerArmor(tier, ArmorItem.Type.CHESTPLATE, "chestplate");
            registerArmor(tier, ArmorItem.Type.LEGGINGS, "leggings");
            registerArmor(tier, ArmorItem.Type.BOOTS, "boots");
            registerTools(tier);
        }
    }

    private static ArmorMaterial createMaterial(Tier tier) {
        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, ConfigInit.CONFIG.dragon_armor_protection_helmet + tier.helmet);
        defense.put(ArmorItem.Type.CHESTPLATE, ConfigInit.CONFIG.dragon_armor_protection_chest + tier.chest);
        defense.put(ArmorItem.Type.LEGGINGS, ConfigInit.CONFIG.dragon_armor_protection_leggings + tier.legs);
        defense.put(ArmorItem.Type.BOOTS, ConfigInit.CONFIG.dragon_armor_protection_boots + tier.boots);
        defense.put(ArmorItem.Type.BODY, ConfigInit.CONFIG.dragon_armor_protection_horse); // no se usa (sin armadura de caballo)
        float toughness = ConfigInit.CONFIG.dragon_armor_toughness + tier.toughness;
        int enchantability = Math.max(ConfigInit.CONFIG.dragon_armor_enchantability, tier.tools.get().getEnchantmentValue());
        return new ArmorMaterial(
                defense,
                enchantability,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> tier.tools.get().getRepairIngredient(),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ManyRandomCompats.MODID, "netherite_" + tier.id + "_dragon"))),
                toughness,
                ConfigInit.CONFIG.dragon_armor_knockback_resistance);
    }

    private static int armorDurabilityMultiplier(Tier tier) {
        int multiplier = ConfigInit.CONFIG.dragon_armor_durability_multiplier;
        if (ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled) {
            multiplier = Math.max(multiplier, 47); // mismo mínimo que usa DragonLoot
        }
        return multiplier + (tier.armorDurabilityMultiplier - 37); // 37 = multiplicador de la netherita vanilla
    }

    private static void registerArmor(Tier tier, ArmorItem.Type type, String piece) {
        DeferredItem<Item> item = ManyRandomCompats.ITEMS.register("netherite_" + tier.id + "_dragon_" + piece,
                () -> new DragonAdvancedArmorItem(MATERIALS.get(tier), type,
                        new Item.Properties().fireResistant().durability(type.getDurability(armorDurabilityMultiplier(tier)))));
        ALL_ITEMS.add(item);
    }

    private static void registerTools(Tier tier) {
        String p = "netherite_" + tier.id + "_dragon_";
        ALL_ITEMS.add(ManyRandomCompats.ITEMS.register(p + "pickaxe", () -> {
            DragonAdvancedTier t = new DragonAdvancedTier(tier.tools.get(), tier.ordinal());
            return new DragonAdvancedPickaxeItem(t, new Item.Properties().fireResistant()
                    .attributes(PickaxeItem.createAttributes(t, 1.0F, -2.8F)));
        }));
        ALL_ITEMS.add(ManyRandomCompats.ITEMS.register(p + "axe", () -> {
            DragonAdvancedTier t = new DragonAdvancedTier(tier.tools.get(), tier.ordinal());
            float damage = ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled ? 8.0F : 5.0F; // igual que DragonLoot
            return new DragonAdvancedAxeItem(t, new Item.Properties().fireResistant()
                    .attributes(AxeItem.createAttributes(t, damage, -3.0F)));
        }));
        ALL_ITEMS.add(ManyRandomCompats.ITEMS.register(p + "shovel", () -> {
            DragonAdvancedTier t = new DragonAdvancedTier(tier.tools.get(), tier.ordinal());
            return new DragonAdvancedShovelItem(t, new Item.Properties().fireResistant()
                    .attributes(ShovelItem.createAttributes(t, 1.5F, -3.0F)));
        }));
        ALL_ITEMS.add(ManyRandomCompats.ITEMS.register(p + "hoe", () -> {
            DragonAdvancedTier t = new DragonAdvancedTier(tier.tools.get(), tier.ordinal());
            return new DragonAdvancedHoeItem(t, new Item.Properties().fireResistant()
                    .attributes(HoeItem.createAttributes(t, -4.0F, 0.0F)));
        }));
        ALL_ITEMS.add(ManyRandomCompats.ITEMS.register(p + "sword", () -> {
            DragonAdvancedTier t = new DragonAdvancedTier(tier.tools.get(), tier.ordinal());
            float damage = ConfigInit.CONFIG.advanced_netherite_gear_perks_enabled ? 6.0F : 3.0F; // igual que DragonLoot
            // Daño de la espada sobre la espada Dragon (9 -> 10 / 11 / 12 / 14 con la config por defecto): ver SWORD_DAMAGE_BONUS.
            // El tier ya aporta una diferencia propia (la de Advanced Netherite), así que se compensa para que
            // el total sea exactamente "daño Dragon + (tier + 1)".
            float tierBonusInTier = t.getAttackDamageBonus() - ConfigInit.CONFIG.dragon_item_base_damage;
            float swordExtra = SWORD_DAMAGE_BONUS[tier.ordinal()] - tierBonusInTier;
            return new DragonAdvancedSwordItem(t, new Item.Properties().fireResistant()
                    .attributes(SwordItem.createAttributes(t, damage + swordExtra, -2.4F)));
        }));
    }

    public static void init(IEventBus modEventBus) {
        // Al llegar aquí la clase ya se cargó (bloque static) y los items quedaron anotados en ITEMS.
        ARMOR_MATERIALS.register(modEventBus);
        // Casco 3D de DragonLoot para cada tier (solo en el cliente)
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            DragonLootClient.register(modEventBus);
        }
        modEventBus.addListener(DragonLootAdvancedNetheriteCompat::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT || event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (DeferredItem<Item> item : ALL_ITEMS) {
                event.accept(item.get());
            }
        }
    }
}
