package com.ganhamosgames.whispercompass;

import java.util.function.Function;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

/** Registro de todos os itens do mod. */
public final class ModItems {
    private ModItems() {}

    public static final Item WHISPER_COMPASS = register("whisper_compass", WhisperCompassItem::new,
            new Item.Settings()
                    .maxDamage(120)                         // 120 pontos de durabilidade (não empilha)
                    .repairable(Items.AMETHYST_SHARD)       // conserta na bigorna com cacos de ametista
                    .rarity(Rarity.UNCOMMON)
                    .component(ModComponents.TARGET_MODE, 0) // começa procurando carvão
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)); // brilho mágico

    /** No 1.21.4 todo item precisa ter a chave de registro definida nas Settings antes de ser criado. */
    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(WhisperCompassMod.MOD_ID, name));
        return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
    }

    public static void register() {
        // Coloca a bússola na aba "Ferramentas e utilitários" do modo criativo.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(WHISPER_COMPASS));
    }
}
