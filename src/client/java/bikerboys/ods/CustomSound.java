package bikerboys.ods;

import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class CustomSound {
    Vec3d pos;
    SoundCategory soundCategory;


    public CustomSound(Vec3d pos, SoundCategory category) {
        this.pos = pos;
        this.soundCategory = category;
    }

    public SoundCategory getSoundCategory() {
        return soundCategory;
    }

    public Vec3d getPos() {
        return pos;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public void setSoundCategory(SoundCategory soundCategory) {
        this.soundCategory = soundCategory;
    }
}
