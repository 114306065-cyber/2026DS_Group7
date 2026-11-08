package edu.nccu.moviefinder.model;

/** 一個關鍵字與它的權重。權重可為負數,例如訂票類雜訊。 */
public class Keyword {
    private final String name;
    private final double weight;

    public Keyword(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return name + "(" + weight + ")";
    }
}
