package me.nightsky.tbot.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class TbotClient implements ClientModInitializer {

    private static boolean isTbEnabled = true;
    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = new KeyBinding("triggerbot toggle", GLFW.GLFW_KEY_V, "Tbot");
        KeyBindingHelper.registerKeyBinding(toggleKey);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            if (toggleKey.wasPressed()) {
                isTbEnabled = !isTbEnabled;
                client.player.sendMessage(Text.of("Triggerbot " + (isTbEnabled ? "enabled" : "disabled")), true);
            }

            if (!isTbEnabled || client.crosshairTarget == null) return;

            if (client.crosshairTarget instanceof EntityHitResult hitResult) {
                if (hitResult.getEntity() instanceof LivingEntity target) {
                    hitEntity(client, target);
                }
            }
        });
    }

    private void hitEntity(MinecraftClient client, LivingEntity target) {
        if (!target.isAlive()) {
            return;
        }
        if (client.player.getAttackCooldownProgress(0.5F) < 1.0F) {
            return;
        }

        client.interactionManager.attackEntity(client.player, target);
        client.player.swingHand(Hand.MAIN_HAND);
    }
}
