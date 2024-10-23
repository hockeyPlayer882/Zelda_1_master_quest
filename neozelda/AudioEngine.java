package neozelda;

import java.util.ArrayList;
import java.io.File;
import javax.sound.sampled.*;

public class AudioEngine {
    static SoundSource bgm = null;
    static ArrayList<ClipSoundSource> sounds = new ArrayList<>();

    /**
     * Play a standard looping BGM with no intro track.
     * 
     * @param soundPath Path of the sound file to load and play.
     */
    public static void playBGM(String soundPath) {
        try {
            Clip clip = loadClip(new File(soundPath));
            BGMSoundSource bgmSource = new BGMSoundSource(clip);

            if (bgm != null)
                bgm.pause();

            bgm = bgmSource;
            bgm.play();
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
        try {
            Clip introClip = loadClip(new File(introPath));
            Clip loopClip = loadClip(new File(soundPath));
            BGMSoundSourceIntro bgmSource = new BGMSoundSourceIntro(introClip, loopClip);

            if (bgm != null)
                bgm.pause();

            bgm = bgmSource;
            bgm.play();
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
        try {
            Clip highlightClip = loadClip(new File(soundPath));
            HighlightSoundSource highlight = new HighlightSoundSource(highlightClip, bgm);

            if (bgm != null)
                bgm.pause();

            highlight.start();
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
        try {
            Clip clip = loadClip(new File(clipPath));
            ClipSoundSource clipSrc = new ClipSoundSource(clip);

            // TODO: Possible memory leak?
            clipSrc.start();
            sounds.add(clipSrc);
        }
        catch (Exception ie) {
            System.out.println("Failed to load requested clip. Stack trace:");
            ie.printStackTrace();
        }

        // Purge all clips that are no longer running.
        ArrayList<ClipSoundSource> dupClips = new ArrayList<>();
        dupClips.addAll(sounds);
        for (ClipSoundSource sndClip : sounds) {
            if (sndClip.isFinished()) {
                sndClip.delete();
                sounds.remove(sndClip);
            }
        }
    }

    public static SoundSource getCurrentBGM() {
        return bgm;
    }

    static Clip loadClip(File path) throws Exception {
        AudioInputStream stream = AudioSystem.getAudioInputStream(path);
        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip clip = (Clip) AudioSystem.getLine(info);
        clip.open(stream);
        return clip;
    }
}
