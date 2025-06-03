package bikerboys.ods;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomSoundListener implements SoundInstanceListener {
    // Thread-safe list for sound positions
    public static final Set<CustomSound> soundPositions = new HashSet<>();
    public static final Map<Vec3d, Long> soundStartTimes = new HashMap<>();

    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        Vec3d soundPosition = new Vec3d(sound.getX(), sound.getY(), sound.getZ());
        CustomSound customSound = new CustomSound(soundPosition, sound.getCategory());

        // Add the sound position only if it's not already being tracked
        if (MidnightConfigLib.listEnum == MidnightConfigLib.ListEnum.WHITELIST) {
            if (!soundStartTimes.containsKey(soundPosition) && MidnightConfigLib.ignoredSounds.contains(sound.getId().toString())) {


                if (!soundPositions.contains(customSound)) {
                    soundPositions.add(customSound);
                }




            }
        }

        if (MidnightConfigLib.listEnum == MidnightConfigLib.ListEnum.BLACKLIST) {
            if (!soundStartTimes.containsKey(soundPosition) && !MidnightConfigLib.ignoredSounds.contains(sound.getId().toString())) {


                if (!soundPositions.contains(customSound)) {
                    soundPositions.add(customSound);
                }
            }
        }
    }







    public static Map<CustomSound, Long> getNearbySoundLocations(Vec3d listenerPos, double maxRange) {


        long now = Util.getMeasuringTimeMs();
        Map<CustomSound, Long> elapsedTimes = new HashMap<>();
        List<CustomSound> toRemove = new ArrayList<>();

        for (CustomSound sound : soundPositions) {
            Vec3d pos = sound.getPos();
            if (listenerPos.isInRange(pos, maxRange)) {
                soundStartTimes.putIfAbsent(pos, now);
                long elapsed = now - soundStartTimes.get(pos);
                if (elapsed > MidnightConfigLib.arrowFade) {
                    toRemove.add(sound);
                } else {
                    elapsedTimes.put(sound, elapsed);
                }
            } else {
                toRemove.add(sound);
            }
        }

        for (CustomSound sound : toRemove) {
            soundPositions.remove(sound);
            soundStartTimes.remove(sound.getPos());
        }

        return elapsedTimes;
    }



}