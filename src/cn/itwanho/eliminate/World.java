package cn.itwanho.eliminate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

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
    public static final int ELEMENT_TYPE_BEAR = 0;  //熊
    public static final int ELEMENT_TYPE_BIRD = 1;  //鸟
    public static final int ELEMENT_TYPE_FOX = 2;  //狐狸
    public static final int ELEMENT_TYPE_FROG = 3;  //青蛙
    public static final int ELIMINATE_NONE = 0; //元素不可消
    public static final int ELIMINATE_ROW = 1; //元素行可消
    public static final int ELIMINATE_COL = 2; //元素列可消
    //claim the game element,any elements will be stored in this array
    public Element[][] elements = new Element[ROWS_ELEMENT][COLS_ELEMENT];
    //eliminate stack,size is row_e*col_e
    public Stack<Element> eliminateStack = new Stack<>();
    private cn.itwanho.eliminate.MusicPlayer MusicPlayer;
    private int firstRow = 0; //第一个选中的元素的ROW
    private int firstCol = 0; //第一个选中的元素的COL
    private int secondRow = 0; //第二个选中的元素的ROW
    private int secondCol = 0; //第二个选中的元素的COL
    private boolean canInteractive = true; //是否可以交互
    private int selectedNumber = 0; //选中的元素个数
    private boolean selected; //是否选中


    public static void main(String[] args) {
        JFrame frame = new JFrame();
        World world = new World();//创建窗口中的那一堆对象
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
        fillAllElement();
        new Thread(() -> {
            //get mouse click event
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    //if can't interactive,return
                    if (!canInteractive) {
                        return;
                    }
                    //if mouse click position is out of game window,return
                    if (e.getX() < OFFSET || e.getX() > OFFSET + COLS * ANIMAL_SIZE || e.getY() < OFFSET || e.getY() > OFFSET + ROWS * ANIMAL_SIZE) {
                        return;
                    }
                    //get mouse click position in array
                    canInteractive = false;
                    int[] position = getMouseClickPositionInArray(e);
                    selectedNumber++;
                    if (selectedNumber == 1) {
                        firstRow = position[0];
                        firstCol = position[1];
                        elements[firstRow][firstCol].setSelected(true);
                        canInteractive = true;
                    } else if (selectedNumber == 2) {
                        secondRow = position[0];
                        secondCol = position[1];
                        elements[secondRow][secondCol].setSelected(true);
                        canInteractive = true;
                        //if two elements are adjacent
                        if (checkAdjacent()) {
                            //if two elements can be eliminated
                            new Thread(() -> {
                                elements[firstRow][firstCol].setSelected(false); //取消选中状态
                                elements[secondRow][secondCol].setSelected(false); //取消选中状态
                                //移动、交换、消除
                                moveElement();      //移动两个元素
                                swapElements();  //交换两个元素
                                if (eliminateElement()) { //若有可消元素，并消除
                                    //下落元素
                                    do {
                                        dropElement();
                                        try {
                                            Thread.sleep(10);
                                        } catch (InterruptedException ex) {
                                            ex.printStackTrace();
                                        }
                                    } while (eliminateElement());    //持续扫描

                                } else {
                                    //交换回去
                                    moveElement();      //移动两个元素
                                    swapElements();  //交换两个元素
                                }

                                canInteractive = true;  //可交互


                            }).start();
                        } else {

                            elements[firstRow][firstCol].setSelected(false); //取消选中状态
                            elements[secondRow][secondCol].setSelected(false); //取消选中状态
                            canInteractive = true;//可交互
                        }
                        canInteractive = true;//在某种条件下可交互
                        selectedNumber = 0; //选中个数归零
                    }
                    repaint();
                }
            });
        }).start();

    }




    //print the game window
    public void paint(Graphics g) {
        Images.background.paintIcon(null, g, 0, 0);
        for (int row = 0; row < ROWS_ELEMENT; row++) {
            for (int col = 0; col < COLS_ELEMENT; col++) {
                Element element = elements[row][col];
                if (element != null) {
                    element.paintElement(g);
                }
            }
        }
    }
    private boolean isSelected() {
        return selected;
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
    public boolean checkAdjacent() {
        return (Math.abs(firstRow - secondRow) == 1 && firstCol == secondCol) || (Math.abs(firstCol - secondCol) == 1 && firstRow == secondRow);
    }

    //交换元素
    private void swapElements() {
        Element element1 = elements[firstRow][firstCol];
        Element element2 = elements[secondRow][secondCol];
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

    private void moveElement() {
        if (firstRow == secondRow) {
            //若行号相同，表示左右移动
            int firstX = OFFSET + firstCol * ANIMAL_SIZE;
            int secondX = OFFSET + secondCol * ANIMAL_SIZE;
            int step = firstX < secondX ? 4 : -4;  //设置步长
            for (int i = 0; i < 15; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                firstX += step;
                secondX -= step;
                //修改元素坐标
                elements[firstRow][firstCol].setX(firstX);
                elements[secondRow][secondCol].setX(secondX);

                repaint();
            }

        }
        if (firstCol == secondCol) {
            //若列号相同，表示上下移动
            int firstY = OFFSET + firstRow * ANIMAL_SIZE;
            int secondY = OFFSET + secondRow * ANIMAL_SIZE;
            int step = firstY < secondY ? 4 : -4;  //设置步长
            for (int i = 0; i < 15; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                firstY += step;
                secondY -= step;
                //修改元素坐标
                elements[firstRow][firstCol].setY(firstY);
                elements[secondRow][secondCol].setY(secondY);

                repaint();
            }
        }
    }

    private boolean eliminateElement() {
        boolean haveEliminated = false;
        for (int row = ROWS_ELEMENT - 1; row >= 0; row--) {
            for (int col = COLS_ELEMENT - 1; col >= 0; col--) {
                Element element = elements[row][col];
                if (element == null) {   //若元素为null 则跳过
                    continue;
                }
                //查找一行内当前元素前面的连续个数, 查找一列内当前元素前面的连续个数
                int colRepeat = 0; //行不变，列相邻，与当前元素相邻的行元素连续重复个数
                for (int pc = col - 1; pc >= 0; pc--) {
                    if (elements[row][pc] == null) {
                        //若当前元素为null 则break 直接退出
                        break;
                    }
                    //若遍历元素与当前元素类型相同，重复个数增1，否则 break
                    if (elements[row][pc].getClass() == element.getClass()) {
                        colRepeat++;
                    } else {
                        break;  //只要右一个不同，后续不需比较
                    }
                }
                int rowRepeat = 0; //列不变，行相邻，与当前元素相邻的列元素连续重复个数
                for (int pr = row - 1; pr >= 0; pr--) {
                    if (elements[pr][col] == null) {
                        //若当前元素为null 则break 直接退出
                        break;
                    }
                    //若遍历元素与当前元素类型相同，重复个数增1，否则 break
                    if (elements[pr][col].getClass() == element.getClass()) {
                        rowRepeat++;
                    } else {
                        break;  //只要右一个不同，后续不需比较
                    }
                }
                //将可消除元素设计为可消除状态
                if (colRepeat >= 2) {
                    //行不变，列相邻
                    elements[row][col].setEliminated(true); //设置当前元素可消除
                    for (int i = 1; i <= colRepeat; i++) {
                        //遍历连续个数次
                        elements[row][col - i].setEliminated(true);   //行不变,列前元素设置为可消
                    }

                }
                if (rowRepeat >= 2) {
                    //列不变，行相邻
                    elements[row][col].setEliminated(true); //设置当前元素可消除
                    for (int i = 1; i <= rowRepeat; i++) {
                        //遍历连续个数次
                        elements[row - i][col].setEliminated(true);   //列不变,行前元素设置为可消
                    }
                }
                //将可消除状态元素绘制成爆炸动画
                if (colRepeat >= 2 || rowRepeat >= 2) {
                    for (int i = 0; i < Images.bombs.length; i++) {
                        repaint();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //将可消除状态元素设置为null 等待其他元素下落
                if (colRepeat >= 2) {
                    elements[row][col] = null; //设置当前元素null
                    for (int i = 1; i <= colRepeat; i++) {
                        //遍历连续个数次
                        elements[row][col - i] = null;   //行不变,列前元素设置为null
                    }
                    haveEliminated = true;  //有可消元素被消除
                }
                if (rowRepeat >= 2) {
                    elements[row][col] = null; //设置当前元素null
                    for (int i = 1; i <= rowRepeat; i++) {
                        //遍历连续个数次
                        elements[row - i][col] = null;   //列不变,行前元素设置为null
                    }
                    haveEliminated = true;  //有可消元素被消除
                }
            }
        }
        return haveEliminated;
    }

    private void dropElement() {
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
                        for (int i = 0; i < nullCols.length; i++) {
                            int nullCol = nullCols[i];
                            for (int dr = row - 1; dr >= 0; dr--) {
                                Element element = elements[dr][nullCol];
                                if (element != null) {
                                    element.setY(element.getY() + 4);
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
                    for (int i = 0; i < nullCols.length; i++) {
                        int nullCol = nullCols[i];
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
}