package neozelda;

import javax.sound.sampled.Clip;

class HighlightSoundSource extends SoundSource implements Runnable {
    Clip clip;
    SoundSource bgm;

    boolean runPlayerThread = true;
    boolean threadActive = false;
    Thread playerThread;

    public HighlightSoundSource(Clip clip, SoundSource bgm) {
        this.clip = clip;
        this.bgm = bgm;

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
        clip.setFramePosition(0);
        play();
    }

    @Override
    public void pause() {
        threadActive = false;
        clip.stop();
    }
    
    protected void finalize() {
        clip.stop();
        clip.close();
    }

    @Override
    public void run() {
        while (runPlayerThread) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ie) {}

            // Don't manage the clip if it's not playing.
            if (!threadActive)
                continue;

            // Play the BGM again after this clip is done.
            if (!clip.isRunning() && clip.getFramePosition() != 0) {
                runPlayerThread = false;

                if (AudioEngine.getCurrentBGM() == bgm)
                    bgm.play();
            }
            // Start the intro clip.
            else {
                clip.start();
            }
        }
    }
}
