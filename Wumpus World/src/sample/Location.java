package sample;

public class Location {

    private int col;
    private int row;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void moveUp(){ if(row!=0)row--; };
    public void moveDown(){ if(row!=9)row++; };
    public void moveLeft(){ if(col!=0)col--; };
    public void moveRight(){ if(col!=9)col++; };

    @Override
    public String toString() {
        return "Location{" +
                "col=" + col +
                ", row=" + row +
                '}';
    }
}
