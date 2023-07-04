package cn.itwanho.eliminate;

public class AnimeDraw {
    //boom anime play method
    public static void playBoomAnime(Element element) {
        //if the element is eliminated,play boom anime
        if (element.getEliminated()) {
            //if the boom anime is playing,continue
            if (element.getBoomAnimeIndexForElement() < Images.bombs.length) {
                //get the boom anime image
                element.setImage(Images.bombs[element.getBoomAnimeIndexForElement()]);
                //boom anime index + 1
                element.setBoomAnimeIndexForElement(element.getBoomAnimeIndexForElement() + 1);
            }
        }
    }

    //move anime play method
    //move anime just move 1px every time
    //move have direction by receiving the direction parameter:1 up,2 down,3 left,4 right
    public static void playMoveAnime(Element element, int direction) {
        //if the element is selected,play move anime
        if (element.isSelected()) {
            //if the move anime is playing,continue
            if (element.getY() > 0 && element.getY() < 9 && element.getX() > 0 && element.getX() < 9) {
                //move up
                if (direction == 1) {
                    element.setImage(Images.bombs[0]);
                    element.setY(element.getY() - 1);
                }
                //move down
                if (direction == 2) {
                    element.setImage(Images.bombs[0]);
                    element.setY(element.getY() + 1);
                }
                //move left
                if (direction == 3) {
                    element.setImage(Images.bombs[0]);
                    element.setX(element.getX() - 1);
                }
                //move right
                if (direction == 4) {
                    element.setImage(Images.bombs[0]);
                    element.setX(element.getX() + 1);
                }
            }
        }
    }

    //play swap anime by calling playMoveAnime() method
    public static void playSwapAnime(Element element1, Element element2) {
        //if the two elements are selected,play swap anime
        if (element1.isSelected() && element2.isSelected()) {
            //if the swap anime is playing,continue
            if (element1.getY() > 0 && element1.getY() < 9 && element1.getX() > 0 && element1.getX() < 9) {
                //if the two elements are adjacent,play swap anime
                if (Math.abs(element1.getY() - element2.getY()) + Math.abs(element1.getX() - element2.getX()) == 1) {
                    //if the two elements are in the same row,play left or right swap anime
                    if (element1.getY() == element2.getY()) {
                        //if the element1 is on the left of element2,play left swap anime
                        if (element1.getX() < element2.getX()) {
                            playMoveAnime(element1, 4);
                            playMoveAnime(element2, 3);
                        }
                        //if the element1 is on the right of element2,play right swap anime
                        if (element1.getX() > element2.getX()) {
                            playMoveAnime(element1, 3);
                            playMoveAnime(element2, 4);
                        }
                    }
                    //if the two elements are in the same column,play up or down swap anime
                    if (element1.getX() == element2.getX()) {
                        //if the element1 is on the top of element2,play up swap anime
                        if (element1.getY() < element2.getY()) {
                            playMoveAnime(element1, 2);
                            playMoveAnime(element2, 1);
                        }
                        //if the element1 is on the bottom of element2,play down swap anime
                        if (element1.getY() > element2.getY()) {
                            playMoveAnime(element1, 1);
                            playMoveAnime(element2, 2);
                        }
                    }
                }
            }
        }
    }
}
