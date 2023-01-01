package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.rest_responses.ListResponse;
import at.ac.uibk.swa.models.rest_responses.MessageResponse;
import at.ac.uibk.swa.models.rest_responses.RestResponse;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.UserDeckService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller handling {@link Deck} related Information (e.g. creating, updating, deleting {@link Deck}s)
 *
 * @author Fabian Magreiter
 */
@RestController
@SuppressWarnings("unused")
public class DeckController {

    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;

    @PostMapping("/api/create-deck")
    public RestResponse createDeck(
            @RequestBody final Deck deck
    ) {
        List<Card> cards = deck.getCards();
        System.out.println(deck);

        if (!userDeckService.create(deck)) {
            return new MessageResponse(false, "Deck not created");
        }

        return new MessageResponse(false, "Deck created");
    }

    @PostMapping("/api/update-deck")
    public RestResponse updateDeck(
            @RequestBody final Deck deck
    ) {
        if (userDeckService.update(deck.getDeckId(), deck.getName(), deck.getDescription())) {
            return new MessageResponse(true, "Deck updated");
        }
        return new MessageResponse(false, "Deck not updated");
    }

    @PutMapping("/api/set-publicity")
    public RestResponse setPublicity(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.publish(deckId)) {
            return new MessageResponse(true, "Deck publicity changed");
        }
        return new MessageResponse(false, "Deck publicity not changed");
    }

    @DeleteMapping("/api/delete-deck")
    public RestResponse deleteDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.delete(deckId)) {
            return new MessageResponse(true, "Deck deleted");
        }
        return new MessageResponse(false, "Deck not deleted");
    }

    @GetMapping("/api/get-user-decks")
    public RestResponse getUserDecks(
            @RequestParam(name = "personId") final UUID personId
    ) throws JsonProcessingException {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllOwnedDecks();
        if (maybeDecks.isPresent()) {
            List<Deck> decks = maybeDecks.get();
            return new ListResponse<>(decks);
        }
        //TODO warum hier personId zurückgeben?
        return new MessageResponse(false, "getUserDecks" + personId + " failed");
    }

    @GetMapping("/api/get-published-decks")
    public RestResponse getPublishedDecks() {
        return new ListResponse<>(userDeckService.findAllAvailableDecks());
    }

    @AnyPermission(Permission.ADMIN)
    @GetMapping("/api/get-all-decks")
    public RestResponse getAllDecks() {
        return new ListResponse<>(adminDeckService.findAll());
    }

    /**
     * Gets the Cards from the given Deck which should be learned.
     * A Card should be learned if it's nextLearn-Date is before LocalDateTime.NOW
     *
     * @return A List of Cards that should be learned sorted by nextLearn-Date.
     */
    @GetMapping("/api/get-all-cards-to-learn")
    public RestResponse getAllCardsToLearn(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        Optional<List<Card>> maybeCards = cardService.getAllCardsToLearn(deckId);
        if (maybeCards.isPresent()) {
            List<Card> cards = maybeCards.get();
            return new ListResponse<>(cards);
        }
        return new MessageResponse(false, "getAllCardsToLearn failed");

    }
}

