package neozelda;

import javax.sound.sampled.Clip;

class BGMSoundSourceIntro extends SoundSource implements Runnable {
    Clip intro;
    Clip loop;

    boolean runPlayerThread = true;
    boolean threadActive = false;
    Thread playerThread;

    public BGMSoundSourceIntro(Clip intro, Clip loop) {
        this.intro = intro;
        this.loop = loop;

        playerThread = new Thread(this);
        playerThread.setDaemon(true);
        playerThread.start();
    }

    @Override
    public void play() {
        threadActive = true;
    }

    @Override
    public void start() {
        intro.setFramePosition(0);
        loop.setFramePosition(0);
        play();
    }

    @Override
    public void pause() {
        threadActive = false;
        loop.stop();
    }
    
    protected void finalize() {
        intro.stop();
        intro.close();
        loop.stop();
        loop.close();
    }

    @Override
    public void run() {
        while (runPlayerThread) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ie) {}

            // Don't manage the clip if it's not playing.
            if (!threadActive) {
                intro.stop();
                continue;
            }

            // Play the loop clip now.
            if (!intro.isRunning() && intro.getFramePosition() != 0) {
                threadActive = false;
                loop.loop(Clip.LOOP_CONTINUOUSLY);
            }
            // Start the intro clip.
            else {
                intro.start();
            }
        }
    }
}
