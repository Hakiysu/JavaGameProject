package cn.itwanho.eliminate;

import javax.swing.*;
import java.awt.*;

public abstract class Element {
    //position of the element
    private int x;
    private int y;
    //selected status of the element
    private boolean selected;
    //can this element be eliminated
    private boolean eliminated;
    //爆炸图片计数
    private int boomAnimeIndexForElement;
    //image
    private ImageIcon image;
    //name
    private String name;

    public Element(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.eliminated = false;
        this.boomAnimeIndexForElement = 0;
    }

    public int getCol() {
        return x;
    }

    public void setCol(int i) {
        this.x = i;
    }

    public int getRow() {
        return y;
    }

    public void setRow(int i) {
        this.y = i;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon bomb) {
        this.image = bomb;
    }
    public void paintElement(Graphics g) {
        if (isSelected()) {
            g.setColor(Color.GREEN);
            //g.fillRect(x, y, World.ANIMAL_SIZE, World.ANIMAL_SIZE);
            this.getImage().paintIcon(null, g, this.x, this.y);

        } else if (isEliminated()) {
            //若没到最后一张爆炸图
            if (boomAnimeIndexForElement < Images.bombs.length) {
                Images.bombs[boomAnimeIndexForElement++].paintIcon(null, g, x, y);
            }

        } else {
            this.getImage().paintIcon(null, g, this.x, this.y);
        }
    }

    public boolean isEliminated() {
        return eliminated;
    }

    public int getBoomAnimeIndexForElement() {
        return boomAnimeIndexForElement;
    }

    public void setBoomAnimeIndexForElement(int i) {
        this.boomAnimeIndexForElement = i;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
