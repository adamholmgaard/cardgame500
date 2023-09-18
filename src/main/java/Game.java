import java.util.*;
import java.util.stream.IntStream;
/*
TODO
- man må ikke lægge ned på første runde!
- læg ned er buggy
- lav så man kan tage jokeren (må man tage den op på hånden?)
- Hvad sker der hvis der ikke er flere kort at trække ind? (slut spil automatisk evt.)
- Man kan bruge jokeren fuldkommen
  - Den kan lægges i et run
    - Den kan ikke lægges i et run hvor den erstatter et kort som er på bordet i forvejen
  - hvis det er en joker man vil lægge ned, hvor skal den så hen på boardet?
  - Den kan lægges til, kun hvis den ikke findes på boardet i forvejen
- Player 2 spiller mod en, hvor algoritmen forbedres løbende
- Manlger test suite
- Endeligt: man kan spille via terminalen, evt som .exe fil eller lignende
- evt: smid exceptions i stedet for at printe, som er mere beskrivende når man prøve at gøre noget ulovligt
 */
public class Game {
    private Terminal terminal;
    private Player player1, player2;
    private int turn;
    private ArrayList<ArrayList<Card>> board;
    private Stack<Card> deck, discardPile;

    public Game() {
        this.player1 = new Player();
        this.player2 = new Player();
        this.turn = 0;
        this.board = new ArrayList<ArrayList<Card>>();
        this.discardPile = new Stack<>();
        this.deck = new Stack<>();
        for (int i = 1; i<=13; i++) {
            deck.push(new Card(Suit.Diamonds, i));
            deck.push(new Card(Suit.Clubs, i));
            deck.push(new Card(Suit.Spades, i));
            deck.push(new Card(Suit.Hearts, i));
        }
        deck.push(new Card(Suit.Joker, 1));
        deck.push(new Card(Suit.Joker, 2));
        Collections.shuffle(deck);
        this.terminal = new Terminal(55, this);
    }

    public void startGame() {
        for (int i = 0; i < 7; i++) {
            drawCard(player1);
            drawCard(player2);
        }
        player1.sortHand();

        discardPile.add(deck.pop());
        while (discardPile.peek().getSuit() == Suit.Joker) {
            discardPile.add(deck.pop());
        }

        while (!player2.getHand().isEmpty()) {
            turn++;
            if (turn % 2 == 1)
                player1Turn();
            else
                player2Turn();
        }

        if (turn % 2 == 1)
            player2.addPoints(-cardsToPoints(player2.getHand()));
        else
            player1.addPoints(-cardsToPoints(player2.getHand()));

        terminal.printWinner();
    }

    private int cardsToPoints(Collection<Card> cards) {
        return cards.stream().mapToInt(Card::toPoints).sum();
    }

    public Player getYourself() {
        return player1;
    }

    public Player getOpponent() {
        return player2;
    }

    public ArrayList<ArrayList<Card>> getBoard() {
        return board;
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getDiscardPile() {
        return discardPile;
    }

    public int getTurn() {
        return turn;
    }

    private void drawCard(Player player) {
        player.addToHand(deck.pop());
    }

    private void drawFromDiscardPile(Player player) {
        player.addToHand(discardPile.pop());
    }

    private void drawDiscardPile(Player player) {
        player.addToHand(discardPile);
        discardPile = new Stack<Card>();
    }

    private void discard(Player player, Card card) {
        player.removeFromHand(card);
        discardPile.push(card);
    }

    private void playHandRun(Player player, Collection<Card> cards) {
        board.add((ArrayList<Card>) cards);
        player.removeFromHand(cards);
    }

    private void player2Turn() {
        drawCard(player2);
        discard(player2, player2.getHand().get(player2.getHand().size()-1));
    }

    private boolean player1aux(List<Card> cards, int k, int i) {
        if (cards.size() < k + 1) {
            if (cards.get(k).getRank() == ((cards.get(i).getRank() + k - i - 1) % 13) + 1) {
                return true;
            }
            else if (cards.get(k).getSuit() == Suit.Joker) {
                return player1aux(cards, k + 1, i);
            }
            else
                return false;
        }
        else
            return true;
    }

    private void player1Turn() {
        boolean drewDiscard = false;
        boolean loopBool = true;
        Scanner scanner = new Scanner(System.in);
        terminal.printGame();

        // Start med at tage ind
        while (loopBool)
            try {
                System.out.println("Your turn! Draw from deck, from discard pile (" + discardPile.peek().toString() + ") or take entire discard pile? (1/2/3): ");
                int in = scanner.nextInt();
                switch (in) {
                    case 1 -> {
                        if (discardPile.isEmpty())
                            System.out.println("Discard pile is empty!");
                        else {
                            drawCard(player1);
                            player1.sortHand();
                            terminal.printGame();
                            loopBool = false;
                        }
                    }
                    case 2 -> {
                        drawFromDiscardPile(player1);
                        player1.sortHand();
                        terminal.printGame();
                        loopBool = false;
                    }
                    case 3 -> {
                        if (turn == 1)
                            System.out.println("You can't take the discard pile on your first turn!");
                        else {
                            drewDiscard = true;
                            drawDiscardPile(player1);
                            loopBool = false;
                        }

                    }
                    default -> {
                        System.out.println("Invalid input, try again.");
                        terminal.printGame();
                    }
                }
            }
            catch(Exception e) {
                System.out.println("Invalid input, try again.");
                terminal.printGame();
            }

        //spørg om man vil bytte kort rundt (så man kan bruge joker og q-k-a)
        loopBool = true;
        while (loopBool) {
            try {
                System.out.println("Do you want to move any cards in your hand? (0 for no, 1-" + player1.getHand().size() + " for the card you want to move to the right): ");
                int in = scanner.nextInt();
                if (in == 0) {
                    terminal.printGame();
                    loopBool = false;
                }
                else if (in > 0 & in <= player1.getHand().size()) {
                    player1.moveRightInHand(in - 1);
                    terminal.printGame();
                }
                else {
                    System.out.println("Invalid input, try again.");
                    terminal.printGame();
                }
            } catch(Exception e) {
                    System.out.println("Invalid input, try again.");
                    terminal.printGame();
            }
        }

        // Spørg om man vil lægge ned
        loopBool = true;
        while (loopBool) {
            try {
                System.out.println("Do you want to play any card runs? (0 for no, i j for the cards you want to play from your hand): ");
                int in1 = scanner.nextInt();
                switch (in1) {
                    case 0 -> {
                        if (drewDiscard) {
                            System.out.println("You have to play a run after taking the discard pile! Go back or accept 25 point penalty? (1/2)");
                            int in = scanner.nextInt();
                            switch (in) {
                                case 1 -> {}
                                case 2 -> {
                                    player1.addPoints(-25);
                                    drewDiscard = false;
                                    loopBool = false;
                                }
                                default -> System.out.println("Invalid input, try again.");
                            }
                        }
                        else {
                            terminal.printGame();
                            loopBool = false;
                        }
                    }
                    default -> {
                        int in2 = scanner.nextInt();
                        if (0 < in1 & in1 <= player1.getHand().size() & 0 < in2 & in2 <= player1.getHand().size() & in1 + 1 < in2) {
                            List<Card> cards = player1.getHand().subList(in1 - 1, in2);
                            if (IntStream.range(0, in2 - 1).allMatch(i -> {
                                if (cards.get(i).getSuit() == Suit.Joker)
                                    return true;
                                else {
                                    if (cards.get(i+1).getSuit() == Suit.Joker) {
                                        return player1aux(cards, i+2, i);
                                    }
                                    else {
                                        return cards.get(i).getRank() == cards.get(i+1).getRank();
                                    }
                                }
                            })) { // Good, læg ned
                                drewDiscard = false;
                                playHandRun(player1, cards);
                                player1.addPoints(cardsToPoints(cards));
                                terminal.printGame();
                            }
                        }
                        else {
                            System.out.println("Invalid input, try again.");
                            terminal.printGame();
                        }
                    }
                }
            } catch(Exception e) {
                System.out.println("Invalid input, try again.");
                terminal.printGame();
            }
        }

        //spørg om man vil lægge til
        loopBool = true;
        while (loopBool) {
            try {
                System.out.print("Do you want to lay off any cards? (0 for no, 1-" + player1.getHand().size() + " for the card you want to lay off): ");
                int in = scanner.nextInt();
                if (in == 0)
                    loopBool = false;
                else if (in > 0 & in < player1.getHand().size()+1) {
                    Card card = player1.getHand().get(in - 1);
                    Optional<ArrayList<Card>> res = board.stream().filter(list -> ((list.get(list.size() -1).getRank() - 2) % 13) + 1 == card.getRank() & list.get(list.size() - 1).getSuit() == card.getSuit()).findFirst();
                    if (card.getSuit() == Suit.Joker)
                      System.out.println("Laying off joker feature not implemented"); //if the card you want to lay off is a joker
                    else
                        if (res.isPresent()) {
                            res.get().add(0, card);
                            player1.removeFromHand(player1.getHand().get(in-1));
                            player1.addPoints(card.toPoints());
                            terminal.printGame();
                        }
                        else {
                            System.out.println("No run was found to match your chosen card!");
                            terminal.printGame();
                        }
                }
                else {
                    System.out.println("Invalid input, try again.");
                    terminal.printGame();
                }
            } catch(Exception e) {
                    System.out.println("Invalid input, try again.");
                    terminal.printGame();
            }
        }

        terminal.printGame();

        //læg kort tilbage
        loopBool = true;
        if (!player1.getHand().isEmpty()) {
            while (loopBool) {
                try {
                    System.out.println("What card do you want to put out? (1-" + player1.getHand().size() + "):");
                    int in = scanner.nextInt();
                    if (in < 1 | in > player1.getHand().size()) {
                        System.out.println("Invalid input, try again.");
                        terminal.printGame();
                    }
                    else {
                        discard(player1, player1.getHand().get(in - 1));
                        loopBool = false;
                    }
                } catch(Exception e) {
                        System.out.println("Invalid input, try again.");
                        terminal.printGame();
                }
            }
        }
    }
}
