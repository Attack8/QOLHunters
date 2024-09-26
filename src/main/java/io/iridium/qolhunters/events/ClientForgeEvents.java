package io.iridium.qolhunters.events;

import com.simibubi.create.foundation.config.ui.ConfigHelper;
import com.simibubi.create.foundation.config.ui.SubMenuConfigScreen;
import io.iridium.qolhunters.QOLHunters;
import io.iridium.qolhunters.config.QOLHuntersClientConfigs;
import io.iridium.qolhunters.features.vault_scavenger.Scavenger;
import io.iridium.qolhunters.networking.ModMessages;
import io.iridium.qolhunters.networking.packet.HandshakeCheckModIsOnServerC2SPacket;
import io.iridium.qolhunters.util.KeyBindings;
import iskallia.vault.config.AbilitiesDescriptionsConfig;
import iskallia.vault.config.BingoConfig;
import iskallia.vault.config.MenuPlayerStatDescriptionConfig;
import iskallia.vault.config.SkillDescriptionsConfig;
import iskallia.vault.core.data.key.ThemeKey;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.vault.WorldManager;
import iskallia.vault.core.vault.objective.Objectives;
import iskallia.vault.event.event.VaultJoinEvent;
import iskallia.vault.event.event.VaultLeaveEvent;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ServerboundMagnetToggleMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static io.iridium.qolhunters.QOLHunters.MOD_ID;
import static io.iridium.qolhunters.util.SharedFunctions.*;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
public  class ClientForgeEvents {
    public static QOLHunters.ModMode MOD_MODE = QOLHunters.ModMode.CLIENTONLY;

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {

        if (KeyBindings.TOGGLE_CAKE_OVERLAY_COLOR.consumeClick()) {
            QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.set(
                    QOLHuntersClientConfigs.CakeVaultOverlayColor.values()[
                            (QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.get().ordinal() + 1) % QOLHuntersClientConfigs.CakeVaultOverlayColor.values().length
                            ]
            );
            Style style = Style.EMPTY.withColor(QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.get().getColorCode());
            displayMessageOnScreen(new TextComponent("Changed Cake Overlay Color").withStyle(style));
            QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.set(QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.get());
        }

        if (KeyBindings.TOGGLE_CAKE_OVERLAY_STYLE.consumeClick()) {
            QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_STYLE.set(
                    QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_STYLE.get() == QOLHuntersClientConfigs.CakeVaultOverlayStyle.VIGNETTE
                            ? QOLHuntersClientConfigs.CakeVaultOverlayStyle.RADAR
                            : QOLHuntersClientConfigs.CakeVaultOverlayStyle.VIGNETTE
            );
            Style style = Style.EMPTY.withColor(QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_COLOR.get().getColorCode());
            String styleText = QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_STYLE.get() == QOLHuntersClientConfigs.CakeVaultOverlayStyle.VIGNETTE ? "Vignette" : "Radar";
            displayMessageOnScreen(new TextComponent("Changed Cake Overlay Style: " + styleText).withStyle(style));
            QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_STYLE.set(QOLHuntersClientConfigs.CAKE_VAULT_OVERLAY_STYLE.get());
        }

        if (event.getKey() == KeyBindings.TOGGLE_MAGNET_GUI.getKey().getValue() && event.getAction() == GLFW.GLFW_PRESS && Minecraft.getInstance().screen != null) {
            ModNetwork.CHANNEL.sendToServer(ServerboundMagnetToggleMessage.INSTANCE);
        }

        if (KeyBindings.OPEN_CONFIG.consumeClick()) {
            SubMenuConfigScreen screen = SubMenuConfigScreen.find(ConfigHelper.ConfigPath.parse("qolhunters:client.Client-Only Extensions"));
            Minecraft.getInstance().setScreen(screen);
        }

        if (event.getKey() == GLFW.GLFW_KEY_Q && event.getAction() == GLFW.GLFW_PRESS &&
                (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0 &&
                (event.getModifiers() & GLFW.GLFW_MOD_CONTROL) != 0 &&
                (event.getModifiers() & GLFW.GLFW_MOD_ALT) != 0) {
            Scavenger.ScavengerItems.clear();
            QOLHunters.LOGGER.info("Scavenger items cleared");
        }

        if (event.getKey() == GLFW.GLFW_KEY_O && event.getAction() == GLFW.GLFW_PRESS &&
                (event.getModifiers() & GLFW.GLFW_MOD_SHIFT) != 0 &&
                (event.getModifiers() & GLFW.GLFW_MOD_CONTROL) != 0 &&
                (event.getModifiers() & GLFW.GLFW_MOD_ALT) != 0) {
            displayMessageOnScreen(new TextComponent("OBS MODE ENABLED").withStyle(ChatFormatting.RED));
        }


    }



    private static Component vaultTitle;
    private static Component vaultSubtitle;

    @SubscribeEvent
    public static void onVaultJoin(VaultJoinEvent event) {

        ResourceLocation theme = event.getVault().get(Vault.WORLD).get(WorldManager.THEME);
        ThemeKey themeKey = VaultRegistry.THEME.getKey(theme);
        vaultSubtitle = new TextComponent(themeKey.getName()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(themeKey.getColor()))).withStyle(ChatFormatting.ITALIC);
        String obj = getVaultObjective(event.getVault().get(Vault.OBJECTIVES).get(Objectives.KEY));
        vaultTitle = new TextComponent(obj + " Vault").withStyle((Style.EMPTY.withColor(TextColor.fromRgb(14536734))));


        // Register ClientTickEvent listener
        MinecraftForge.EVENT_BUS.addListener(new java.util.function.Consumer<TickEvent.ClientTickEvent>() {
            @Override
            public void accept(TickEvent.ClientTickEvent tickEvent) {
                if (vaultTitle != null && Minecraft.getInstance().player != null && Minecraft.getInstance().screen == null) {
                    displayTitleOnScreen(vaultTitle);
                    displaySubtitleOnScreen(vaultSubtitle);
                    vaultTitle = null;
                    vaultSubtitle = null;
                    // Unregister the listener after displaying the message
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onVaultLeave(VaultLeaveEvent event) {
        Scavenger.ScavengerItems.clear();
    }

    @SubscribeEvent
    public static void onVaultComplete(PlayerEvent.PlayerChangedDimensionEvent event) {
        Scavenger.ScavengerItems.clear();
    }


    public static String getVaultObjective(String key) {
        String var2 = key == null ? "" : key.toLowerCase();

        return switch (var2) {
            case "boss" -> "Hunt the Guardians";
            case "monolith" -> "Brazier";
            case "empty", "" -> "";
            default -> key.substring(0, 1).toUpperCase() + key.substring(1);
        };
    }



    private static Boolean isBetterAbilitiesDescriptionEnabled = null;
    private static Boolean isBetterStatsDescriptionEnabled = null;
    private static Boolean isBetterSkillDescriptionEnabled = null;
    private static Boolean isBetterBingoEnabled = null;
    private static long LastCheckedTime = 0;

    @SubscribeEvent
    public static void CheckIfMixinConfigsAreChanged(TickEvent.ClientTickEvent event) {
        if (isBetterAbilitiesDescriptionEnabled == null) {
            isBetterAbilitiesDescriptionEnabled = QOLHuntersClientConfigs.BETTER_ABILITIES_DESCRIPTIONS.get();
            isBetterStatsDescriptionEnabled = QOLHuntersClientConfigs.BETTER_STATS_DESCRIPTIONS.get();
            isBetterSkillDescriptionEnabled = QOLHuntersClientConfigs.BETTER_TALENTS_EXPERTISE_RESEARCH_DESCRIPTIONS.get();
            isBetterBingoEnabled = QOLHuntersClientConfigs.BETTER_BINGO_DESCRIPTIONS.get();
        }

        if (System.currentTimeMillis() < LastCheckedTime + 5000 ||
                (isBetterAbilitiesDescriptionEnabled == QOLHuntersClientConfigs.BETTER_ABILITIES_DESCRIPTIONS.get() &&
                        isBetterStatsDescriptionEnabled == QOLHuntersClientConfigs.BETTER_STATS_DESCRIPTIONS.get() &&
                        isBetterSkillDescriptionEnabled == QOLHuntersClientConfigs.BETTER_TALENTS_EXPERTISE_RESEARCH_DESCRIPTIONS.get() &&
                        isBetterBingoEnabled == QOLHuntersClientConfigs.BETTER_BINGO_DESCRIPTIONS.get()
                ))
            return;

        QOLHunters.LOGGER.info(MOD_MODE.toString());
        isBetterAbilitiesDescriptionEnabled = QOLHuntersClientConfigs.BETTER_ABILITIES_DESCRIPTIONS.get();
        isBetterStatsDescriptionEnabled = QOLHuntersClientConfigs.BETTER_STATS_DESCRIPTIONS.get();
        isBetterSkillDescriptionEnabled = QOLHuntersClientConfigs.BETTER_TALENTS_EXPERTISE_RESEARCH_DESCRIPTIONS.get();
        isBetterBingoEnabled = QOLHuntersClientConfigs.BETTER_BINGO_DESCRIPTIONS.get();

        ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS = (new MenuPlayerStatDescriptionConfig()).readConfig();
        ModConfigs.ABILITIES_DESCRIPTIONS = (new AbilitiesDescriptionsConfig()).readConfig();
        ModConfigs.SKILL_DESCRIPTIONS = (new SkillDescriptionsConfig()).readConfig();
        ModConfigs.BINGO = (new BingoConfig()).readConfig();

        LastCheckedTime = System.currentTimeMillis();

    }

    @SubscribeEvent
    public static void onJoinServer(ClientPlayerNetworkEvent.LoggedInEvent event) {

        MOD_MODE = QOLHunters.ModMode.CLIENTONLY;
        ModMessages.sendToServer(new HandshakeCheckModIsOnServerC2SPacket());


    }
}
