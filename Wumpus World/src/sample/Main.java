package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Main extends Application {

    Image blackTile,caveTile,glitterTile,goldTile,groundTile,guy,spiderTile,stinkTile,webTile,windTile,wumpusTile;

    WumpusTile world[][];
    boolean[][] visible;

    boolean placingTile = false, showWorld=false, userControl=true;
    int tileBeingPlaced = WumpusTile.GROUND,speed=100;

    Location guyLoc = new Location(9,0);

    @Override
    public void start(Stage primaryStage) throws Exception {

        visible = new boolean[10][10];
        world = new WumpusTile[10][10];
        visible[9][0] = true;
        initWorld();


        StackPane root = new StackPane();
        int width = 800;
        int height = 600;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        blackTile = new Image(new FileInputStream  ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World\\src\\pics\\blackTile.png"));
        caveTile = new Image(new FileInputStream   ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/caveTile.png"));
        glitterTile = new Image(new FileInputStream("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/glitterTile.png"));
        goldTile = new Image(new FileInputStream   ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/goldTile.png"));
        groundTile = new Image(new FileInputStream ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/groundTile.png"));
        guy = new Image(new FileInputStream        ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/guy.png"));
        spiderTile = new Image(new FileInputStream ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/spiderTile.png"));
        stinkTile = new Image(new FileInputStream  ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/stinkTile.png"));
        webTile = new Image(new FileInputStream    ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/webTile.png"));
        windTile = new Image(new FileInputStream   ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/windTile.png"));
        wumpusTile = new Image(new FileInputStream ("C:\\Users\\dongsoo\\IdeaProjects\\Wumpus World/src/pics/wumpusTile.png"));

        root.getChildren().add(canvas);
        Scene scene = new Scene(root, width, height);
        primaryStage.setTitle("Wumpus World");
        primaryStage.setScene(scene);

        getKeyboardInput(scene);
        getMouseInput(scene);

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(!userControl) act();
                drawWorld(gc);
                drawToolBar(gc);
                if(!userControl)try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();

        primaryStage.show();

    }

    private void getMouseInput(Scene scene) {
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                System.out.println("Mouse Clicked: ("+x+", "+y+")");  //debug
                Location worldClickLoc = whatThe(x,y);
                System.out.println("Mouse Clicked On: "+whatThe(x,y));
                checkToolBarClick(x,y);
                if(worldClickLoc != null) {
                    if(true){
                        int row=worldClickLoc.getRow(), col=worldClickLoc.getCol();
                        world[row][col].setType(tileBeingPlaced);
                        placingTile = false;
                        int tileSurround=-1;
                        switch (tileBeingPlaced){
                            case WumpusTile.CAVE: tileSurround=WumpusTile.WIND;break;
                            case WumpusTile.WUMPUS:tileSurround=WumpusTile.STINK;break;
                            case WumpusTile.SPIDER:tileSurround=WumpusTile.WEB;break;
                            case WumpusTile.GOLD:tileSurround=WumpusTile.GLITTER;break;
                        }
                        if(tileSurround!=-1){
                            if(row>0) if(world[row-1][col].getType()==WumpusTile.GROUND) world[row-1][col].setType(tileSurround);
                            if(row<9) if(world[row+1][col].getType()==WumpusTile.GROUND) world[row+1][col].setType(tileSurround);
                            if(col>0) if(world[row][col-1].getType()==WumpusTile.GROUND) world[row][col-1].setType(tileSurround);
                            if(col<9) if(world[row][col+1].getType()==WumpusTile.GROUND) world[row][col+1].setType(tileSurround);
                        }
                    }
                }

            }
        });
    }

    private void checkToolBarClick(double x, double y) {
        if (x>=721 && x<=769 && y>=101 && y<=147) {
            placingTile = true;
            tileBeingPlaced = WumpusTile.GROUND;
        } else if (x>=721 && x<=769 && y>=153 && y<=199) {
            placingTile = true;
            tileBeingPlaced = WumpusTile.CAVE;
        }else if (x>=721 && x<=769 && y>=205 && y<=251) {
            placingTile = true;
            tileBeingPlaced = WumpusTile.SPIDER;
        }else if (x>=721 && x<=769 && y>=257 && y<=303) {
            placingTile = true;
            tileBeingPlaced = WumpusTile.GOLD;
        }else if (x>=721 && x<=769 && y>=309 && y<=355) {
            placingTile = true;
            tileBeingPlaced = WumpusTile.WUMPUS;
        }
    }

    private Location whatThe(double x, double y) {
        int row = (int)(y-40)/50;
        int col = (int)(x-25)/50;

        if(row>=0 && row<world.length && col>=0 && col<world[row].length) {
            return new Location(row, col);
        } else {
            return null;
        }
    }

    private void getKeyboardInput(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String code = event.getCode().toString();
                System.out.println("Key Pressed: " + code);  //debug

                switch (code) {
                    case "UP":
                        move(1);
                        updateCurrentTile();
                        break;
                    case "DOWN":
                        move(3);
                        updateCurrentTile();
                        break;
                    case "LEFT":
                        move(4);
                        updateCurrentTile();
                        break;
                    case "RIGHT":
                        move(2);
                        updateCurrentTile();
                        break;
                    case "V":
                        showWorld = !showWorld;
                        break;
                    case "U":
                        userControl = !userControl;
                        break;
                    case "R":
                        reset();
                        break;
                    case "EQUALS":
                        speed*=0.7;
                        pl("Speed="+speed);
                        break;
                    case "MINUS":
                        speed/=0.7;
                        pl("Speed="+speed);
                        break;
                }
                visible[guyLoc.getRow()][guyLoc.getCol()] = true;
            }
        });
    }

    private void drawToolBar(GraphicsContext gc) {
        gc.drawImage(groundTile,720,100);
        gc.drawImage(caveTile,720,152);
        gc.drawImage(spiderTile,720,204);
        gc.drawImage(goldTile,720,256);
        gc.drawImage(wumpusTile,720,308);
    }

    private void drawWorld(GraphicsContext gc) {
        for(int i = 0; i<world.length;i++) {
            for(int j = 0; j<world[i].length; j++){
                if(visible[i][j]||showWorld){
                    if(world[i][j].getType() == WumpusTile.GROUND){
                        gc.drawImage(groundTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.CAVE){
                        gc.drawImage(caveTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.SPIDER){
                        gc.drawImage(spiderTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.GOLD){
                        gc.drawImage(goldTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.WUMPUS){
                        gc.drawImage(wumpusTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.WEB){
                        gc.drawImage(webTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.STINK){
                        gc.drawImage(stinkTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.WIND){
                        gc.drawImage(windTile,25+j*50, 40+i*50);
                    }
                    if(world[i][j].getType() == WumpusTile.GLITTER){
                        gc.drawImage(glitterTile,25+j*50, 40+i*50);
                    }
                }
                else
                    gc.drawImage(blackTile,25+j*50, 40+i*50);
            }
            gc.setFill(Color.WHITE);
            gc.fillRect(550,60,60,30);
            gc.setFill(Color.BLACK);
            gc.fillText("step: "+stepCount,550,80);
        }
        gc.drawImage(guy,25+guyLoc.getCol()*50,40+guyLoc.getRow()*50);
    }

    private void initWorld() {
        for(int i = 0; i<world.length;i++) {
            for(int j = 0; j<world[i].length; j++){
                    world[i][j] =  new WumpusTile(WumpusTile.GROUND);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    boolean initialized=false,isFoundGold=false,isFoundGlitter=false;
    MapNote[][] map = new MapNote[10][10];
    MapNote glitterMapNote;
    Stack<MapNote> route = new Stack<>();
    ArrayList<MapNote> possibleGold = new ArrayList<>();
    int stepCount=0;
    int lastDirection=1;
    /**
     * 1.up
     * 2.right
     * 3.down
     * 4.left
     */

    public void act(){
        if(isFoundGold) {goHome(); return;}
        if(!initialized) initialize();

        move(getNextMove());
        updateCurrentTile();

        pl("route: "+route);
    }

    private int getNextMove1(){//go through every one
        int row=guyLoc.getRow(),col=guyLoc.getCol();
        MapNote currentMapNote = map[row][col];

//        ArrayList<Integer> score=new ArrayList<>(4);
        for(MapNote temp1 : currentMapNote.neighbors){
            if (temp1.walked) temp1.score-=50;
            if (temp1.canBeMonster==1) temp1.score-=100;
            for(MapNote temp2 : temp1.neighbors){
//            for(MapNote temp2 : currentMapNote.neighbors){
                if(!temp2.walked){temp1.score++;}
            }
        }
        currentMapNote.neighbors.sort((o1,o2)-> o2.score-o1.score);
        for(MapNote temp1 : currentMapNote.neighbors){pl(temp1);pl("have"+temp1.score);}
        if(currentMapNote.neighbors.get(0).score<=0) {
            for(MapNote temp : currentMapNote.neighbors) temp.score=0;
            int direction = getDirection(currentMapNote,route.get(route.size()-2));//(lastDirection+1)%4+1;
            route.pop();route.pop();
            return direction;
        }
        else {
            for(MapNote temp : currentMapNote.neighbors) temp.score=0;
            return getDirection(currentMapNote,currentMapNote.neighbors.get(0));
        }
    }
    private int getNextMove2(){//go through every one
        int row=guyLoc.getRow(),col=guyLoc.getCol();
        MapNote currentMapNote = map[row][col];

        for(MapNote temp1 : currentMapNote.neighbors){
            if (temp1.walked) temp1.score-=50;
            if (!temp1.isWorthGoTo) temp1.score-=100;
            if (temp1.canBeMonster==1) temp1.score-=200;
            for(MapNote temp2 : temp1.neighbors){
//            for(MapNote temp2 : currentMapNote.neighbors){
                if(!temp2.walked){
                    temp1.score++;
                    if((temp2.row==0||temp2.row==9)&&(temp2.col==0||temp2.col==9)) //if in corner
                        temp1.score++;
                }
            }
        }
        currentMapNote.neighbors.sort((o1,o2)-> o2.score-o1.score);
        for(MapNote temp1 : currentMapNote.neighbors){p(temp1);pl("have"+temp1.score+",isWorth="+temp1.isWorthGoTo);}
        if(currentMapNote.neighbors.get(0).score<0) {
            int direction = getDirection(currentMapNote,route.get(route.size()-2));//(lastDirection+1)%4+1;
            route.pop();route.pop();
            for(MapNote temp : currentMapNote.neighbors) temp.score=0; return direction;
        }
        else {
            if (currentMapNote.neighbors.get(0).score==currentMapNote.neighbors.get(1).score){
                if(lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(0))||
                        lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(1))||
                        (currentMapNote.neighbors.get(2).score==currentMapNote.neighbors.get(1).score&&
                                lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(2)))){
                    for(MapNote temp : currentMapNote.neighbors) temp.score=0; return lastDirection;
                }
            }
            lastDirection = getDirection(currentMapNote,currentMapNote.neighbors.get(0));

            for(MapNote temp : currentMapNote.neighbors) temp.score=0; return lastDirection;
        }
    }
    private int getNextMove(){//go through every one
        int row=guyLoc.getRow(),col=guyLoc.getCol();
        MapNote currentMapNote = map[row][col];

        if(possibleGold.size()==1){ //go to gold
            if(currentMapNote==glitterMapNote)
                return getDirection(currentMapNote,possibleGold.get(0));
            return goBackTo(glitterMapNote);
        }

        for(MapNote temp1 : currentMapNote.neighbors){//get score
            if (temp1.walked) temp1.score-=4;
            if (!temp1.isWorthGoTo) temp1.score-=6;
            if (temp1.canBeMonster==1) temp1.score-=8;
            for(MapNote temp2 : temp1.neighbors){
                if(!temp2.walked){
                    temp1.score++;
                    if((temp2.row==0||temp2.row==9)&&(temp2.col==0||temp2.col==9)) //if in corner
                        temp1.score++;
                }
            }
        }
        currentMapNote.neighbors.sort((o1,o2)-> o2.score-o1.score);
        for(MapNote temp1 : currentMapNote.neighbors) {p(temp1);pl("have"+temp1.score+" points, isPossibleToHaveGoldInThisArea="+temp1.isWorthGoTo);}
        if(currentMapNote.neighbors.get(0).score<0) {

            for(MapNote temp : currentMapNote.neighbors) temp.score=0;

            for(int i=route.size()-2;i>=0;i--){
                MapNote temp=route.get(i);
                for (MapNote temp1:temp.neighbors){
                    if (temp1.isWorthGoTo&&!temp1.walked&&temp1.canBeMonster!=1)
                        return goBackTo(temp);
                }
            }
            return goBackTo(map[9][0]);
        }
        else {
            if (currentMapNote.neighbors.get(0).score==currentMapNote.neighbors.get(1).score){
                if(lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(0))||
                        lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(1))||
                        (currentMapNote.neighbors.get(2).score==currentMapNote.neighbors.get(1).score&&
                                lastDirection==getDirection(currentMapNote,currentMapNote.neighbors.get(2)))){
                    for(MapNote temp : currentMapNote.neighbors) temp.score=0; return lastDirection;
                }
            }
            for(MapNote temp : currentMapNote.neighbors) temp.score=0; return getDirection(currentMapNote,currentMapNote.neighbors.get(0));
        }
    }

    private void move(int direction){
        switch (direction){
            case 1:guyLoc.moveUp();lastDirection=1;break;
            case 2:guyLoc.moveRight();lastDirection=2;break;
            case 3:guyLoc.moveDown();lastDirection=3;break;
            case 4:guyLoc.moveLeft();lastDirection=4;break;
            default:
                lastDirection=-1;
                pl("Finished or Invalid Direction; Didn't Move");
                return;
        }
        stepCount++;
        pl("Moved to "+guyLoc);
        route.push(map[guyLoc.getRow()][guyLoc.getCol()]);
        visible[guyLoc.getRow()][guyLoc.getCol()]=true;
    }

    private void updateCurrentTile1(){

        int row=guyLoc.getRow(),col=guyLoc.getCol();
        map[row][col].walked=true;
        switch (world[row][col].getType()) {
            case WumpusTile.GROUND:
                map[row][col].groundUpdate1();
                break;
            case WumpusTile.WIND:map[row][col].clueUpdate();break;
            case WumpusTile.STINK:map[row][col].clueUpdate();break;
            case WumpusTile.WEB:map[row][col].clueUpdate();break;
            case WumpusTile.GOLD:isFoundGold=true;break;
            case WumpusTile.GLITTER:
//                map[row][col].clueUpdate();
                map[row][col].groundUpdate1();
                map[row][col].glitterUpdate();
                break;
        }
    }
    private void updateCurrentTile(){

        int row=guyLoc.getRow(),col=guyLoc.getCol();
        map[row][col].walked=true;
        switch (world[row][col].getType()) {
            case WumpusTile.GROUND:
                map[row][col].groundUpdate();
                break;
            case WumpusTile.WIND:map[row][col].clueUpdate();break;
            case WumpusTile.STINK:map[row][col].clueUpdate();break;
            case WumpusTile.WEB:map[row][col].clueUpdate();break;
            case WumpusTile.GOLD:isFoundGold=true;break;
            case WumpusTile.GLITTER:
//                map[row][col].clueUpdate();
//                map[row][col].groundUpdate();
                map[row][col].glitterUpdate();
                break;
        }
    }

    private int getDirection(MapNote from, MapNote to){
        if     (to.row== from.row&&to.col+1==from.col) return 4;
        else if(to.row== from.row&&to.col-1==from.col) return 2;
        else if(to.row+1==from.row&&to.col==from.col) return 1;
        else if(to.row-1==from.row&&to.col==from.col) return 3;
        else return -1;
    }

    private void initialize(){
        initialized=true;
        for(int i=0;i<10;i++)
            for(int j=0;j<10;j++){
                map[i][j]=new MapNote(i,j);
            }
        for(int i=0;i<10;i++)
            for(int j=0;j<10;j++){
                if(i>0) map[i][j].neighbors.add(map[i-1][j]);
                if(i<9) map[i][j].neighbors.add(map[i+1][j]);
                if(j>0) map[i][j].neighbors.add(map[i][j-1]);
                if(j<9) map[i][j].neighbors.add(map[i][j+1]);
            }

        route.push(map[guyLoc.getRow()][guyLoc.getCol()]);
        updateCurrentTile();
    }
    private void reset(){
        if (stepCount==0)
            initWorld();

        speed=100;
        guyLoc=new Location(9,0);
        visible=new boolean[10][10];

        initialized=false;isFoundGold=false;isFoundGlitter=false;
        map = new MapNote[10][10];
        glitterMapNote=null;
        route = new Stack<>();
        possibleGold = new ArrayList<>();
        stepCount=0;
        lastDirection=1;
    }

    private void goHome(){
        move(goBackTo(map[9][0]));

    }
    private void goHome1(){
        if(!(guyLoc.getRow()==9&&guyLoc.getCol()==0)){
            MapNote currentMapNote = map[guyLoc.getRow()][guyLoc.getCol()];
            for(MapNote temp:route)
                if(getDirection(currentMapNote,temp)!=-1)
                    {move(getDirection(currentMapNote,temp));return;}
        }

    }

    private int goBackTo(MapNote to){

        if(guyLoc.getRow()==9&&guyLoc.getCol()==0) return -1;

        int startAt=0;
        for (;startAt<route.size();startAt++){
            if(route.get(startAt).equals(to)) break;
        }

        MapNote currentMapNote = map[guyLoc.getRow()][guyLoc.getCol()];
        for(;startAt<route.size();startAt++){
            for(MapNote temp1:route.get(startAt).neighbors)
                if(getDirection(currentMapNote,temp1)!=-1&&temp1.canBeMonster==2&&getDirection(currentMapNote,temp1)==getDirection(temp1,route.get(startAt))) {
                    return getDirection(currentMapNote,temp1);}
            if(getDirection(currentMapNote,route.get(startAt))!=-1)
                return getDirection(currentMapNote,route.get(startAt));
        }

        return -1;
        
    }

    public static void p(Object o) {
        if(o instanceof Object[])
            p((Object[]) o);
        else
            System.out.print(o);
    }
    public static void pl() {System.out.println();}
    public static void pl(Object o) {p(o);pl();}

class MapNote{

    int row,col,score=0;
    ArrayList<MapNote> neighbors = new ArrayList<>();

    public MapNote(int row, int col) {
        this.row = row;
        this.col = col;
    }

    int canBeMonster=0,canBeGold=0;
    /**
     * 0.unknown
     * 1.true
     * 2.false
     */
    boolean walked=false,isWorthGoTo=true,checked=false;

    void groundUpdate1(){
        for( MapNote temp : neighbors){
            temp.canBeMonster=2;
        }
    }
    void groundUpdate(){
        for( MapNote temp : neighbors){
            temp.canBeMonster=2;
            temp.canBeGold=2;
            if(possibleGold.size()!=0)
                for(int i=0;i<possibleGold.size();i++)
                    if (possibleGold.get(i)==temp){
                        possibleGold.remove(i);
                        i--;
                    }
        }
        isWorthGoToUpdate();
    }

    void isWorthGoToUpdate(){
        for( MapNote temp : neighbors){
            isWorthGoToCheck(temp);
            for(int i=0;i<10;i++)
                for(int j=0;j<10;j++){
                    map[i][j].checked=false;
                }
        }
    }boolean isWorthGoToCheck(MapNote current){
        current.checked=true;
        if(current.canBeGold==2){
            for (MapNote temp:current.neighbors){
                if(!temp.checked&&!temp.walked&&temp.isWorthGoTo)
                    if(isWorthGoToCheck(temp))
                        return true;
            }
        }
        else return true;
        current.isWorthGoTo=false;
        return false;
    }

    void clueUpdate(){
        for( MapNote temp : neighbors){
            if(temp.canBeMonster==0) temp.canBeMonster=1;
        }
        isWorthGoToUpdate();
    }

    void glitterUpdate1(){
        for( MapNote temp : neighbors){
            if(temp.canBeMonster!=2) possibleGold.add(temp);
            temp.canBeMonster=2;
        }
        isWorthGoToUpdate();
    }
    void glitterUpdate2(){
        for( MapNote temp : neighbors){
            if(temp.canBeMonster!=2){
                temp.canBeGold=1;
                isFoundGlitter=true;
            }
            temp.canBeMonster=2;
        }

        isWorthGoToUpdate();
    }
    void glitterUpdate3(){ //quicker a little bit
        clueUpdate();
        if(possibleGold.size()==0)
            for( MapNote temp : neighbors){
                if(temp.canBeMonster!=2)
                    possibleGold.add(temp);
                temp.canBeMonster=2;
            }
        else
            for(int i=0;i<possibleGold.size();i++){
                boolean haveSame=false;
                for(MapNote temp : neighbors) {
                    if(temp.equals(possibleGold.get(i)))
                        haveSame = true;
                    temp.canBeMonster=2;
                }
                if(!haveSame) {
                    possibleGold.remove(i);
                    i--;
                }
            }
        glitterMapNote=map[row][col];
        pl("------------------------------------");
        pl(possibleGold);
        isWorthGoToUpdate();
    }
    void glitterUpdate(){ //safer
        clueUpdate();
        if(possibleGold.size()==0)
            for( MapNote temp : neighbors){
                if(temp.canBeMonster!=2)
                    possibleGold.add(temp);
            }
        else
            for(int i=0;i<possibleGold.size();i++){
                boolean haveSame=false;
                for(MapNote temp : neighbors) {
                    if(temp.equals(possibleGold.get(i)))
                        haveSame = true;
                    temp.canBeMonster=2;
                }
                if(!haveSame) {
                    possibleGold.remove(i);
                    i--;
                }
            }
        glitterMapNote=map[row][col];
        pl("-----------Locations of possibleGold: "+possibleGold);
        isWorthGoToUpdate();
    }

    public String toString(){
        return row+","+col;
    }
    }
}

