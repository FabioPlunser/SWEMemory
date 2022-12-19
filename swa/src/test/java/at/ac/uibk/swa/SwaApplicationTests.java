package at.ac.uibk.swa;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Person;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.PersonService;
import at.ac.uibk.swa.service.DeckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SwaApplicationTests {

    @Autowired
    private PersonService personService;
    @Autowired
    private DeckService deckService;
    @Autowired
    private CardService cardService;

    @Test
    public void DummyTest() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TestRetrievingUserFromToken() {
        Person person = new Person("tokenTest", "def", "ghi", new ArrayList<>());

        assertEquals(true, personService.save(person));

        Optional<Person> maybePerson = personService.login(person.getUsername(), person.getPassword());
        assertTrue(maybePerson.isPresent());
        UUID token = maybePerson.get().getToken();

        Optional<Person> oCustomer = personService.findByToken(token);
        assertTrue(oCustomer.isPresent());
        assertEquals(person.getPersonId(), oCustomer.get().getPersonId());
    }

    @Test
    public void TestCreatingDeck() {
        Person person = new Person("deckTest", "def", "ghi", new ArrayList<>());

        Deck deck = new Deck("Deck1", "abcd", person);
        Card card = new Card("Front", "Back", false, deck);

        assertEquals(true, personService.save(person));
        assertEquals(true, deckService.save(deck));
        assertEquals(true, cardService.save(card));

        person = personService.findByToken(personService.login(person.getUsername(), person.getPassword()).get().getToken()).get();

        assertEquals(deck.getDeckId(), person.getCreatedDecks().get(0).getDeckId());
        assertEquals(1, person.getCreatedDecks().size());

        // assertEquals(card.getCardId(), person.getDecks().get(0).getCards().get(0).getCardId());
        assertEquals(1, person.getCreatedDecks().get(0).getCards().size());
    }
}
