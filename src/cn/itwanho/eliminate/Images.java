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
            System.out.println(background.getImageLoadStatus());    //返回8表示成功
            System.out.println(bird.getImageLoadStatus());    //返回8表示成功
            System.out.println(frog.getImageLoadStatus());    //返回8表示成功
            System.out.println(fox.getImageLoadStatus());    //返回8表示成功
            System.out.println(bear.getImageLoadStatus());    //返回8表示成功
            for (ImageIcon bomb : bombs) {
                System.out.println(bomb.getImageLoadStatus());
            }
        }
}
