package cn.itwanho.eliminate;

import javax.swing.*;

public class Bear extends Element {

    public Bear(int x, int y) {
        super(x, y);
    }

    @Override
    public ImageIcon getImage() {
        return Images.bear;
    }
}
