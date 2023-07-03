package cn.itwanho.eliminate;

import javax.swing.*;

public class Images {
    public static ImageIcon background;
    public static ImageIcon fox;
    public static ImageIcon frog;
    public static ImageIcon bear;
    public static ImageIcon bird;
    public static ImageIcon[] bombs;

    static {
        background = new ImageIcon("assets/img/background.png");
        fox = new ImageIcon("assets/img/fox.png");
        frog = new ImageIcon("assets/img/frog.png");
        bear = new ImageIcon("assets/img/bear.png");
        bird = new ImageIcon("assets/img/bird.png");
        bombs = new ImageIcon[4];
        for (int i = 0; i < bombs.length; i++) {
            bombs[i] = new ImageIcon("assets/img/bom" + (i + 1) + ".png");
        }
    }

    public static void main(String[] args) {
        //for test pictures
        //print 8 means the picture is loaded successfully
        System.out.println(background.getImageLoadStatus());
        System.out.println(fox.getImageLoadStatus());
        System.out.println(frog.getImageLoadStatus());
        System.out.println(bear.getImageLoadStatus());
        System.out.println(bird.getImageLoadStatus());
        for (int i = 0; i < bombs.length; i++) {
            System.out.println(bombs[i].getImageLoadStatus());
        }

    }
}
