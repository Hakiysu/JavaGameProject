package cn.itwanho.eliminate;

import javax.swing.*;

public class Bird extends Element {

    public Bird(int x, int y) {
        super(x, y);
    }

    @Override
    public ImageIcon getImage() {
        return Images.bird;
    }
}
