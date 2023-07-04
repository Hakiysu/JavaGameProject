import cn.itwanho.eliminate.Element;
import cn.itwanho.eliminate.Images;
import cn.itwanho.eliminate.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

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
        world.MusicPlayer=new MusicPlayer();
        world.MusicPlayer.playBackgroundMusic();
        world.startGameLoop();
    }

    private void startGameLoop() {
        // TODO Auto-generated method stub
        while (true) {
            //repaint the game window
            repaint();
            try {
                Thread.sleep(1000);//FPS
                //create a thread to get mouse click
                new Thread(() -> {
                    // TODO Auto-generated method stub
                    //get mouse click event
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // TODO Auto-generated method stub
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
                            element=null;
                            System.out.println("element"+element);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //insert animal element into Element array,using for loop
    public void generateRandomGameArray() {
        for (int i = 0; i < ROWS_ELEMENT; i++) {
            for (int j = 0; j < COLS_ELEMENT; j++) {
                getRandomElement(i, j);
            }
        }
        //make sure there is no eliminate element when game start
        while (checkEliminate()) {
            generateRandomGameArray();
        }

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
        //check if two elements are adjacent
        //if two elements are adjacent,return true
        //if two elements are not adjacent,return false

        //check if two elements are in the same row
        if (element1.getRow() == element2.getRow()) {
            //check if two elements are adjacent
            if (element1.getCol() == element2.getCol() - 1 || element1.getCol() == element2.getCol() + 1) {
                return true;
            }
        }
        //check if two elements are in the same column
        if (element1.getCol() == element2.getCol()) {
            //check if two elements are adjacent
            if (element1.getRow() == element2.getRow() - 1 || element1.getRow() == element2.getRow() + 1) {
                return true;
            }
        }
        return false;
    }


    //swap two elements,then check is there any element can eliminate first
    //if there is no element can be eliminated,swap two elements back
    //if there is element can be eliminated,swap two elements and eliminate them,play bomb anime
    //do not need to check if two elements are adjacent,because it has been checked before
    public void swapElements(Element element1, Element element2) {
        //swap two elements
        Element temp = elements[element1.getRow()][element1.getCol()];
        elements[element1.getRow()][element1.getCol()] = elements[element2.getRow()][element2.getCol()];
        elements[element2.getRow()][element2.getCol()] = temp;
        //check if there are elements can be eliminated
        if (checkEliminate()) {
            //if there are elements can be eliminated,eliminate them
            eliminateElements();
            //after eliminate elements,check if there are elements can be eliminated again
            //if there are elements can be eliminated,eliminate them
            while (checkEliminate()) {
                System.out.println("Eliminate again");
                eliminateElements();
                //if no elements can be eliminated,stop while loop
            }
        } else {
            //if there are no elements can be eliminated,swap two elements back
            temp = elements[element1.getRow()][element1.getCol()];
            elements[element1.getRow()][element1.getCol()] = elements[element2.getRow()][element2.getCol()];
            elements[element2.getRow()][element2.getCol()] = temp;
        }
    }

    //read eliminate elements from stack,then set the image of element to null
    private void eliminateElements() {
        //read eliminate elements from stack
        while (!eliminateStack.isEmpty()) {
            Element element = eliminateStack.pop();
            //play boom anime
            playBoomAnime(element);
            //set the image of element to null
            elements[element.getRow()][element.getCol()].setImage(null);
        }
    }

    //check element can be eliminated or not
    //rule:3 or more same animal in a row or column can be eliminated
    //use stack to store the elements can be eliminated
    public boolean checkEliminate() {
        //check if there are elements can be eliminated
        boolean flag = false;
        //check by row
        for (int i = 0; i < ROWS; i++) {
            //use stack to store the elements can be eliminated
            //push the first element into stack
            eliminateStack.push(elements[0][i]);//start at (0,0),then check by row,such as (0,1),(0,2),(0,3)...(0,7)
            for (int j = 1; j < COLS; j++) {
                //if the element is the same as the top element in stack,push it into stack
                //same rule:check name of two elements
                //if any element is not the same as the top element in stack,check the size of stack
                //if the size of stack is less than 3,clear the stack
                //if the size of stack is more than 3,eliminate elements in stack
                if (elements[j][i].getName().equals(eliminateStack.peek().getName())) {
                    eliminateStack.push(elements[j][i]);
                } else {
                    if (eliminateStack.size() < 3) {
                        eliminateStack.clear();
                        eliminateStack.push(elements[i][j]);
                    } else {
                        flag = true;
                        break;
                    }
                }
            }
            //if the size of stack is more than 3,eliminate elements in stack
            if (eliminateStack.size() >= 3) {
                flag = true;
                break;
            } else {
                eliminateStack.clear();
            }
        }

        return flag;
    }

    //drop elements after eliminate
    //if there are empty elements,drop the elements above them
    //generate random elements to fill the empty elements
    public void dropElements() {
        //drop elements by column
        Stack<Element> dropStack = new Stack<>();
        for (int j = 0; j < COLS_ELEMENT; j++) {
            //use stack to store the elements in a column
            for (int i = 0; i < ROWS_ELEMENT; i++) {
                //if the element is not null,push it into stack
                if (elements[i][j].getImage() != null) {
                    dropStack.push(elements[i][j]);
                }
            }
            //drop elements
            for (int i = 0; i < ROWS_ELEMENT; i++) {
                //if the stack is not empty,drop the element
                if (!dropStack.isEmpty()) {
                    elements[i][j] = dropStack.pop();
                    elements[i][j].setRow(i);
                    elements[i][j].setCol(j);
                } else {
                    //if the stack is empty,generate random element to fill the empty element
                    getRandomElement(i, j);
                }
            }
            //clear the stack
            dropStack.clear();
        }
    }
    public void getRandomElement(int i, int j) {
        //generate random element
        int random = (int) (Math.random() * 4);
        //switch the random number to generate different animal
        switch (random) {
            case 0 -> elements[i][j] = new Element(i, j, Images.fox, "fox");
            case 1 -> elements[i][j] = new Element(i, j, Images.frog, "frog");
            case 2 -> elements[i][j] = new Element(i, j, Images.bear, "bear");
            case 3 -> elements[i][j] = new Element(i, j, Images.bird, "bird");
        }
    }
}