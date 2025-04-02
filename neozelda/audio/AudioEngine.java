package neozelda.audio;

import java.util.ArrayList;
import java.io.File;
import javax.sound.sampled.*;

public class AudioEngine implements Runnable {
    SoundSource bgm = null;
    ArrayList<ClipSoundSource> sounds = new ArrayList<>();
    HighlightSoundSource currentHighlight = null;

    static AudioEngine theEngine;
    static Thread engineThread;

    static {
        theEngine = new AudioEngine();
        engineThread = new Thread(theEngine);
        engineThread.setDaemon(true);
        engineThread.start();
    }

    /**
     * Internal.
     */
    private void tryPlayBGM(String soundPath) {
        try {
            Clip clip = loadClip(new File(soundPath));
            BGMSoundSource bgmSource = new BGMSoundSource(clip);

            if (currentHighlight != null) {
                currentHighlight.delete();
                currentHighlight = null;
            }

            if (bgm != null)
                bgm.delete();

            bgm = bgmSource;
            bgmSource.start();
        }
        catch (Exception ie) {
            System.out.println("Failed to load requested clip. Stack trace:");
            ie.printStackTrace();
        }
    }

    /**
     * Play a standard looping BGM with no intro track.
     * 
     * @param soundPath Path of the sound file to load and play.
     */
    public static void playBGM(String soundPath) {
        theEngine.tryPlayBGM(soundPath);
    }

    /**
     * Play a looping BGM with an intro track.
     * 
     * @param introPath Path of the intro track.
     * @param soundPath Path of the loop section.
     */
    public void tryPlayBGM(String introPath, String soundPath) {
        try {
            Clip introClip = loadClip(new File(introPath));
            Clip loopClip = loadClip(new File(soundPath));
            BGMSoundSourceIntro bgmSource = new BGMSoundSourceIntro(introClip, loopClip);

            if (currentHighlight != null) {
                currentHighlight.delete();
                currentHighlight = null;
            }

            if (bgm != null)
                bgm.delete();

            bgm = bgmSource;
            bgmSource.start();
        }
        catch (Exception ie) {
            System.out.println("Failed to load requested clip. Stack trace:");
            ie.printStackTrace();
        }
    }

    /**
     * Play a looping BGM with an intro track.
     * 
     * @param introPath Path of the intro track.
     * @param soundPath Path of the loop section.
     */
    public static void playBGM(String introPath, String soundPath) {
        theEngine.tryPlayBGM(introPath, soundPath);
    }

    /**
     * Play a highlight track that plays over and temporarily replaces the BGM.
     * 
     * @param soundPath Path of the highlight.
     */
    public void tryPlayHighlight(String soundPath) {
        try {
            Clip highlightClip = loadClip(new File(soundPath));
            HighlightSoundSource highlight = new HighlightSoundSource(highlightClip);

            //if (bgm != null)
            //    bgm.pause();

            if (currentHighlight != null)
                currentHighlight.delete();

            currentHighlight = highlight;
            currentHighlight.start();
        }
        catch (Exception ie) {
            System.out.println("Failed to load requested clip. Stack trace:");
            ie.printStackTrace();
        }
    }

    /**
     * Play a highlight track that plays over and temporarily replaces the BGM.
     * 
     * @param soundPath Path of the highlight.
     */
    public static void playHighlight(String soundPath) {
        theEngine.tryPlayHighlight(soundPath);
    }

    /**
     * Play a sound clip.
     */
    // TODO: Prevent stacking of clips if the same filename's been played
    // in the last 500 ms.
    public void tryPlayClip(String clipPath) {
        try {
            Clip clip = loadClip(new File(clipPath));
            ClipSoundSource clipSrc = new ClipSoundSource(clip);

            clipSrc.start();
            sounds.add(clipSrc);
        }
        catch (Exception ie) {
            System.out.println("Failed to load requested clip. Stack trace:");
            ie.printStackTrace();
        }
    }

    /**
     * Play a sound clip.
     */
    public static void playClip(String clipPath) {
        theEngine.tryPlayClip(clipPath);
    }

    static Clip loadClip(File path) throws Exception {
        AudioInputStream stream = AudioSystem.getAudioInputStream(path);
        Clip clip = AudioSystem.getClip();
        clip.open(stream);
        stream.close();
        return clip;
    }

    @Override
    public void run() {
        while (true) {
            // Purge all clips that are no longer running.
            ArrayList<ClipSoundSource> dupClips = new ArrayList<>();
            dupClips.addAll(sounds);
            for (ClipSoundSource sndClip : dupClips) {
                if (sndClip.isFinished()) {
                    sndClip.delete();
                    sounds.remove(sndClip);
                }
            }

            // Don't waste an entire CPU on this thread.
            try {
                Thread.sleep(25);
            } 
            catch (InterruptedException ie) {}

            // Play an overlay highlight.
            if (currentHighlight != null) {
                if (bgm.playing())
                    bgm.pause();

                else if (currentHighlight.isFinished()) {
                    currentHighlight.pause();
                    currentHighlight = null;
                    bgm.play();
                }
            }
            // Manage the current BGM and looping.
            else if (bgm != null && bgm.isFinished())
                bgm.play();
        }
    }
}
