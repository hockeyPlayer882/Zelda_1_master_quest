package neozelda;

import javax.sound.sampled.Clip;

class HighlightSoundSource extends SoundSource {
    Clip clip;

    public HighlightSoundSource(Clip clip, SoundSource bgm) {
        this.clip = clip;
    }

    @Override
    public void play() {
        System.out.println("Playing highlight");
        clip.start();
    }

    @Override
    public void start() {
        System.out.println("starting highlight");
        clip.setFramePosition(0);
        play();
    }

    @Override
    public void pause() {
        System.out.println("pausing highlight?");
        clip.stop();
    }

    @Override
    public boolean isFinished() {
        return !clip.isRunning() && clip.getFramePosition() == clip.getFrameLength();
    }

    @Override
    public boolean playing() {
        return clip.isRunning();
    }
    
    protected void finalize() {
        System.out.println("finalizing highlight");
        clip.stop();
        clip.close();
    }
}
