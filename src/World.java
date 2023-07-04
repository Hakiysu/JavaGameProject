import cn.itwanho.eliminate.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.Stack;
import java.util.Arrays;

import static cn.itwanho.eliminate.AnimeDraw.playBoomAnime;
import static cn.itwanho.eliminate.AnimeDraw.playSwapAnime;

//window class
public class World extends JPanel {
    //window size
    public static final int WIDTH = 429;
    public static final int HEIGHT = 570;
    //game window row and column
    public static final int ROWS = 8;//game have 8 rows
    public static final int COLS = 6;//game have 6 columns
    //each row have 6 elements
    //each column have 8 elements
    public static final int ROWS_ELEMENT = 6;
    public static final int COLS_ELEMENT = 8;
    //animal size
    public static final int ANIMAL_SIZE = 60;
    //offset of game window
    public static final int OFFSET = 30;
    //claim the game element,any elements will be stored in this array
    public Element[][] elements = new Element[ROWS_ELEMENT][COLS_ELEMENT];
    //eliminate stack,size is row_e*col_e
    public Stack<Element> eliminateStack = new Stack<>();
    private cn.itwanho.eliminate.MusicPlayer MusicPlayer;
    public static final int ELEMENT_TYPE_BEAR = 0;  //熊
    public static final int ELEMENT_TYPE_BIRD = 1;  //鸟
    public static final int ELEMENT_TYPE_FOX = 2;  //狐狸
    public static final int ELEMENT_TYPE_FROG = 3;  //青蛙

    public static final int ELIMINATE_NONE = 0; //元素不可消
    public static final int ELIMINATE_ROW = 1; //元素行可消
    public static final int ELIMINATE_COL = 2; //元素列可消
    private int firstRow = 0; //第一个选中的元素的ROW
    private int firstCol = 0; //第一个选中的元素的COL
    private int secondRow = 0; //第二个选中的元素的ROW
    private int secondCol = 0; //第二个选中的元素的COL

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        World world = new World();//创建窗口中的那一堆对象
        world.generateRandomGameArray();
        world.setFocusable(true);// 将控件设置成可获取焦点状态
        frame.add(world);
        //设置用户在此窗体上发起 "close" 时默认执行的操作，EXIT_ON_CLOSE：使用 System exit 方法退出应用程序
        frame.setTitle("开心消消乐小游戏");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT + 17);//17是窗口标题栏的高度
        frame.setLocationRelativeTo(null);//使窗口显示在屏幕中央
        frame.setVisible(true);//自动调用paint()方法
        //start game
        world.MusicPlayer = new MusicPlayer();
        world.MusicPlayer.playBackgroundMusic();
        world.startGameLoop();
    }

    private void startGameLoop() {
        while (true) {
            //repaint the game window
            repaint();
            try {
                Thread.sleep(1000);//FPS
                //create a thread to get mouse click
                new Thread(() -> {
                    //get mouse click event
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            //get the position of mouse click
                            int[] position = getMouseClickPositionInArray(e);
                            //get the element by position
                            Element element = elements[position[0]][position[1]];
                            //if the element is not selected,select it
                            //if the element is selected,unselect it
                            element.setSelected(!element.isSelected());
                            //check if there are two elements selected
                            int count = 0;
                            Element element1 = null;
                            Element element2 = null;
                            for (int i = 0; i < ROWS_ELEMENT; i++) {
                                for (int j = 0; j < COLS_ELEMENT; j++) {
                                    if (elements[i][j].isSelected()) {
                                        count++;
                                        if (count == 1) {
                                            element1 = elements[i][j];
                                        } else if (count == 2) {
                                            element2 = elements[i][j];
                                        }
                                    }

                                }
                            }
                            //destroy element
                            element = null;
                            System.out.println("element" + Arrays.deepToString(elements));
                            //check between two elements
                            if (count == 2) {
                                System.out.println("Do check adjacent");
                                //if two elements are neighbor,swap them
                                if (checkAdjacent(element1, element2)) {
                                    //swap two elements
                                    swapElements(element1, element2);
                                    //play swap anime
                                    playSwapAnime(element1, element2);
                                    //unselect two elements
                                    element1.setSelected(false);
                                    element2.setSelected(false);
                                    System.out.println("Adjacent!");
                                } else {
                                    //if two elements are not neighbor,unselect them
                                    element1.setSelected(false);
                                    element2.setSelected(false);
                                    //play swap anime
                                    playSwapAnime(element1, element2);
                                    //repaint the game window
                                }
                            }
                            repaint();
                        }
                    });
                }).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //insert animal element into Element array,using for loop
    public void generateRandomGameArray() {
        fillAllElement();
    }

    //print the game window
    public void paint(Graphics g) {
        //draw the background
        g.drawImage(Images.background.getImage(), 0, 0, null);
        //draw the game window
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                //check element is selected or not
                //if selected,draw a green rectangle background,then draw the animal above the background
                //if not selected,just draw the animal
                if (elements[i][j].isSelected()) {
                    //draw green rectangle background,using fillRect()
                    g.setColor(Color.GREEN);
                    g.fillRect(OFFSET + i * ANIMAL_SIZE, OFFSET + j * ANIMAL_SIZE, ANIMAL_SIZE, ANIMAL_SIZE);
                    g.drawImage(elements[i][j].getImage().getImage(), OFFSET + i * ANIMAL_SIZE, OFFSET + j * ANIMAL_SIZE, null);
                } else {
                    g.drawImage(elements[i][j].getImage().getImage(), OFFSET + i * ANIMAL_SIZE, OFFSET + j * ANIMAL_SIZE, null);
                }
            }
        }
    }

    //get position by listening mouse click event,then return the position:x,y
    public int[] getMouseClickPositionInArray(MouseEvent e) {
        int[] position = new int[2];
        position[0] = (e.getX() - OFFSET) / ANIMAL_SIZE;
        position[1] = (e.getY() - OFFSET) / ANIMAL_SIZE;
        //show choose anime
        return position;
    }


    //Input:two elements itself,not position
    //Process:check if two elements are adjacent
    //Output:boolean,if two elements are adjacent,return true,else return false
    public boolean checkAdjacent(Element element1, Element element2) {
        firstRow = element1.getRow();
        firstCol = element1.getCol();
        secondRow = element2.getRow();
        secondCol = element2.getCol();
        //若行相邻且列相等  或 列相邻且行相等
        if ((Math.abs(firstRow - secondRow) == 1 && firstCol == secondCol) || (Math.abs(firstCol - secondCol) == 1 && firstRow == secondRow)) {
            return true;    //相邻
        } else {
            return false;    //不相邻
        }
    }


    //交换元素
    private void swapElements(Element element1, Element element2) {
        elements[firstRow][firstCol] = element2;
        elements[secondRow][secondCol] = element1;
    }

    //检查消除
    public int checkEliminate(int row, int col) {
        Element element = elements[row][col];   //获取当前元素
        //判断纵向
        if (row >= 2) {
            Element element1 = elements[row - 1][col];  //获取当前元素上面第1个元素
            Element element2 = elements[row - 2][col];  //获取当前元素上面第2个元素
            if (element1 != null && element2 != null && element != null) {
                //若元素都不为null
                if (element.getClass().equals(element1.getClass()) && element.getClass().equals(element2.getClass())) {
                    return ELIMINATE_COL; //表示列可消除
                }
            }
        }

        //判断横向
        if (col >= 2) {
            Element element1 = elements[row][col - 1];  //获取当前元素前面第1个元素
            Element element2 = elements[row][col - 2];  //获取当前元素前面第2个元素
            if (element1 != null && element2 != null && element != null) {
                //若元素都不为null
                if (element.getClass().equals(element1.getClass()) && element.getClass().equals(element2.getClass())) {
                    return ELIMINATE_ROW; //表示行可消除
                }
            }
        }

        return ELIMINATE_NONE;  //表示不能消除
    }

    //drop elements after eliminate
    //if there are empty elements,drop the elements above them
    //generate random elements to fill the empty elements
    private void dropElements() {
        for (int row = ROWS - 1; row >= 0; row--) {
            //只要有null 元素就将它上面的元素落下
            while (true) {
                int[] nullCols = {};    //当前行为null的列号

                for (int col = COLS - 1; col >= 0; col--) {
                    Element element = elements[row][col];
                    if (element == null) {
                        nullCols = Arrays.copyOf(nullCols, nullCols.length + 1);
                        nullCols[nullCols.length - 1] = col;
                    }
                }

                //查找null 列
                if (nullCols.length > 0) {
                    //移动下落元素
                    for (int count = 0; count < 15; count++) {
                        // 向下落一下
                        for (int nullCol : nullCols) {
                            for (int dr = row - 1; dr >= 0; dr--) {
                                Element element = elements[dr][nullCol];
                                if (element != null) {
                                    element.setRow(element.getRow() + 1);
                                    element.setCol(nullCol);
                                }
                            }
                        }


                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        repaint();
                    }

                    //真正让数组上面的元素向下移动
                    for (int nullCol : nullCols) {
                        for (int nr = row; nr > 0; nr--) {
                            elements[nr][nullCol] = elements[nr - 1][nullCol];
                        }
                        //生成新元素
                        elements[0][nullCol] = createElement(0, nullCol);
                    }

                } else {
                    break;
                }
            }
        }

    }

    public Element createElement(int row, int col) {
        int x = OFFSET + col * ANIMAL_SIZE;    //列col的值控制x坐标
        int y = OFFSET + row * ANIMAL_SIZE;    //行row的值控制y坐标
        Random random = new Random();
        int type = random.nextInt(4);
        return switch (type) {
            case ELEMENT_TYPE_BEAR -> new Bear(x, y);
            case ELEMENT_TYPE_BIRD -> new Bird(x, y);
            case ELEMENT_TYPE_FOX -> new Fox(x, y);
            default -> new Frog(x, y);
        };

    }

    public void fillAllElement() {
        for (int row = 0; row < ROWS_ELEMENT; row++) {
            for (int col = 0; col < COLS_ELEMENT; col++) {
                //判断行消列消
                do {
                    Element element = createElement(row, col);
                    elements[row][col] = element;   //将元素填充到 elements数组中
                } while (checkEliminate(row, col) != ELIMINATE_NONE);  //若可消则重新生成元素

            }
        }
    }
}