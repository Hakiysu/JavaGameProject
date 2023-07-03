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
    //this func plays music only once
    private void playOnce(String filePath){
                try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    stopMusic();
                }
            });

            clip.open(audioStream);
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
    public void playSwapMusic() {
        playOnce("assets/music/swap.wav");
    }
    public void playEliminateMusic() {
        playOnce("assets/music/eliminate.wav");
    }
    public void playDropMusic() {
        playOnce("assets/music/drop.wav");
    }
    public void playGoodMusic() {
        playOnce("assets/music/good.wav");
    }
    public void playGreatMusic() {
        playOnce("assets/music/bad.wav");
    }
    public void playAmazingMusic() {
        playOnce("assets/music/amazing.wav");
    }
    public void playExcellentMusic() {
        playOnce("assets/music/excellent.wav");
    }
    public void playUnbelievableMusic() {
        playOnce("assets/music/unbelievable.wav");
    }
}

