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

    public Element(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.eliminated = false;
        this.boomAnimeIndexForElement = 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int i) {
        this.x = i;
    }

    public int getY() {
        return y;
    }

    public void setY(int i) {
        this.y = i;
    }

    public ImageIcon getImage() {
        return image;
    }

    public boolean getEliminated() {
        return eliminated;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }

    //绘制元素图片
    public void paintElement(Graphics g) {
        //选中状态
        if (getSelected()) {//如果元素状态为选中，打印绿色方块背景
            g.setColor(Color.GREEN);
            g.fillRect(x, y, World.ANIMAL_SIZE, World.ANIMAL_SIZE);//先画绿色背景
            this.getImage().paintIcon(null, g, this.x, this.y);//再画图片
        } else if (getEliminated()) {//如果元素状态为消除，打印爆炸图，图片播放完后播放爆炸音乐
            //若没到最后一张爆炸图
            if (boomAnimeIndexForElement < Images.bombs.length) {
                Images.bombs[boomAnimeIndexForElement++].paintIcon(null, g, x, y);//打印爆炸图
                //若到了最后一张爆炸图,播放音乐
                if (boomAnimeIndexForElement == Images.bombs.length) {
                    MusicPlayer mmp = new MusicPlayer();//multiple music player instances
                    mmp.playEliminateMusic();
                }
            }
        } else {
            //既不是选中状态，也不是消除状态，打印图片就行
            this.getImage().paintIcon(null, g, this.x, this.y);
        }
    }
}
