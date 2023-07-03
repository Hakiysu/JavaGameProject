package cn.itwanho.eliminate;

import javax.swing.*;

public class Element {
    //position of the element
    private int x;
    private int y;
    //selected status of the element
    private boolean selected;
    //can this element be eliminated
    private boolean eliminated;
    //boom anime for the element
    //anime is 4 pictures,save in a array
    //start from 0,then play 1,2,3,then stop
    private int boomAnimeIndexForElement;
    //image
    private ImageIcon image;
    //name
    private String name;

    public Element(int x, int y, ImageIcon image,String name) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.eliminated = false;
        this.boomAnimeIndexForElement = 0;
        this.image = image;
        this.name = name;
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
