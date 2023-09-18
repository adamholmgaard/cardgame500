import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Player {
    private ArrayList<Card> hand;
    private int points;
    public Player() {
        this.hand = new ArrayList<>();
        this.points = 0;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void addToHand(Card card) {
        hand.add(card);
    }

    public void addToHand(Collection<Card> cards) {
        hand.addAll(cards);
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
    }

    public void removeFromHand(Collection<Card> cards) {
        hand.removeAll(cards);
    }

    public void sortHand() {
        Collections.sort(hand);
    }

    public void moveRightInHand(int index) {
        Collections.swap(hand, index, (index + 1) % hand.size());
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int amount) {
        points += amount;
    }
}
