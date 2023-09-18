import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class Terminal {
    private int width;
    private final String sep;
    private final Game game;

    public Terminal(int width, Game game) {
        this.width = width;
        this.game = game;
        this.sep = "-".repeat(width);
    }

    public void setWidth(int newWidth) {
        width = newWidth;
    }

    public void printWinner() {
        System.out.println("Player " + (game.getTurn() % 2 == 1 ? 1 : 2) + " has won on turn " + game.getTurn() + "!");
        System.out.println("Player 1 points: " + game.getYourself().getPoints());
        System.out.println("Player 2 points: " + game.getOpponent().getPoints());
    }

    public void printGame() {
        String s = "";

        System.out.println(otherHandToString());

        for (ArrayList<Card> cards : game.getBoard()) {
            if (cardStringLength(s) + 1 + cardStringLength(cardsToString(cards)) + 2 >= width) {
                System.out.println(sep);
                System.out.println(s);
                s = cardsToString(cards);
            }
            else
                s += "-" + cardsToString(cards);
            if (game.getBoard().get(game.getBoard().size() - 1) == cards) {
                System.out.println(sep);
                System.out.println(withStringPad(s));
            }
        }

        System.out.println(sep);

        System.out.println(deckAndDiscardPileToString());
        System.out.println(ownHandToString());
    }

    private int cardStringLength(String s) {
        return s.replaceAll("\033\\[0;30m", "")
                .replaceAll("\033\\[0;31m", "")
                .replaceAll("\033\\[0m", "")
                .replaceAll("\uD83C\uDCDF", " ").length();
    }

    private String withStringPad(String s) {
        return "-".repeat((width - cardStringLength(s)) / 2)
                + s
                + "-".repeat((width + 1 - cardStringLength(s)) / 2);
    }

    private String cardsToString(Collection<Card> cards) {
        return cards
                .stream()
                .map(Card::toString)
                .collect(Collectors.joining("-", "[", "]"));
    }

    private String otherHandToString() {
        return withStringPad("[" + game.getOpponent().getHand().size() + "]");
    }

    private String ownHandToString() {
        return withStringPad(cardsToString(game.getYourself().getHand()));
    }

    private String deckAndDiscardPileToString() {
        return withStringPad("["
                + game.getDeck().size()
                + "]-["
                + (game.getDiscardPile().isEmpty() ? "" : game.getDiscardPile().peek().toString())
                + "]");
    }
}
