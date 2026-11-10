package com.ganhamosgames.whispercompass;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;

/**
 * Minérios que a bússola sabe farejar.
 *
 * Usa as tags vanilla (coal_ores, iron_ores...), então minérios de outros mods
 * que seguem essas tags também funcionam. O {@code cost} é a durabilidade gasta
 * por escaneamento: quanto mais raro o minério, mais a bússola se desgasta.
 */
public enum OreTarget {
    COAL("coal", state -> state.isIn(BlockTags.COAL_ORES), 0x9A9A9A, 1),
    COPPER("copper", state -> state.isIn(BlockTags.COPPER_ORES), 0xE77C56, 1),
    IRON("iron", state -> state.isIn(BlockTags.IRON_ORES), 0xD8AF93, 1),
    GOLD("gold", state -> state.isIn(BlockTags.GOLD_ORES), 0xFDF55F, 2),
    REDSTONE("redstone", state -> state.isIn(BlockTags.REDSTONE_ORES), 0xFF4A3D, 2),
    LAPIS("lapis", state -> state.isIn(BlockTags.LAPIS_ORES), 0x5A7BFF, 2),
    DIAMOND("diamond", state -> state.isIn(BlockTags.DIAMOND_ORES), 0x4AEDD9, 3),
    EMERALD("emerald", state -> state.isIn(BlockTags.EMERALD_ORES), 0x17DD62, 3),
    ANCIENT_DEBRIS("ancient_debris", state -> state.isOf(Blocks.ANCIENT_DEBRIS), 0xB5806B, 4);

    private static final OreTarget[] VALUES = values();

    private final String key;
    private final Predicate<BlockState> matcher;
    private final int color;
    private final int cost;

    OreTarget(String key, Predicate<BlockState> matcher, int color, int cost) {
        this.key = key;
        this.matcher = matcher;
        this.color = color;
        this.cost = cost;
    }

    public boolean matches(BlockState state) {
        return matcher.test(state);
    }

    public int color() {
        return color;
    }

    /** Durabilidade gasta por escaneamento bem-sucedido. */
    public int cost() {
        return cost;
    }

    /** Chave de tradução, ex.: target.whispercompass.iron */
    public String translationKey() {
        return "target." + WhisperCompassMod.MOD_ID + "." + key;
    }

    /** Próximo alvo da lista (volta ao primeiro depois do último). */
    public OreTarget next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    /** Converte o índice guardado no item, com proteção contra valores inválidos. */
    public static OreTarget byIndex(int index) {
        if (index < 0 || index >= VALUES.length) {
            return VALUES[0];
        }
        return VALUES[index];
    }
}
