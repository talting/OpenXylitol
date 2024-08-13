package cc.xylitol.utils.sound;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;


public class WAVPlayer {
    public void playSound(String file) {
        try {
            InputStream audioSrc = this.getClass().getResourceAsStream("/assets/minecraft/xylitol/" + file);
            assert audioSrc != null;
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            Clip clip = getClip(audioInputStream, bufferedIn, audioSrc);
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Clip getClip(AudioInputStream audioInputStream, InputStream bufferedIn, InputStream audioSrc) throws LineUnavailableException {
        Clip clip = AudioSystem.getClip();
        clip.addLineListener(event -> {
            if (event.getType().equals(LineEvent.Type.STOP)) {
                if (event.getFramePosition() == clip.getFrameLength()) {
                    try {
                        clip.close();
                        audioInputStream.close();
                        bufferedIn.close();
                        audioSrc.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        return clip;
    }
}
