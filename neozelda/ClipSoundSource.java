package neozelda;

import javax.sound.sampled.Clip;

public class ClipSoundSource extends SoundSource {
    Clip internal;

    public ClipSoundSource(Clip clip) {
        this.internal = clip;
    }

    @Override
    public void play() {
        internal.start();
    }

    @Override
    public void start() {
        internal.setFramePosition(0);
        play();
    }

    public boolean isFinished() {
        return !internal.isRunning() && internal.getFramePosition() == internal.getFrameLength();
    }

    @Override
    public boolean playing() {
        return internal.isRunning();
    }

    public void delete() {
        internal.stop();
        internal.close();
    }

    @Override
    public void pause() {
        internal.stop();
    }
    
    protected void finalize() {
        System.out.println("Freeing clip " + this.internal);
        internal.stop();
        internal.close();
    }
}
