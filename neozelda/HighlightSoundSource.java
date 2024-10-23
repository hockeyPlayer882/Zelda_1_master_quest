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
        System.out.println("Playing highlight");
        threadActive = true;
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
        threadActive = false;
        clip.stop();
    }
    
    protected void finalize() {
        System.out.println("finalizing highlight");
        clip.stop();
        clip.close();
    }

    // TODO: Highlight Sound Source is broken. Rewrite the entire audio engine
    // mixer in a single thread? That's probably far more efficient.
    @Override
    public void run() {
        System.out.println("Thread started.");

        while (runPlayerThread) {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ie) {}

            // Don't manage the clip if it's not playing.
            if (!threadActive)
                continue;

            // Play the BGM again after this clip is done.
            if (!clip.isRunning() && clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
                runPlayerThread = false;
                clip.stop();

                if (AudioEngine.getCurrentBGM() == bgm)
                    bgm.play();

                System.out.println("Thread ended.");
            }

            System.out.println(clip.getMicrosecondPosition());
        }
    }
}
