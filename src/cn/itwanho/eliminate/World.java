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
        //游戏一开始棋盘为空，调用fillAllElement()方法填充所有元素
        fillAllElement();//填充所有元素，初始化游戏
        repaint();//调用repaint()方法初始化游戏
        //获取鼠标点击事件
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("上一次连击数：" + combo);//打印上一次连击数，此时combo未重置，在下方换算坐标时重置
                //判断此时游戏状态是否可交互
                if (!canInteractive) {
                    return;
                }
                //判断鼠标点击的位置在不在游戏区域内
                if (e.getX() < OFFSET || e.getX() > OFFSET + COLS * ANIMAL_SIZE || e.getY() < OFFSET || e.getY() > OFFSET + ROWS * ANIMAL_SIZE) {
                    return;
                }
                //如果鼠标点击的位置在游戏区域内，则响应
                canInteractive = false;//设置游戏状态为不可交互，防止在执行元素交换进程时，用户再次点击
                int[] position = getMouseClickPositionInArray(e);//获取鼠标点击的位置，返回棋盘坐标存入position数组
                selectedNumber++;//选中个数加1，游戏开始时为0，第一次点击为1，第二次点击为2
                if (selectedNumber == 1) {//第一次选择元素，保存第一个元素的棋盘坐标，更改元素的选中状态，仍然保持游戏状态为可交互，接受第二次点击
                    firstRow = position[0];
                    firstCol = position[1];
                    elements[firstRow][firstCol].setSelected(true);
                    canInteractive = true;
                } else if (selectedNumber == 2) {//第二次选择元素，保存第二个元素的棋盘坐标，更改元素的选中状态
                    secondRow = position[0];
                    secondCol = position[1];
                    elements[secondRow][secondCol].setSelected(true);
                    //此时已经选择2个元素了，开始判断两个元素是否相邻
                    if (checkAdjacent()) {
                        //元素相邻，开始交换元素
                        //启动一个线程用于元素交换事件
                        new Thread(() -> {
                            elements[firstRow][firstCol].setSelected(false);//取消选中状态
                            elements[secondRow][secondCol].setSelected(false);//取消选中状态
                            //移动、交换、消除
                            moveElement();//移动两个元素
                            swapElements();//交换两个元素
                            if (eliminateElement()) {//判断是否有可消元素，如果有就消除并执行以下操作
                                do {
                                    dropElement();//元素下落动画及棋盘数据更新
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                } while (eliminateElement());//持续扫描，直至没有可消元素
                            } else {
                                //若没有可消元素，还原之前已经移动、交换的两个元素，再次操作以还原
                                moveElement();//还原移动两个元素
                                swapElements();//还原交换两个元素
                            }
                            canInteractive = true;//设置游戏状态为可交互
                        }).start();
                    } else {
                        //元素不相邻，设置元素选中状态为false，设置游戏状态为可交互
                        elements[firstRow][firstCol].setSelected(false); //取消选中状态
                        elements[secondRow][secondCol].setSelected(false); //取消选中状态
                        canInteractive = true;//设置游戏状态为可交互
                    }
                    canInteractive = true;//在某种条件下可交互
                    selectedNumber = 0;//选中个数归零
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

    //检查两个元素是否相邻
    public boolean checkAdjacent() {
        //如果两个选中元素的行or列相同，则相减后绝对值为1，则相邻
        //否则不相邻
        return (Math.abs(firstRow - secondRow) == 1 && firstCol == secondCol) || (Math.abs(firstCol - secondCol) == 1 && firstRow == secondRow);
        //相邻
    }

    //交换元素
    private void swapElements() {//操作棋盘内元素
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
            Element element1 = elements[row - 1][col];//获取当前元素上面第1个元素
            Element element2 = elements[row - 2][col];//获取当前元素上面第2个元素
            if (element1 != null && element2 != null && element != null) {
                //若元素都不为null
                if (element.getClass().equals(element1.getClass()) && element.getClass().equals(element2.getClass())) {
                    return ELIMINATE_COL; //表示列可消除
                }
            }
        }
        //判断横向
        if (col >= 2) {
            Element element1 = elements[row][col - 1];//获取当前元素前面第1个元素
            Element element2 = elements[row][col - 2];//获取当前元素前面第2个元素
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
        //col,row为棋盘坐标
        //x,y为计算后在程序内部的绝对像素点坐标
        int x = OFFSET + col * ANIMAL_SIZE;//列col的值控制x坐标
        int y = OFFSET + row * ANIMAL_SIZE;//行row的值控制y坐标
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
                    elements[x][y] = element;//将元素填充到 elements数组中
                } while (checkEliminate(x, y) != ELIMINATE_NONE);//若可消则重新生成元素
            }
        }
    }

    //移动元素
    private void moveElement() {//动画效果
        if (firstRow == secondRow) {
            //若行号相同，表示左右移动
            int firstX = OFFSET + firstCol * ANIMAL_SIZE;
            int secondX = OFFSET + secondCol * ANIMAL_SIZE;
            int step = firstX < secondX ? 4 : -4;//设置步长
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
            int step = firstY < secondY ? 4 : -4;//设置步长
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
        boolean haveEliminated = false;//棋盘中是否有元素可消
        for (int row = ROWS - 1; row >= 0; row--) {
            for (int col = COLS - 1; col >= 0; col--) {
                Element element = elements[row][col];
                if (element == null) {//若元素为null 则跳过
                    continue;
                }
                //查找一行内当前元素前面的连续个数, 查找一列内当前元素前面的连续个数
                int colRepeat = 0;//行不变，列相邻，与当前元素相邻的行元素连续重复个数
                for (int pc = col - 1; pc >= 0; pc--) {
                    if (elements[row][pc] == null) {
                        //若当前元素为null则break 直接退出
                        break;
                    }
                    //若遍历元素与当前元素类型相同，重复个数增1，否则 break
                    if (elements[row][pc].getClass() == element.getClass()) {
                        colRepeat++;
                    } else {
                        break;//只要右一个不同，后续不需比较
                    }
                }
                int rowRepeat = 0;//列不变，行相邻，与当前元素相邻的列元素连续重复个数
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
        for (int row = ROWS - 1; row >= 0; row--) {//col:列，row:行
            //从最底下一行往上遍历
            while (true) {
                //只要某列有null元素，就将此列中它上面的元素落下
                int[] nullCols = {};//用于保存含null元素的列号，当前行号=有元素为null的列号,初始化为0
                for (int col = COLS - 1; col >= 0; col--) {//从最右边一列往左遍历
                    Element element = elements[row][col];//获取当前元素
                    if (element == null) {//若当前元素为null
                        nullCols = Arrays.copyOf(nullCols, nullCols.length + 1);//扩容
                        nullCols[nullCols.length - 1] = col;//将当前列号存入nullCols数组
                    }
                }
                if (nullCols.length > 0) {//如果nullCols数组长度大于0，说明棋盘中这一行有null元素，需要下落
                    //下落动画
                    for (int count = 0; count < 15; count++) {
                        //15*4=60，下落60像素，正好是一个元素的高度
                        //向下落一个元素的高度
                        for (int nullCol : nullCols) {//遍历nullCols数组，获取每个null列号，从右往左遍历(因为保存的是从右往左的列号)
                            for (int dropRow = row - 1; dropRow >= 0; dropRow--) {//dropRow:下落行号，从当前行的上一行开始，到第一行结束；row行必定有null元素，否则判断nullCols.length>0不成立
                                //这里双层for循环仅遍历部分列中所有元素，因为nullCols数组中保存的是有null元素的列号，而且是从右往左的列号
                                Element element = elements[dropRow][nullCol];//获取当前元素
                                if (element != null) {//若当前元素不为null
                                    element.setY(element.getY() + 4);//将非null元素向下移动4像素
                                }
                            }
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //此行下落元素位置全部改变后，进行重绘，视觉上是一起下落的
                        repaint();
                    }
                    //棋盘数组上面的元素向下移动
                    for (int nullCol : nullCols) {//遍历nullCols数组，获取每个null列号，从右往左遍历(因为保存的是从右往左的列号)
                        for (int nullRow = row; nullRow > 0; nullRow--) {//遍历null列上面的元素，从最后一行开始，到第一行结束
                            elements[nullRow][nullCol] = elements[nullRow - 1][nullCol];//将上一行元素赋值给当前行
                        }
                        //将null列上面的元素全部下移后，第一行元素调用createElement方法重新生成
                        elements[0][nullCol] = createElement(0, nullCol);
                        MusicPlayer mmp = new MusicPlayer();
                        mmp.playDropMusic();//播放下落音效
                    }
                } else {
                    break;
                }
            }
        }
        //多行消除时，下落动画重绘
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