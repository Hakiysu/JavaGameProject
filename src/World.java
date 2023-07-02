import cn.itwanho.eliminate.Element;
import cn.itwanho.eliminate.*;

import javax.swing.*;

//window class
public class World extends JPanel {
    //window size
    public static final int WIDTH = 429;
    public static final int HEIGHT = 570;
    //game window row and column
    public static final int ROWS = 8;
    public static final int COLS = 6;
    //animal size
    public static final int ANIMAL_SIZE = 60;
    //offset of game window
    public static final int OFFSET = 30;
    //claim the game element,any elements will be stored in this array
    public Element[][] elements = new Element[ROWS][COLS];

    //insert animal element into Element array,using for loop
    public void importElements() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                //generate random number from 0 to 3
                int random = (int) (Math.random() * 4);
                switch (random) {
                    case 0:
                        elements[i][j] = new Fox(i, j);
                        break;
                    case 1:
                        elements[i][j] = new Frog(i, j);
                        break;
                    case 2:
                        elements[i][j] = new Bear(i, j);
                        break;
                    case 3:
                        elements[i][j] = new Bird(i, j);
                        break;
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        World world = new World();//创建窗口中的那一堆对象
        world.setFocusable(true);// 将控件设置成可获取焦点状态
        frame.add(world);
        //设置用户在此窗体上发起 "close" 时默认执行的操作，EXIT_ON_CLOSE：使用 System exit 方法退出应用程序
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT + 17);//17是窗口标题栏的高度
        frame.setLocationRelativeTo(null);//使窗口显示在屏幕中央
        frame.setVisible(true);//自动调用paint()方法
    }
}
