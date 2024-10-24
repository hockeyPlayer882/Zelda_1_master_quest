package neozelda.audio;

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

    /**
     * Report whether this source is playing or not.
     * 
     * @return Whether it's still playing.
     */
    public abstract boolean isFinished();

    /**
     * Report whether this audio clip is playing.
     * 
     * @return Whether it's actively playing.
     */
    public abstract boolean playing();

    /**
     * Force this audio track to stop playing and deallocate all resources.
     */
    public abstract void delete();
}
