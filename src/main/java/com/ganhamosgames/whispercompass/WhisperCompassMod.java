package com.ganhamosgames.whispercompass;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Whispering Compass (Bússola Sussurrante) - ponto de entrada do mod.
 *
 * Um farejador de minérios reutilizável e "vanilla-friendly": alcance curto,
 * recarga entre usos, durabilidade que gasta mais para minérios mais raros
 * e receita que pede ouro + ametista (progressão de meio de jogo).
 */
public class WhisperCompassMod implements ModInitializer {
    public static final String MOD_ID = "whispercompass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // A ordem importa: o componente precisa existir antes do item que o usa.
        ModComponents.register();
        ModItems.register();
        LOGGER.info("Whispering Compass carregada. Boa exploração!");
    }
}
