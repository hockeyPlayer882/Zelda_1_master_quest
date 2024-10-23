package neozelda;

import javax.sound.sampled.Clip;

class BGMSoundSource extends SoundSource {
    Clip internal;

    public BGMSoundSource(Clip clip) {
        this.internal = clip;
    }

    @Override
    public void play() {
        internal.loop(Clip.LOOP_CONTINUOUSLY);
    }

    @Override
    public void start() {
        internal.setFramePosition(0);
        play();
    }

    @Override
    public void pause() {
        internal.stop();
    }
    
    protected void finalize() {
        internal.stop();
        internal.close();
    }
}
