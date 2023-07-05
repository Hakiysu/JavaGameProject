package cn.itwanho.eliminate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;

public class World extends JPanel {
    //窗口的宽高
    public static final int WIDTH = 429;
    public static final int HEIGHT = 570;
    //一共有8行6列
    public static final int ROWS = 8;
    public static final int COLS = 6;
    public static final int ANIMAL_SIZE = 60;//每个元素的大小
    public static final int OFFSET = 30;//游戏窗口的偏移量
    public static final int ELIMINATE_NONE = 0; //元素不可消
    public static final int ELIMINATE_ROW = 1; //元素行可消
    public static final int ELIMINATE_COL = 2; //元素列可消
    public Element[][] elements = new Element[ROWS][COLS];
    private cn.itwanho.eliminate.MusicPlayer MusicPlayer;
    private int firstRow = 0; //第一个选中的元素的ROW
    private int firstCol = 0; //第一个选中的元素的COL
    private int secondRow = 0; //第二个选中的元素的ROW
    private int secondCol = 0; //第二个选中的元素的COL
    private int selectedNumber = 0; //选中的元素个数
    private boolean canInteractive = true; //是否可以交互
    private int combo = 0; //连击数

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
        repaint();//调用repaint()方法初始化游戏
        //获取鼠标点击事件
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("上一次连击数：" + combo);
                //未响应事件则回调
                if (!canInteractive) {
                    return;
                }
                //如果鼠标点击的位置不在游戏区域内，则不响应
                if (e.getX() < OFFSET || e.getX() > OFFSET + COLS * ANIMAL_SIZE || e.getY() < OFFSET || e.getY() > OFFSET + ROWS * ANIMAL_SIZE) {
                    return;
                }
                //如果鼠标点击的位置在游戏区域内，则响应
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
                    //判断两个元素是否相邻
                    if (checkAdjacent()) {
                        //交换两个元素
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
        };
        this.addMouseListener(mouseAdapter);
    }

    //获取鼠标点击的位置，返回x,y坐标
    public int[] getMouseClickPositionInArray(MouseEvent e) {
        combo = 0;
        int[] position = new int[2];
        position[1] = (e.getX() - OFFSET) / ANIMAL_SIZE;
        position[0] = (e.getY() - OFFSET) / ANIMAL_SIZE;
        return position;
    }

    public boolean checkAdjacent() {
        return (Math.abs(firstRow - secondRow) == 1 && firstCol == secondCol) || (Math.abs(firstCol - secondCol) == 1 && firstRow == secondRow);    //相邻
    }

    //交换元素
    private void swapElements() {
        Element element1 = elements[firstRow][firstCol];
        Element element2 = elements[secondRow][secondCol];
        elements[firstRow][firstCol] = element2;
        elements[secondRow][secondCol] = element1;
        MusicPlayer mmp = new MusicPlayer();
        mmp.playSwapMusic();
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

    //创建元素
    public Element createElement(int row, int col) {
        int x = OFFSET + col * ANIMAL_SIZE;    //列col的值控制x坐标
        int y = OFFSET + row * ANIMAL_SIZE;    //行row的值控制y坐标
        Random random = new Random();//随机数,0~3
        int type = random.nextInt(4);
        return switch (type) {
            case 0 -> new Bear(x, y);
            case 1 -> new Bird(x, y);
            case 2 -> new Fox(x, y);
            default -> new Frog(x, y);
        };

    }

    //填充所有元素
    public void fillAllElement() {
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                //判断行消列消
                do {
                    Element element = createElement(x, y);
                    elements[x][y] = element;   //将元素填充到 elements数组中
                } while (checkEliminate(x, y) != ELIMINATE_NONE);  //若可消则重新生成元素
            }
        }
    }

    //移动元素
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

    //消除元素
    private boolean eliminateElement() {
        boolean haveEliminated = false;
        for (int row = ROWS - 1; row >= 0; row--) {
            for (int col = COLS - 1; col >= 0; col--) {
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
                    combo++;
                    System.out.println("连击数："+combo+"@"+new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                    comboControl();
                }
                if (rowRepeat >= 2) {
                    elements[row][col] = null; //设置当前元素null
                    for (int i = 1; i <= rowRepeat; i++) {
                        //遍历连续个数次
                        elements[row - i][col] = null;   //列不变,行前元素设置为null
                    }
                    haveEliminated = true;  //有可消元素被消除
                    combo++;
                    System.out.println("连击数："+combo+"@"+new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                    comboControl();
                }
            }
        }
        return haveEliminated;
    }

    //元素掉落
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
                        for (int nullCol : nullCols) {
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
                    for (int nullCol : nullCols) {
                        for (int nr = row; nr > 0; nr--) {
                            elements[nr][nullCol] = elements[nr - 1][nullCol];
                        }
                        //生成新元素
                        elements[0][nullCol] = createElement(0, nullCol);
                        MusicPlayer mmp = new MusicPlayer();
                        mmp.playDropMusic();
                    }
                } else {
                    break;
                }
            }
        }
        repaint();
    }

    //得分语音播报
    public void comboControl() {
        MusicPlayer mmp = new MusicPlayer();
        mmp.playScoreSound(combo);
    }

    //绘制游戏界面
    public void paint(Graphics g) {
        Images.background.paintIcon(null, g, 0, 0);
        for (int x = 0; x < ROWS; x++) {
            for (int y = 0; y < COLS; y++) {
                Element element = elements[x][y];
                if (element != null) {
                    element.paintElement(g);
                }
            }
        }
    }
}