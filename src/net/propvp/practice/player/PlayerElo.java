package net.propvp.practice.player;

public class PlayerElo {

    public static final double kFactor = 32.0;

    private int rating;

    public PlayerElo() {
        this(0);
    }

    public PlayerElo(int rating) {
    	this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = (int) rating;
    }

    public double calculateExpectation(PlayerElo opponent) {
        return 1 / (1 + Math.pow(10, (opponent.getRating() - this.getRating()) / 400));
    }

    public double newRatingWin(PlayerElo opponent) {
        return this.rating + (kFactor * (1 - this.calculateExpectation(opponent)));
    }

    public double newRatingLoss(PlayerElo opponent) {
        return this.rating + (kFactor * (0 - this.calculateExpectation(opponent)));
    }

}