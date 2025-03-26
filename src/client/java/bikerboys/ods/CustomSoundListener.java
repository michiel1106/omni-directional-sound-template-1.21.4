package bikerboys.ods;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSoundListener implements SoundInstanceListener {
    // Thread-safe list for sound positions
    private static final Set<Vec3d> soundPositions = new HashSet<>();
    private static final Map<Vec3d, Long> soundStartTimes = new HashMap<>();

    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
        Vec3d soundPosition = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
        // Add the sound position only if it's not already being tracked
        if (!soundStartTimes.containsKey(soundPosition)) {
            soundPositions.add(soundPosition);
        }
    }




    public static Map<Vec3d, Long> getNearbySoundLocations(Vec3d listenerPos, double maxRange) {
        long currentTime = Util.getMeasuringTimeMs();
        Map<Vec3d, Long> elapsedTimes = new HashMap<>();
        List<Vec3d> soundsToRemove = new ArrayList<>(); // Temporary list for sounds to remove


        // Iterate over sound positions
        for (Vec3d soundPos : soundPositions) {
            boolean inRange = listenerPos.isInRange(soundPos, maxRange);

            if (inRange) {
                // Initialize start time if new
                if (!soundStartTimes.containsKey(soundPos)) {
                    soundStartTimes.put(soundPos, currentTime);
                }

                long elapsed = currentTime - soundStartTimes.get(soundPos);

                if (elapsed > 500) {
                    // Mark expired sounds for removal
                    soundsToRemove.add(soundPos);
                } else {
                    elapsedTimes.put(soundPos, elapsed);
                }
            } else {
                // Mark out-of-range sounds for removal
                soundsToRemove.add(soundPos);
            }
        }



        // Remove expired or out-of-range sounds from both collections
        for (Vec3d soundPos : soundsToRemove) {
            soundStartTimes.remove(soundPos);
            soundPositions.remove(soundPos);
        }


        return elapsedTimes;

    }
}