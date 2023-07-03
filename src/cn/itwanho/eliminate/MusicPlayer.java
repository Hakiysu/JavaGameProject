package cn.itwanho.eliminate;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;

    private void playMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
    public void pauseMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    public void resumeMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }
    public void playBackgroundMusic() {
        playMusic("assets/music/bg.wav");
    }
    public void playMainMusic() {
        playMusic("assets/music/main.wav");
    }
}

