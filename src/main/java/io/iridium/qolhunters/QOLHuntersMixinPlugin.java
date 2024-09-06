package io.iridium.qolhunters;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.loading.FMLPaths;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class QOLHuntersMixinPlugin implements IMixinConfigPlugin {

    private static boolean vanillaSafeMode = false;
    private static boolean betterDescriptions = true;
    private static boolean vaultModifierOverlays = true;
    private static boolean vaultEnchanterEmeraldSlot = true;



    private static final Supplier<Boolean> TRUE = () -> true;
    private static final Supplier<Boolean> FALSE = () -> false;

    private static final Map<String, Supplier<Boolean>> BETTER_DESCRIPTIONS_CONDITIONS = ImmutableMap.of(
            "io.iridium.qolhunters.mixin.configs.MixinAbilitiesDescriptionsConfig", () -> QOLHuntersMixinPlugin.betterDescriptions,
            "io.iridium.qolhunters.mixin.configs.MixinMenuPlayerStatDescriptionConfig", () -> QOLHuntersMixinPlugin.betterDescriptions,
            "io.iridium.qolhunters.mixin.configs.MixinSkillDescriptionsConfig", () -> QOLHuntersMixinPlugin.betterDescriptions
    );

    private static final Map<String, Supplier<Boolean>> VAULT_MODIFIER_OVERLAYS_CONDITIONS = ImmutableMap.of(
            "io.iridium.qolhunters.mixin.configs.MixinVaultModifiersConfig", () -> QOLHuntersMixinPlugin.vaultModifierOverlays
    );

    private static final Map<String, Supplier<Boolean>> VAULT_ENCHANTER_EMERALD_SLOT_CONDITIONS = ImmutableMap.of(
            "io.iridium.qolhunters.mixin.vaultenchanter.MixinEnchantmentCost", () -> QOLHuntersMixinPlugin.vaultEnchanterEmeraldSlot && !QOLHuntersMixinPlugin.vanillaSafeMode,
            "io.iridium.qolhunters.mixin.vaultenchanter.MixinVaultEnchanterBlock", () -> QOLHuntersMixinPlugin.vaultEnchanterEmeraldSlot && !QOLHuntersMixinPlugin.vanillaSafeMode,
            "io.iridium.qolhunters.mixin.vaultenchanter.MixinVaultEnchanterContainer", () -> QOLHuntersMixinPlugin.vaultEnchanterEmeraldSlot && !QOLHuntersMixinPlugin.vanillaSafeMode,
            "io.iridium.qolhunters.mixin.vaultenchanter.MixinVaultEnchanterScreen", () -> QOLHuntersMixinPlugin.vaultEnchanterEmeraldSlot && !QOLHuntersMixinPlugin.vanillaSafeMode,
            "io.iridium.qolhunters.mixin.vaultenchanter.MixinVaultEnchanterTileEntity", () -> QOLHuntersMixinPlugin.vaultEnchanterEmeraldSlot && !QOLHuntersMixinPlugin.vanillaSafeMode
    );







    private static final String CONFIG_FILE_NAME = "qolhunters-client.toml";
    private static final String VANILLA_SAFE_MODE_CONFIG_VALUE = "General Configs.Vanilla Safe Mode";
    private static final String BETTER_DESCRIPTIONS_CONFIG_VALUE = "Client-Only Extensions.Better Descriptions";
    private static final String VAULT_MODIFIER_OVERLAYS_CONFIG_VALUE = "Client-Only Extensions.Vault Modifier Text Overlays";
    private static final String VAULT_ENCHANTER_EMERALD_SLOT_CONFIG_VALUE = "Client-Server Extensions.Vault Enchanter Emeralds Slot";



    private static void loadConfig(){
        try {
            Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_NAME);
            File configFile = configPath.toFile();
            if (configFile.exists()) {
                FileConfig config = FileConfig.of(configFile);
                config.load();
                vanillaSafeMode = config.getOrElse(VANILLA_SAFE_MODE_CONFIG_VALUE, false);
                betterDescriptions = config.getOrElse(BETTER_DESCRIPTIONS_CONFIG_VALUE, true);
                vaultModifierOverlays = config.getOrElse(VAULT_MODIFIER_OVERLAYS_CONFIG_VALUE, true);
                vaultEnchanterEmeraldSlot = config.getOrElse(VAULT_ENCHANTER_EMERALD_SLOT_CONFIG_VALUE, true);

                QOLHunters.LOGGER.info("QOLHunters: Vanilla Safe Mode: " + vanillaSafeMode);
                QOLHunters.LOGGER.info("QOLHunters: Better Descriptions: " + betterDescriptions);
                QOLHunters.LOGGER.info("QOLHunters: Vault Modifier Text Overlays: " + vaultModifierOverlays);
                QOLHunters.LOGGER.info("QOLHunters: Vault Enchanter Emeralds Slot: " + vaultEnchanterEmeraldSlot);

                config.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        loadConfig();
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        boolean shouldApply;

        if(VAULT_ENCHANTER_EMERALD_SLOT_CONDITIONS.containsKey(mixinClassName)){
            shouldApply = VAULT_ENCHANTER_EMERALD_SLOT_CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
        } else if(BETTER_DESCRIPTIONS_CONDITIONS.containsKey(mixinClassName)){
            shouldApply = BETTER_DESCRIPTIONS_CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
        } else if(VAULT_MODIFIER_OVERLAYS_CONDITIONS.containsKey(mixinClassName)){
            shouldApply = VAULT_MODIFIER_OVERLAYS_CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
        } else {
            shouldApply = TRUE.get();
        }
        QOLHunters.LOGGER.info("QOLHunters: shouldApplyMixin: " + mixinClassName + " -> " + shouldApply);

        return shouldApply;
    }

    // Boilerplate

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
