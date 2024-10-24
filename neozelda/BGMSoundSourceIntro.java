package neozelda;

import javax.sound.sampled.Clip;

class BGMSoundSourceIntro extends SoundSource {
    Clip intro;
    Clip loop;

    public BGMSoundSourceIntro(Clip intro, Clip loop) {
        this.intro = intro;
        this.loop = loop;
    }

    @Override
    public void play() {
        if (!isFinished())
            intro.start();
        else
            loop.loop(Clip.LOOP_CONTINUOUSLY);
    }

    @Override
    public void start() {
        intro.setFramePosition(0);
        loop.setFramePosition(0);
        play();
    }

    @Override
    public void pause() {
        if (!isFinished())
            intro.stop();
        else
            loop.stop();
    }

    /**
     * Specifically for starting the loop section of the bgm.
     */
    @Override
    public boolean isFinished() {
        return !intro.isRunning() && intro.getFramePosition() == intro.getFrameLength();
    }

    @Override
    public boolean playing() {
        return intro.isRunning() || loop.isRunning();
    }
    
    protected void finalize() {
        System.out.println("Freeing clip " + this.intro + " " + this.loop);
        intro.stop();
        intro.close();
        loop.stop();
        loop.close();
    }
}
