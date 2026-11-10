# 🧭 Bússola Sussurrante (Whispering Compass)

Mod **Fabric** para **Minecraft Java 1.21.4** — Quality of Life, vanilla-friendly.

## Visão geral

Uma bússola de ametista reutilizável que **fareja minérios por perto**.

- **Usar (botão direito):** escaneia um cubo de 25×25×25 blocos ao redor de você e mostra na barra de ação
  o minério escolhido mais próximo: a **direção** (seta relativa a para onde você está olhando), a **distância**
  e se ele está **acima (▲)** ou **abaixo (▼)**. Um rastro de partículas e um som que fica mais agudo
  conforme você chega perto ajudam a achar o caminho.
- **Agachado + usar:** troca o minério procurado (carvão → cobre → ferro → ouro → redstone → lápis → diamante → esmeralda → detritos antigos).
- Funciona com as tags vanilla de minérios (`coal_ores`, `iron_ores`…), então minérios de outros mods que usam essas tags também são detectados.

### Por que não quebra o equilíbrio

| Limite | Valor |
|---|---|
| Alcance | 12 blocos em cada direção (só avisa que existe, você ainda precisa cavar) |
| Recarga | 1,5 s entre usos |
| Durabilidade | 120 pontos; conserta na bigorna com **cacos de ametista** |
| Custo por uso | Carvão/cobre/ferro: 1 · Ouro/redstone/lápis: 2 · Diamante/esmeralda: 3 · Detritos antigos: 4 · Nada encontrado: 1 |
| Receita | Pede ouro + ametista + bússola: item de meio de jogo, não de começo |

## Receita (crafting)

```
 [Caco de ametista]
[Ouro] [Bússola] [Ouro]
 [Barra de ferro]
```

`A = amethyst_shard`, `G = gold_ingot`, `C = compass`, `I = iron_ingot` (grade 3×3, formato acima).

## Estrutura de arquivos

```
Bussola-Sussurrante/
├── build.gradle / gradle.properties / settings.gradle
├── .github/workflows/build.yml          # compila e publica o .jar no GitHub
└── src/main/
    ├── java/com/ganhamosgames/whispercompass/
    │   ├── WhisperCompassMod.java       # ModInitializer (ponto de entrada)
    │   ├── ModItems.java                # registro do item + aba criativa
    │   ├── ModComponents.java           # componente de dados "target_mode"
    │   ├── OreTarget.java               # minérios, cores e custo de durabilidade
    │   └── WhisperCompassItem.java      # lógica do item (escanear, trocar alvo, tooltip)
    └── resources/
        ├── fabric.mod.json
        ├── assets/whispercompass/
        │   ├── items/whisper_compass.json        # definição do item (1.21.4)
        │   ├── models/item/whisper_compass.json  # modelo do item
        │   ├── textures/item/whisper_compass.png
        │   └── lang/en_us.json, pt_br.json
        └── data/whispercompass/
            ├── recipe/whisper_compass.json
            └── advancement/recipes/tools/whisper_compass.json
```

## Como instalar / testar

- Minecraft Java **1.21.4**
- **Fabric Loader** 0.16.9 ou mais novo
- **Fabric API** 0.119.4+1.21.4 (na pasta `mods`, junto com o `.jar`)
- Java 21

## Como compilar

**Pelo GitHub (automático):** a cada push na branch `main` o workflow `build.yml` compila e publica
o `whisper-compass-1.0.0.jar` na aba **Releases**.

**No seu computador:** com JDK 21 e Gradle 8.12 instalados, rode na pasta do projeto:

```bash
gradle build
```

O `.jar` sai em `build/libs/whisper-compass-1.0.0.jar` (não use o `-sources.jar`).

## Licença

MIT
