package com.ganhamosgames.whispercompass;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * A Bússola Sussurrante.
 *
 * <ul>
 *   <li><b>Usar</b>: escaneia um cubo ao redor do jogador e mostra, na barra de ação, a direção
 *       (relativa para onde o jogador olha) e a distância do minério escolhido mais próximo.</li>
 *   <li><b>Agachado + usar</b>: troca o minério procurado.</li>
 * </ul>
 *
 * Equilíbrio: alcance curto ({@link #RADIUS}), recarga ({@link #COOLDOWN_TICKS}) e a durabilidade
 * gasta cresce com a raridade do minério. Ela só avisa que existe algo por perto; o jogador ainda
 * precisa cavar até lá.
 */
public class WhisperCompassItem extends Item {
    /** Alcance do escaneamento, em blocos, em cada direção (cubo de 25x25x25). */
    public static final int RADIUS = 12;
    /** Recarga entre usos (30 ticks = 1,5 segundo). */
    public static final int COOLDOWN_TICKS = 30;

    private static final String[] ARROWS = {"↑", "↗", "→", "↘", "↓", "↙", "←", "↖"};

    public WhisperCompassItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Toda a lógica roda só no servidor; o cliente apenas balança a mão.
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
            return ActionResult.SUCCESS;
        }

        ItemStack stack = user.getStackInHand(hand);
        OreTarget target = getTarget(stack);

        // Agachado: trocar de minério.
        if (user.isSneaking()) {
            OreTarget next = target.next();
            stack.set(ModComponents.TARGET_MODE, next.ordinal());
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 0.8f, 1.4f);
            user.sendMessage(Text.translatable("message.whispercompass.mode", oreName(next)), true);
            return ActionResult.SUCCESS;
        }

        user.getItemCooldownManager().set(stack, COOLDOWN_TICKS);
        BlockPos found = findNearest(world, user.getBlockPos(), target);

        if (found == null) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(),
                    SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.PLAYERS, 0.7f, 0.6f);
            user.sendMessage(Text.translatable("message.whispercompass.none", oreName(target), RADIUS), true);
            // Escanear em vão custa só 1 de durabilidade, para não dar para "spammar" de graça.
            damage(stack, user, hand, 1);
            return ActionResult.SUCCESS;
        }

        double dx = found.getX() + 0.5 - user.getX();
        double dy = found.getY() - user.getBlockPos().getY();
        double dz = found.getZ() + 0.5 - user.getZ();
        int distance = (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));

        user.sendMessage(Text.translatable("message.whispercompass.found",
                oreName(target), arrowFor(user, dx, dz), distance, verticalHint((int) dy)), true);

        // Quanto mais perto, mais agudo o som.
        float closeness = 1.0f - (float) Math.min(distance, RADIUS * 2) / (RADIUS * 2);
        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.0f, 0.7f + closeness * 1.1f);

        // Rastro de partículas apontando na direção do minério.
        Vec3d from = user.getEyePos();
        Vec3d dir = Vec3d.ofCenter(found).subtract(from).normalize();
        for (int i = 0; i < 8; i++) {
            Vec3d p = from.add(dir.multiply(1.2 + i * 0.5));
            serverWorld.spawnParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // Gasta a durabilidade por último: se a bússola quebrar, o item some e nada mais usa o stack.
        damage(stack, user, hand, target.cost());
        return ActionResult.SUCCESS;
    }

    /** Procura o bloco do alvo mais próximo dentro do cubo de {@link #RADIUS}. */
    private static BlockPos findNearest(World world, BlockPos origin, OreTarget target) {
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                int worldX = origin.getX() + x;
                int worldZ = origin.getZ() + z;
                // Nunca força o carregamento de chunks: ignora colunas fora da área carregada.
                if (!world.isChunkLoaded(worldX >> 4, worldZ >> 4)) {
                    continue;
                }
                for (int y = -RADIUS; y <= RADIUS; y++) {
                    int worldY = origin.getY() + y;
                    if (world.isOutOfHeightLimit(worldY)) {
                        continue;
                    }
                    cursor.set(worldX, worldY, worldZ);
                    if (target.matches(world.getBlockState(cursor))) {
                        double d = x * x + y * y + z * z;
                        if (d < bestDistance) {
                            bestDistance = d;
                            best = cursor.toImmutable();
                        }
                    }
                }
            }
        }
        return best;
    }

    /** Seta relativa a para onde o jogador está olhando (↑ = à frente). */
    private static String arrowFor(PlayerEntity player, double dx, double dz) {
        double targetYaw = Math.toDegrees(Math.atan2(-dx, dz));
        double relative = MathHelper.wrapDegrees(targetYaw - player.getYaw());
        int sector = Math.floorMod((int) Math.round(relative / 45.0), 8);
        return ARROWS[sector];
    }

    private static String verticalHint(int dy) {
        if (dy >= 2) return " ▲" + dy;
        if (dy <= -2) return " ▼" + (-dy);
        return "";
    }

    private static MutableText oreName(OreTarget target) {
        int color = target.color();
        return Text.translatable(target.translationKey()).styled(style -> style.withColor(color));
    }

    private static void damage(ItemStack stack, PlayerEntity user, Hand hand, int amount) {
        stack.damage(amount, user, LivingEntity.getSlotForHand(hand));
    }

    public static OreTarget getTarget(ItemStack stack) {
        return OreTarget.byIndex(stack.getOrDefault(ModComponents.TARGET_MODE, 0));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.whispercompass.mode", oreName(getTarget(stack))));
        tooltip.add(Text.translatable("tooltip.whispercompass.use", RADIUS).formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.whispercompass.cycle").formatted(Formatting.DARK_GRAY));
    }
}
