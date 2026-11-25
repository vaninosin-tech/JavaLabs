package lab6;
import java.util.*;

public final class Deck {
    public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
    public enum Rank { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
    public record Card(Suit suit, Rank rank) {
        public Card { Objects.requireNonNull(suit); Objects.requireNonNull(rank); }
    }
    private final List<Card> cards = new ArrayList<>();
    public Deck() { for (Suit suit : Suit.values()) for (Rank rank : Rank.values()) cards.add(new Card(suit, rank)); }
    public int size() { return cards.size(); }
    public List<Card> cards() { return List.copyOf(cards); }
    public void shuffle() { Collections.shuffle(cards); }
    /** Сначала добавляем во внешнюю структуру: при отказе структуры колода не меняется. */
    public Card dealTo(Collection<Card> hand) {
        Objects.requireNonNull(hand);
        if (cards.isEmpty()) throw new NoSuchElementException("Колода пуста");
        Card card = cards.get(cards.size() - 1);
        if (!hand.add(card)) throw new IllegalArgumentException("Внешняя структура не приняла карту");
        cards.remove(cards.size() - 1);
        return card;
    }
    public void returnFrom(Collection<Card> hand, Card card) {
        Objects.requireNonNull(hand); Objects.requireNonNull(card);
        if (cards.contains(card)) throw new IllegalArgumentException("Карта уже есть в колоде");
        if (!hand.remove(card)) throw new IllegalArgumentException("Карты нет в руке");
        cards.add(card);
    }
    public static void main(String[] args) {
        Deck deck = new Deck(); deck.shuffle();
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < 5; i++) deck.dealTo(hand);
        System.out.println("Рука: " + hand + "; в колоде: " + deck.size());
        Card card = hand.get(0); deck.returnFrom(hand, card);
        System.out.println("После возврата: " + deck.size());
        try { deck.returnFrom(hand, card); }
        catch (IllegalArgumentException ex) { System.out.println("Повторный возврат: " + ex.getMessage()); }
    }
}
