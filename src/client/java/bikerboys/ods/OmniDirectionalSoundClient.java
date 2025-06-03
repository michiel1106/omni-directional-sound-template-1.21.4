package bikerboys.ods;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;


import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

import static java.lang.Math.*;

public class OmniDirectionalSoundClient implements ClientModInitializer {
	private static final Identifier EXAMPLE_LAYER = Identifier.of(OmniDirectionalSound.MOD_ID, "hud-ods-layer");
	CustomSoundListener soundListener = new CustomSoundListener();
	@Override
	public void onInitializeClient() {
		//HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.OVERLAY_MESSAGE, EXAMPLE_LAYER, OmniDirectionalSoundClient::render));

		HudRenderCallback.EVENT.register(OmniDirectionalSoundClient::render);


		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			client.getSoundManager().registerListener(soundListener);

		});

	}



	private static void render(DrawContext drawContext, float tickCounter) {
		MinecraftClient mc = MinecraftClient.getInstance();

		int x = mc.getWindow().getScaledWidth() / 2;
		int y = mc.getWindow().getScaledHeight() / 2;


		MatrixStack context = drawContext.getMatrices();

		context.push();

		context.translate(x-2, y-4, 0); // Move to the specified position


		//context.drawText(mc.textRenderer, ">", 0, 0, 0xFFFFFF, false); // Draw the text
		context.pop(); // Restore the original matrix



		if (mc.player == null) {
			return;
		}

		// Set up custom sound listener




		Vec3d playerPos = mc.player.getPos();


		// Iterate over all nearby sounds
		Map<CustomSound, Long> nearbySounds = CustomSoundListener.getNearbySoundLocations(playerPos, MidnightConfigLib.maxSoundRange);  // 30 blocks radius
		int ARROW_DISTANCE = MidnightConfigLib.arrowOffset; // Distance from the center

		if (MidnightConfigLib.editMode) {
			int centerX = mc.getWindow().getScaledWidth() / 2;
			int centerY = mc.getWindow().getScaledHeight() / 2;
			int arrowX = centerX;
			int arrowY = centerY;
			int arrowRotation = 0;
			int color = 0xFFFFFF;


			arrowX = centerX - 3 * MidnightConfigLib.arrowScale;
			arrowY = centerY - ARROW_DISTANCE;
			arrowRotation = 270; // Point up
			textrender(drawContext, ">", arrowRotation, mc, arrowX, arrowY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);


			arrowX = centerX + ARROW_DISTANCE;
			arrowY = centerY - 4 * MidnightConfigLib.arrowScale;
			arrowRotation = 0; // Point right
			textrender(drawContext, ">", arrowRotation, mc, arrowX, arrowY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);

			arrowX = centerX + 4 * MidnightConfigLib.arrowScale ;
			arrowY = centerY + ARROW_DISTANCE;
			arrowRotation = 90; // Point down
			textrender(drawContext, ">", arrowRotation, mc, arrowX, arrowY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);

			arrowX = centerX - ARROW_DISTANCE;
			arrowY = centerY + 3 * MidnightConfigLib.arrowScale;
			arrowRotation = 180; // Point left
			textrender(drawContext, ">", arrowRotation, mc, arrowX, arrowY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);
		}



		for (Map.Entry<CustomSound, Long> entry : nearbySounds.entrySet()) {
			Vec3d soundPos = entry.getKey().getPos();
			int color;

			if (MidnightConfigLib.coloredArrows) {
				switch (entry.getKey().soundCategory) {
					case MASTER -> color = 0x00FF00;
					case MUSIC -> color = 0x00FF00;
					case RECORDS -> color = 0x00FF00;
					case WEATHER -> color = 0x00FF00;
					case BLOCKS -> color = 0x00FF00;
					case HOSTILE -> color = 0xFF0000;
					case NEUTRAL -> color = 0x00FF00;
					case PLAYERS -> color = 0xFFFF00;
					case AMBIENT -> color = 0x00FF00;
					case VOICE -> color = 0x00FF00;
					default -> color = 0x00FF00;
				}
			} else {
				color = 0xFFFFFF;
			}


			Vec3d soundDirection = soundPos.subtract(playerPos).normalize();

			// Get player's yaw (horizontal rotation) in radians
			float yawRad = (float) Math.toRadians(mc.player.getYaw());

			double soundAngle = Math.atan2(soundDirection.z, soundDirection.x) - Math.atan2(cos(yawRad), -sin(yawRad));
			soundAngle = Math.toDegrees(soundAngle);
			if (soundAngle < 0) soundAngle += 360;





			int centerX = mc.getWindow().getScaledWidth() / 2;
			int centerY = mc.getWindow().getScaledHeight() / 2;



			int arrowX = centerX;
			int arrowY = centerY;
			int arrowRotation = 0;

			if (soundAngle >= 315 || soundAngle < 45) {
				// Sound is in front (Up arrow)
				arrowX = centerX - 3 * MidnightConfigLib.arrowScale;
				arrowY = centerY - ARROW_DISTANCE;
				arrowRotation = 270; // Point up



			} else if (soundAngle >= 45 && soundAngle < 135) {
				// Sound is to the right (Right arrow)
				arrowX = centerX + ARROW_DISTANCE;
				arrowY = centerY - 4 * MidnightConfigLib.arrowScale;
				arrowRotation = 0; // Point right
			} else if (soundAngle >= 135 && soundAngle < 225) {
				// Sound is behind (Bottom arrow)
				arrowX = centerX + 4 * MidnightConfigLib.arrowScale ;
				arrowY = centerY + ARROW_DISTANCE;
				arrowRotation = 90; // Point down
			} else if (soundAngle >= 225 && soundAngle < 315) {
				// Sound is to the left (Left arrow)
				arrowX = centerX - ARROW_DISTANCE;
				arrowY = centerY + 3 * MidnightConfigLib.arrowScale;
				arrowRotation = 180; // Point left
			}


			if (MidnightConfigLib.soundAngle) {
				mc.player.sendMessage(Text.of(String.valueOf((Float.valueOf((float) soundAngle)))), true);
			}
			if (MidnightConfigLib.arrowEnum.equals(MidnightConfigLib.ArrowEnum.DYNAMIC)) {
				double radians = Math.toRadians(soundAngle);
				double offsetX = ARROW_DISTANCE * Math.sin(radians);
				double offsetY = ARROW_DISTANCE * Math.cos(radians);

				arrowX = (int) Math.round(centerX + offsetX);
				arrowY = (int) Math.round(centerY - offsetY);
				arrowRotation = (int) soundAngle - 90;

				// Calculate centering adjustments based on glyph dimensions and rotation
				int w = mc.textRenderer.getWidth(">") * MidnightConfigLib.arrowScale;
				int h = mc.textRenderer.fontHeight * MidnightConfigLib.arrowScale;
				double rotationRad = Math.toRadians(arrowRotation);
				double cos = Math.cos(rotationRad);
				double sin = Math.sin(rotationRad);

				// Adjust for glyph center
				double dx = (w / 2.0) * cos - (h / 2.0) * sin;
				double dy = (w / 2.0) * sin + (h / 2.0) * cos;
				int adjustedX = (int) (arrowX - dx);
				int adjustedY = (int) (arrowY - dy);

				textrender(drawContext, ">", arrowRotation, mc, adjustedX, adjustedY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);
			}  else {
				textrender(drawContext, ">", arrowRotation, mc, arrowX, arrowY, MidnightConfigLib.xOffset, MidnightConfigLib.yOffset, color);
			}

		}


	}


	private static void textrender(DrawContext ctx, String text, int rotation, MinecraftClient mc, int x, int y, float xoffset, float yoffset, int color) {
		MatrixStack context = ctx.getMatrices();

		context.push();

		context.translate(xoffset, yoffset, 0);
		context.translate(x, y, 0); // Move to the specified position
		context.scale(MidnightConfigLib.arrowScale, MidnightConfigLib.arrowScale, 1);



		context.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation)); // Rotate the text
		//mc.textRenderer.draw(context, text, 0, 0, color); // Draw the text

		ctx.drawText(mc.textRenderer, text, 0,0, color, false);



		context.pop(); // Restore the original matrix
	}



}