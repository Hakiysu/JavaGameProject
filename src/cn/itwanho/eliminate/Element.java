package cn.itwanho.eliminate;

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

    public Element(int x, int y) {
        this.x = x;
        this.y = y;
        this.selected = false;
        this.eliminated = false;
        this.boomAnimeIndexForElement = 0;
    }
}
