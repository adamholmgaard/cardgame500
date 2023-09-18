public class Card implements Comparable<Card> {
    private final String RESET = "\033[0m";
    private final String RED = "\033[0;31m";
    private final String BLACK = "\033[0;30m";
    private final Suit suit;
    private final int rank;
    public Card(Suit suit, int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getRank() {
        return rank;
    }

    public int toPoints() {
        if (suit == Suit.Joker)
            return 25;
        if (rank == 1)
            return 15;
        if (1 < rank & rank < 10)
            return 5;
        return 10;
    }

    @Override
    public String toString() {
        return switch (suit) {
            case Diamonds -> RED + "♦";
            case Hearts -> RED + "♥";
            case Clubs -> BLACK + "♣";
            case Spades -> BLACK + "♠";
            case Joker -> (rank == 1 ? RED : BLACK ) + "\uD83C\uDCDF";
        } + (suit == Suit.Joker ? "" : switch (rank) {
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            case 1 -> "A";
            default -> rank;
        }) + RESET;
    }

    @Override
    public int compareTo(Card o) {
        return this.suit == o.suit ? this.rank - o.rank : this.suit.ordinal() - o.suit.ordinal();
    }
}
