package at.ac.uibk.swa.controllers;

import at.ac.uibk.swa.models.Card;
import at.ac.uibk.swa.models.Deck;
import at.ac.uibk.swa.models.Permission;
import at.ac.uibk.swa.models.annotations.AnyPermission;
import at.ac.uibk.swa.models.annotations.ApiRestController;
import at.ac.uibk.swa.models.rest_responses.*;
import at.ac.uibk.swa.service.AdminDeckService;
import at.ac.uibk.swa.service.CardService;
import at.ac.uibk.swa.service.MailService;
import at.ac.uibk.swa.service.UserDeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller handling {@link Deck} related Information (e.g. creating, updating, deleting {@link Deck}s)
 *
 * @author Fabian Magreiter
 */
@ApiRestController
@SuppressWarnings("unused")
public class DeckController {

    @Autowired
    private UserDeckService userDeckService;
    @Autowired
    private AdminDeckService adminDeckService;
    @Autowired
    private CardService cardService;
    @Autowired
    private MailService mailService;


    private static final String DECK_LOAD_ERROR_MESSAGE = "Unable to get decks.";

    /**
     * Generates a string to describe the action done on a deck
     * Example:
     * - name of deck:     "example deck"
     * - actionDone:       "was created"
     * - returned string:  "Deck example deck was created."
     *
     * @param deckName   name of the deck on which the action was performed
     * @param actionDone description of the action that was performed on the deck
     * @return a message describing what was done on which deck
     */
    private String generateMessage(String deckName, String actionDone) {
        StringBuilder sb = new StringBuilder();
        sb.append("Deck ");
        sb.append(deckName);
        sb.append(" ");
        sb.append(actionDone);
        sb.append(".");
        return sb.toString();
    }

    /**
     * Creates a new Deck with all the given Cards.
     *
     * @param deck
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/create-deck")
    public RestResponse createDeck(
            @RequestBody final Deck deck
    ) {
        if (!userDeckService.create(deck)) {
            return MessageResponse.builder().error()
                    .message(generateMessage(deck.getName(), "could not be created")).build();
        }

        return MessageResponse.builder().ok()
                .message(generateMessage(deck.getName(), "created successfully")).build();
    }

    /**
     * Updates the given Deck and - if specified - cards of that Deck.
     * The Deck must be owned by the current User.
     *
     * @param deck        The Deck to update.
     * @param updateCards if true, updates the cards of the deck (default value is false)
     * @return A MessageResponse indicating success or failure.
     */
    @PostMapping("/update-deck")
    public RestResponse updateDeck(
            @RequestBody final Deck deck,
            @RequestParam(name = "update-cards", defaultValue = "false") final boolean updateCards
    ) {
        if (userDeckService.update(deck, updateCards)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(deck.getName(), "updated"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(deck.getName(), "not updated"))
                .build();
    }

    /**
     * Sets given deck to public.
     *
     * @param deckId
     * @return
     */
    @PostMapping("/publish-deck")
    public RestResponse publish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.publish(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "published"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "publicity not changed"))
                .build();
    }

    /**
     * Sets given deck to private.
     *
     * @param deckId
     * @return
     */
    @PostMapping("/unpublish-deck")
    public RestResponse unpublish(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unpublish(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "unpublished"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), " publicity not changed"))
                .build();
    }


    /**
     * Subscribes the current User to the given Deck.
     *
     * @param deckId
     * @return
     */
    @PostMapping("/subscribe-deck")
    public RestResponse subscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.subscribe(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "subscribed"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "not subscribed"))
                .build();
    }

    /**
     * Unsubscribes the current User from the given Deck.
     *
     * @param deckId
     * @return
     */
    @PostMapping("/unsubscribe-deck")
    public RestResponse unsubscribeDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.unsubscribe(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "unsubscribed"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "not unsubscribed"))
                .build();
    }

    /**
     * Blocks the given Deck.
     * Only Admins can block Decks.
     *
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PostMapping("/block-deck")
    public RestResponse blockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.block(deckId)) {
            Optional<Deck> maybeDeck = userDeckService.findById(deckId);
            if (maybeDeck.isPresent()) {
                Deck deck = maybeDeck.get();
                mailService.notifyBlockedDeck(deck);
                return MessageResponse.builder()
                        .ok()
                        .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "blocked"))
                        .build();
            }
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), " not blocked"))
                .build();
    }

    /**
     * Unblocks the given Deck.
     * Only Admins can unblock Decks.
     *
     * @param deckId
     * @return
     */
    @AnyPermission(Permission.ADMIN)
    @PostMapping("/unblock-deck")
    public RestResponse unblockDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (adminDeckService.unblock(deckId)) {
            Optional<Deck> maybeDeck = userDeckService.findById(deckId);
            if (maybeDeck.isPresent()) {
                Deck deck = maybeDeck.get();
                mailService.notifyUnblockedDeck(deck);
                return MessageResponse.builder()
                        .ok()
                        .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "unblocked"))
                        .build();
            }
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "not unblocked"))
                .build();
    }

    /**
     * Deletes the given Deck.
     *
     * @param deckId
     * @return
     */
    @DeleteMapping("/delete-deck")
    public RestResponse deleteDeck(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        if (userDeckService.delete(deckId)) {
            return MessageResponse.builder()
                    .ok()
                    .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), "deleted"))
                    .build();
        }
        return MessageResponse.builder()
                .error()
                .message(generateMessage(userDeckService.getDeckNameIfPresent(deckId), " not deleted"))
                .build();
    }

    /**
     * Gets all Decks that the current User is subscribed to or created.
     *
     * @return A List of Decks.
     */
    @GetMapping("/get-user-decks")
    public RestResponse getUserDecks() {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllViewableDecks();
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message(DECK_LOAD_ERROR_MESSAGE)
                .build();
    }

    /**
     * Gets all Decks that given user has subscribed to or created.
     *
     * @return A List of Decks.
     */
    @AnyPermission(Permission.ADMIN)
    @GetMapping("/get-given-user-decks")
    public RestResponse getGivenUserDecks(
            @RequestParam(name = "personId") final UUID personId
    ) {
        Optional<List<Deck>> maybeDecks = userDeckService.getDecksOfGivenPerson(personId);
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message(DECK_LOAD_ERROR_MESSAGE)
                .build();
    }

    /**
     * Gets all Decks that the current User is subscribed to.
     *
     * @return A List of Decks.
     */
    @GetMapping("/get-subscribed-decks")
    public RestResponse getSubscribedDecks() {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllSubscribedDecks();
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message(DECK_LOAD_ERROR_MESSAGE)
                .build();
    }

    /**
     * Gets all Decks that the current User created.
     *
     * @return A List of Decks.
     */
    @GetMapping("/get-created-decks")
    public RestResponse getCreatedDecks() {
        Optional<List<Deck>> maybeDecks = userDeckService.getAllOwnedDecks();
        if (maybeDecks.isPresent()) {
            return new UserDeckListResponse(maybeDecks.get());
        }
        return MessageResponse.builder()
                .error()
                .message(DECK_LOAD_ERROR_MESSAGE)
                .build();
    }


    /**
     * Get all Decks that are available for subscription
     * Does not include decks to which the requesting user has already subscribed
     *
     * @return A List of Decks.
     */
    @GetMapping("/get-published-decks")
    public RestResponse getPublishedDecks() {
        return new DeckListResponse(userDeckService.findAllAvailableDecks());
    }

    /**
     * Gets all Decks.
     *
     * @return A List of Decks.
     */
    @AnyPermission(Permission.ADMIN)
    @GetMapping("/get-all-decks")
    public RestResponse getAllDecks() {
        return new DeckListResponse(adminDeckService.findAll());
    }

    /**
     * Gets the Cards from the given Deck which should be learned.
     * A Card should be learned if it's nextLearn-Date is before {@link LocalDateTime#now()}
     *
     * @return A List of Cards that should be learned sorted by nextLearn-Date.
     */
    @GetMapping("/get-all-cards-to-learn")
    public RestResponse getAllCardsToLearn(
            @RequestParam(name = "deckId") final UUID deckId
    ) {
        Optional<List<Card>> maybeCards = cardService.getAllCardsToLearn(deckId);
        if (maybeCards.isPresent()) {
            List<Card> cards = maybeCards.get();
            return new ListResponse<>(cards);
        }
        return MessageResponse.builder()
                .error()
                .message("Could not get cards")
                .build();

    }
}

