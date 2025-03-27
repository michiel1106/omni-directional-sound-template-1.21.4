package bikerboys.ods;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

import static java.lang.Math.*;

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
		mc.getSoundManager().getSoundDevices().clear();



		Vec3d playerPos = mc.player.getPos();


		// Iterate over all nearby sounds
		Map<Vec3d, Long> nearbySounds = CustomSoundListener.getNearbySoundLocations(playerPos, MidnightConfigLib.maxSoundRange);  // 30 blocks radius
		int ARROW_DISTANCE = MidnightConfigLib.arrowOffset; // Distance from the center




		for (Map.Entry<Vec3d, Long> entry : nearbySounds.entrySet()) {
			Vec3d soundPos = entry.getKey();



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
				arrowX = centerX + 4 ;
				arrowY = centerY + ARROW_DISTANCE;
				arrowRotation = 90; // Point down
			} else if (soundAngle >= 225 && soundAngle < 315) {
				// Sound is to the left (Left arrow)
				arrowX = centerX - ARROW_DISTANCE;
				arrowY = centerY + 3;
				arrowRotation = 180; // Point left
			}


			if (MidnightConfigLib.soundAngle) {
				mc.player.sendMessage(Text.of(String.valueOf((Float.valueOf((float) soundAngle)))), true);
			}
			if (MidnightConfigLib.arrowEnum.equals(MidnightConfigLib.ArrowEnum.STATIC)) {
				double radians = soundAngle * (PI / 180.0);
				arrowX = (int) (centerX + ARROW_DISTANCE * sin(radians));
				arrowY = (int) (centerY - ARROW_DISTANCE * cos(radians));
				arrowRotation = (int) soundAngle;
				textrender(context, ">", arrowRotation - 90, mc, arrowX, arrowY);
			} else {
				textrender(context, ">", arrowRotation, mc, arrowX, arrowY);
			}

		}


	}


	private static void textrender(DrawContext context, String text, int rotation, MinecraftClient mc, int x, int y) {
		context.getMatrices().push();

		context.getMatrices().translate(x - MidnightConfigLib.arrowScale * MidnightConfigLib.testInt, y, 0); // Move to the specified position
		context.getMatrices().scale(MidnightConfigLib.arrowScale, MidnightConfigLib.arrowScale, 1);


		context.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation)); // Rotate the text
		context.drawText(mc.textRenderer, text, 0, 0, 0xFFFFFF, false); // Draw the text
		context.getMatrices().pop(); // Restore the original matrix
	}



}