package neozelda;

abstract class SoundSource {

    /**
     * Start this track at the beginning.
     */
    public abstract void start();

    /**
     * Play this track from where it left off.
     */
    public abstract void play();

    /**
     * Pause the track.
     */
    public abstract void pause();
}
