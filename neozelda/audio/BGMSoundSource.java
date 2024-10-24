package neozelda.audio;

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

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean playing() {
        return internal.isRunning();
    }
    
    protected void finalize() {
        //System.out.println("Freeing bgm " + this.internal);
        internal.stop();
        internal.close();
    }

    @Override
    public void delete() {
        internal.stop();
        internal.close();
    }
}
