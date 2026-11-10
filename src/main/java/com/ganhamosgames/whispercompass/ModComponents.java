package com.ganhamosgames.whispercompass;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Componentes de dados customizados (a forma moderna de guardar dados num item, no lugar de NBT).
 */
public final class ModComponents {
    private ModComponents() {}

    /** Índice do minério que a bússola está procurando (posição em {@link OreTarget}). */
    public static final ComponentType<Integer> TARGET_MODE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(WhisperCompassMod.MOD_ID, "target_mode"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .packetCodec(PacketCodecs.VAR_INT) // enviado ao cliente para o tooltip
                    .build());

    /** Chamar este método força o carregamento da classe (e portanto o registro acima). */
    public static void register() {}
}
