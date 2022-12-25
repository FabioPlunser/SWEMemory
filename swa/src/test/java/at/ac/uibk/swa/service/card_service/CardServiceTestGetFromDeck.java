package at.ac.uibk.swa.service.card_service;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.UserDeckService;
import at.ac.uibk.swa.util.StringGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CardServiceTestGetFromDeck {
    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private PersonService personService;

    private Person createUser(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.USER));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Person createAdmin(String username) {
        Person person = new Person(username, StringGenerator.email(), StringGenerator.password(), Set.of(Permission.ADMIN));
        assertTrue(personService.create(person), "Unable to create user");
        return person;
    }

    private Deck createDeck(String name, Person creator) {
        Deck deck = new Deck(name, StringGenerator.deckDescription(), creator);
        assertTrue(userDeckService.create(deck), "Unable to create deck");
        return deck;
    }

    @Test
    public void testGetCardsFromDeckOwned() {
        // given: a user, that created a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwned");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwned", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: all cards must be loaded again
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void testGetCardsFromDeckOwnedBlocked() {
        // given: a user, that created a deck with multiple cards, where the deck got blocked after creation
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwnedBlocked");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedBlocked", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(adminDeckService.block(deck), "Unable to block deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromDeckOwnedDeleted() {
        // given: a user, that created a deck with multiple cards, where the deck got deleted after creation
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckOwnedDeleted");
        Deck deck = createDeck("deck-testGetCardsFromDeckOwnedDeleted", person);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.delete(deck), "Unable to delete deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }

    @Test
    public void testGetCardsFromDeckSubscribedPublished() {
        // given: a user, that subscribed to a deck with multiple cards
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedPublished");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedPublished", createUser("person-testGetCardsFromDeckSubscribedPublished-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: all cards must be loaded again
        assertEquals(cards.size(), loadedCards.size(), "Got wrong number of cards");
        for (Card card : cards) {
            assertTrue(loadedCards.contains(card), "Unable to find a card");
        }
    }

    @Test
    public void testGetCardsFromDeckSubscribedUnpublished() {
        // given: a user, that subscribed to a deck with multiple cards, but the deck was unpublished after subscription
        int numCardsPerDeck = 10;
        Person person = createUser("person-testGetCardsFromDeckSubscribedUnpublished");
        Deck deck = createDeck("deck-testGetCardsFromDeckSubscribedUnpublished", createUser("person-testGetCardsFromDeckSubscribedUnpublished-creator"));
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < numCardsPerDeck; i++) {
            Card card = new Card(StringGenerator.cardText(), StringGenerator.cardText(),false, deck);
            assertTrue(cardService.create(card), "Unable to create card");
            cards.add(card);
        }
        assertTrue(userDeckService.publish(deck), "Unable to publish deck");
        assertTrue(userDeckService.subscribe(deck, person), "Unable to subscribe to deck");
        assertTrue(userDeckService.unpublish(deck), "Unable to unpublish deck");

        // when: retrieving all cards for that user and deck
        List<Card> loadedCards = cardService.getAllCards(deck, person);

        // then: no cards must be loaded
        assertEquals(0, loadedCards.size(), "Got wrong number of cards");
    }
}