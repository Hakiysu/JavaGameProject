import cn.itwanho.eliminate.Element;
import cn.itwanho.eliminate.Images;

import javax.swing.*;
import java.awt.*;

//window class
public class World extends JPanel {
    //window size
    public static final int WIDTH = 429;
    public static final int HEIGHT = 570;
    //game window row and column
    public static final int ROWS = 6;
    public static final int COLS = 8;
    //animal size
    public static final int ANIMAL_SIZE = 60;
    //offset of game window
    public static final int OFFSET = 30;
    //claim the game element,any elements will be stored in this array
    public Element[][] elements = new Element[ROWS][COLS];

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        World world = new World();//创建窗口中的那一堆对象
        world.importElements();
        world.setFocusable(true);// 将控件设置成可获取焦点状态
        frame.add(world);
        //设置用户在此窗体上发起 "close" 时默认执行的操作，EXIT_ON_CLOSE：使用 System exit 方法退出应用程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT + 17);//17是窗口标题栏的高度
        frame.setLocationRelativeTo(null);//使窗口显示在屏幕中央
        frame.setVisible(true);//自动调用paint()方法
    }

    //insert animal element into Element array,using for loop
    public void importElements() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                //generate random number from 0 to 3
                int random = (int) (Math.random() * 4);
                //switch the random number to generate different animal
                switch (random) {
                    case 0 -> elements[i][j] = new Element(i, j, Images.fox);
                    case 1 -> elements[i][j] = new Element(i, j, Images.frog);
                    case 2 -> elements[i][j] = new Element(i, j, Images.bear);
                    case 3 -> elements[i][j] = new Element(i, j, Images.bird);
                }
            }
        }
    }

    //print the game window
    public void paint(Graphics g) {
        //draw the background
        g.drawImage(Images.background.getImage(), 0, 0, null);
        //draw the game window
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Element element = elements[i][j];
                g.drawImage(
                        element.getImage().getImage(),
                        element.getCol() * ANIMAL_SIZE + OFFSET,
                        element.getRow() * ANIMAL_SIZE + OFFSET,
                        null
                );
            }
        }
    }

    //get mouse click position,return the row and column of the element
    public Element getAnimal(int x, int y) {
        int col = (x - OFFSET) / ANIMAL_SIZE;
        int row = (y - OFFSET) / ANIMAL_SIZE;
        return elements[row][col];
    }
}
