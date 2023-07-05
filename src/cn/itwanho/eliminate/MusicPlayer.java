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

    private void playGoodMusic() {
        playOnce("assets/music/good.wav");
    }

    private void playGreatMusic() {
        playOnce("assets/music/great.wav");
    }

    private void playAmazingMusic() {
        playOnce("assets/music/amazing.wav");
    }

    private void playExcellentMusic() {
        playOnce("assets/music/excellent.wav");
    }

    private void playUnbelievableMusic() {
        playOnce("assets/music/unbelievable.wav");
    }

    public void playScoreSound(int score) {
        System.out.println("score: " + score);
        switch (score) {
            case 0:
                break;
            case 1:
                playGoodMusic();
                break;
            case 2:
                playGreatMusic();
                break;
            case 3:
                playAmazingMusic();
                break;
            case 4:
                playExcellentMusic();
                break;
            default:
                playUnbelievableMusic();
                break;
        }
    }
}

