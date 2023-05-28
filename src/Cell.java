public class Cell {
    private boolean alive;

    public Cell() {
        this.alive = false; // По умолчанию клетка мертва
    }

    public Cell(boolean isAlive) {
        alive = isAlive;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}