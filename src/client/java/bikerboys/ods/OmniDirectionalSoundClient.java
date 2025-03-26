package bikerboys.ods;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OmniDirectionalSoundClient implements ClientModInitializer {
	private static final Identifier EXAMPLE_LAYER = Identifier.of(OmniDirectionalSound.MOD_ID, "hud-ods-layer");

	@Override
	public void onInitializeClient() {
		HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.OVERLAY_MESSAGE, EXAMPLE_LAYER, OmniDirectionalSoundClient::render));


	}



	private static void render(DrawContext context, RenderTickCounter tickCounter) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (mc.player == null) {
			return;
		}

		// Set up custom sound listener
		CustomSoundListener soundListener = new CustomSoundListener();
		mc.getSoundManager().registerListener(soundListener);



		Vec3d playerPos = mc.player.getPos();
		Vec3d playerDirection = mc.player.getRotationVec(1.0F);  // The player's direction (forward vector)
		Vec3d playerRight = new Vec3d(playerDirection.z, 0, -playerDirection.x);  // Perpendicular to the forward vector (right direction)

		// Iterate over all nearby sounds
		Map<Vec3d, Long> nearbySounds = CustomSoundListener.getNearbySoundLocations(playerPos, 30.0);  // 30 blocks radius
		int ARROW_DISTANCE = 20; // Distance from the center


		List<Vec3d> toRemove = new ArrayList<>();

		for (Map.Entry<Vec3d, Long> entry : nearbySounds.entrySet()) {
			Vec3d soundPos = entry.getKey();
			long soundTime = entry.getValue();

			// Calculate direction vector from player to sound
			Vec3d soundDirection = soundPos.subtract(playerPos).normalize();

			// Get player's yaw (horizontal rotation) in radians
			float yawRad = (float) Math.toRadians(mc.player.getYaw());

			// Calculate the angle between the player's forward direction and the sound direction
			double soundAngle = Math.atan2(soundDirection.z, soundDirection.x) - Math.atan2(Math.cos(yawRad), -Math.sin(yawRad));
			soundAngle = Math.toDegrees(soundAngle); // Convert to degrees
			if (soundAngle < 0) soundAngle += 360; // Normalize to [0, 360)

			// Debugging: Print the calculated angle


			// Determine which quadrant the sound is in
			int centerX = mc.getWindow().getScaledWidth() / 2;
			int centerY = mc.getWindow().getScaledHeight() / 2;
			int offset = 20; // Distance from the center

			// Default arrow position (top)
			int arrowX = centerX;
			int arrowY = centerY;
			int arrowRotation = 0;

// Adjust arrow position and rotation based on angle
			if (soundAngle >= 315 || soundAngle < 45) {
				// Sound is in front (Up arrow)
				arrowX = centerX - 3;
				arrowY = centerY - ARROW_DISTANCE;
				arrowRotation = 270; // Point up
			} else if (soundAngle >= 45 && soundAngle < 135) {
				// Sound is to the right (Right arrow)
				arrowX = centerX + ARROW_DISTANCE;
				arrowY = centerY - 4;
				arrowRotation = 0; // Point right
			} else if (soundAngle >= 135 && soundAngle < 225) {
				// Sound is behind (Bottom arrow)
				arrowX = centerX + 4;
				arrowY = centerY + ARROW_DISTANCE;
				arrowRotation = 90; // Point down
			} else if (soundAngle >= 225 && soundAngle < 315) {
				// Sound is to the left (Left arrow)
				arrowX = centerX - ARROW_DISTANCE;
				arrowY = centerY + 3;
				arrowRotation = 180; // Point left
			}

			// Render the arrow at the fixed position with the correct rotation
			textrender(context, ">", arrowRotation, mc, arrowX, arrowY);
		}


	}


	private static void textrender(DrawContext context, String text, int rotation, MinecraftClient mc, int x, int y) {
		context.getMatrices().push();
		context.getMatrices().translate(x, y, 0); // Move to the specified position
		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation)); // Rotate the text
		context.drawText(mc.textRenderer, text, 0, 0, 0xFFFFFF, false); // Draw the text
		context.getMatrices().pop(); // Restore the original matrix
	}



}