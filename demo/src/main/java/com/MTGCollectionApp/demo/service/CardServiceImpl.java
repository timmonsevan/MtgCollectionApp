package com.MTGCollectionApp.demo.service;
import com.MTGCollectionApp.demo.dao.CardDAO;
import com.MTGCollectionApp.demo.entity.DatabaseCard;
import com.MTGCollectionApp.demo.exceptions.CardNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.magicthegathering.javasdk.resource.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;


/**
 * service methods for controllers
 *
 * @author timmonsevan
 */
@Service
public class CardServiceImpl implements CardService {

    private final CardDAO cardDAO;
    private final RestTemplate restTemplate;

    @Autowired
    public CardServiceImpl(CardDAO theCardDAO, RestTemplate restTemplate) {
        this.cardDAO = theCardDAO;
        this.restTemplate = restTemplate;
    }

    /**
     * @return a card in collection found by its Id number in stored database
     */
    @Override
    public DatabaseCard findById(long id) {
        return cardDAO.findById(id);
    }

    /**
     * @return a String List of all cards in collection
     */
    @Override
    public String viewCollection() {

        List<DatabaseCard> query = new ArrayList<>(cardDAO.findAll());
        StringBuilder collectionList = new StringBuilder();

        if (query.isEmpty()) {
            return "Collection is empty.";
        }

        for (DatabaseCard card : query) {
            collectionList.append("You have ")
                    .append(card.getQuantity())
                    .append(card.getQuantity() == 1 ? " copy" : " copies")
                    .append(" of ")
                    .append(card.getName())
                    .append(" in your collection\n");
        }
        return collectionList.toString();
    }

    /**
     * @return List of all cards in stored database collection
     */
    @Override
    public List<DatabaseCard> listCollection() {
        return new ArrayList<>(cardDAO.findAll());
    }

    /**
     * searches collection for card by name
     *
     * @return a message screen
     */
    @Override
    public String searchCollectionByName(String cardName) throws ClassNotFoundException {

        if (cardName != null) {

            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered.");
            }

            List<DatabaseCard> query;

            try {
                query = new ArrayList<>(cardDAO.findByName(cardName));
            } catch (NullPointerException ex) {
                return "Card '" + cardName + "' not found in your collection.";
            }

            query = new ArrayList<>(cardDAO.findByName(cardName));
            StringBuilder collectionList = new StringBuilder();

            for (DatabaseCard card : query) {
                collectionList.append("You have ")
                        .append(card.getQuantity())
                        .append(card.getQuantity() == 1 ? " copy" : " copies")
                        .append(" of ")
                        .append(card.getName())
                        .append(" in your collection\n");
            }
            return collectionList.toString();
        } else {
            return null;
        }
    }

    /**
     * adds a new card to the collection with a specified quantity
     *
     * @return a message screen
     */
    @Override
    @Transactional
    public String addNewCard(String cardName, String numCards) throws ClassNotFoundException {

        int quantity;
        String url = "https://api.magicthegathering.io/v1/cards?name=" + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        if (cardName != null && numCards != null) {

            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered");
            }

            try {
                quantity = Integer.parseInt(numCards);
            } catch (NumberFormatException ex) {
                return "Invalid entry, please check that numCards is whole number value > 0";
            }

            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid entry, please check that numCards is whole number value > 0");
            }

            try {
                List<DatabaseCard> query = new ArrayList<>(cardDAO.findByName(cardName));

                if (!query.isEmpty()) {
                    for (DatabaseCard card : query) {
                        if (card.getName().equalsIgnoreCase(cardName)) {
                            return cardName + " already exists in your collection";
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                List<Card> cards = new ArrayList<>();

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
                    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");

                    Type cardListType = new TypeToken<List<Card>>() {
                    }.getType();
                    cards = new Gson().fromJson(cardsArray, cardListType);
                }
                for (Card card : cards) {
                    if (card.getName().equalsIgnoreCase(cardName) && card.getMultiverseid() > 0) {
                        DatabaseCard tempDatabaseCard = new DatabaseCard(card, quantity);
                        cardDAO.save(tempDatabaseCard);
                        break;
                    }
                }
            } catch (Exception ex) {
                throw new CardNotFoundException("Card with name " + cardName + " cannot be found.");
            }
            return cardName + " was added to your collection.";
        } else {
            return null;
        }
    }

    /**
     * adds a new card to the collection with a specified quantity and set indicator
     *
     * @return a message screen
     */
    @Override
    @Transactional
    public String addNewCard(String cardName, String numCards, String set) throws ClassNotFoundException {

        int quantity;
        String url = "https://api.magicthegathering.io/v1/cards?name=" + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        if (cardName != null && numCards != null && set != null) {


            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered");
            }

            try {
                quantity = Integer.parseInt(numCards);
            } catch (NumberFormatException ex) {
                return "Invalid entry, please check that numCards is whole number value > 0";
            }

            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid entry, please check that numCards is whole number value > 0");
            }

            try {
                List<DatabaseCard> query = new ArrayList<>(cardDAO.findByName(cardName));

                if (!query.isEmpty()) {
                    for (DatabaseCard card : query) {
                        if (card.getName().equalsIgnoreCase(cardName)) {
                            return cardName + " already exists in your collection";
                        }
                    }
                }
            } catch (Exception ignored) {
            }

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            List<Card> cards = new ArrayList<>();

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
                JsonArray cardsArray = jsonObject.getAsJsonArray("cards");

                Type cardListType = new TypeToken<List<Card>>() {
                }.getType();
                cards = new Gson().fromJson(cardsArray, cardListType);

            }

            for (Card card : cards) {
                if (card.getName().equalsIgnoreCase(cardName) && card.getSet().equalsIgnoreCase(set)) {
                    DatabaseCard tempDatabaseCard = new DatabaseCard(card, quantity);
                    cardDAO.save(tempDatabaseCard);
                    return cardName + " was added to your collection.";
                }
            }

            return cardName + " from set " + set + " cannot be found";
        } else {
            return null;
        }
    }

    /**
     * updates a card quantity
     *
     * @return a message screen
     */
    @Override
    @Transactional
    public String updateCard(String cardName, String numCards) throws ClassNotFoundException {

        int quantity;
        String url = "https://api.magicthegathering.io/v1/cards?name=" + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        if (cardName != null && numCards != null) {

            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered");
            }

            try {
                quantity = Integer.parseInt(numCards);
            } catch (NumberFormatException ex) {
                return "Invalid entry, please check that numCards is whole number value > 0";
            }

            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid entry, please check that numCards is whole number value > 0");
            }

            List<DatabaseCard> query;

            try {
                query = new ArrayList<>(cardDAO.findByName(cardName));
            } catch (NullPointerException ex) {
                return "Card '" + cardName + "' not found in your collection.";
            }

            query = new ArrayList<>(cardDAO.findByName(cardName));

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                List<Card> cards = new ArrayList<>();

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
                    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");

                    Type cardListType = new TypeToken<List<Card>>() {
                    }.getType();
                    cards = new Gson().fromJson(cardsArray, cardListType);
                }

                for (DatabaseCard databaseCard : query) {
                    if (databaseCard.getName().equalsIgnoreCase(cardName)) {
                        for (Card card : cards) {
                            if (databaseCard.getMultiverseId() == card.getMultiverseid()) {
                                databaseCard.setQuantity(quantity);
                                cardDAO.update(databaseCard);
                                break;
                            }
                        }
                    }
                }

                return cardName + " quantity updated";

            } catch (Exception ex) {
                return "Update error.";
            }
        } else {
            return null;
        }
    }

    /**
     * update a card's quantity and/or set
     *
     * @return a message screen
     */
    @Override
    @Transactional
    public String updateCard(String cardName, String numCards, String set) throws ClassNotFoundException {

        int quantity;
        String url = "https://api.magicthegathering.io/v1/cards?name=" + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        if (cardName != null && numCards != null && set != null) {

            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered");
            }

            try {
                quantity = Integer.parseInt(numCards);
            } catch (NumberFormatException ex) {
                return "Invalid entry, please check that numCards is whole number value > 0";
            }

            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid entry, please check that numCards is whole number value > 0");
            }

            List<DatabaseCard> query;

            try {
                query = new ArrayList<>(cardDAO.findByName(cardName));
            } catch (NullPointerException ex) {
                return "Card '" + cardName + "' not found in your collection.";
            }

            query = new ArrayList<>(cardDAO.findByName(cardName));

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                List<Card> cards = new ArrayList<>();

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
                    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");

                    Type cardListType = new TypeToken<List<Card>>() {
                    }.getType();
                    cards = new Gson().fromJson(cardsArray, cardListType);
                }

                for (DatabaseCard databaseCard : query) {
                    if (databaseCard.getName().equalsIgnoreCase(cardName)) {
                        for (Card card : cards) {
                            if (databaseCard.getMultiverseId() == card.getMultiverseid()) {
                                databaseCard.setQuantity(quantity);
                                databaseCard.setSet(set);
                                cardDAO.update(databaseCard);
                                break;
                            }
                        }
                    }
                }

                return cardName + " updated";

            } catch (Exception ex) {
                return "Update error.";
            }
        } else {
            return null;
        }
    }

    /**
     * removes card from collection based off card name
     *
     * @return a message screen
     */
    @Override
    @Transactional
    public String removeCardFromCollection(String cardName) {

        String url = "https://api.magicthegathering.io/v1/cards?name=" + URLEncoder.encode(cardName, StandardCharsets.UTF_8);

        if (cardName != null) {

            if (cardName.isBlank() || cardName.isEmpty()) {
                throw new IllegalArgumentException("Invalid entry, please check that cardName is entered.");
            }

            List<DatabaseCard> query;

            try {
                query = new ArrayList<>(cardDAO.findByName(cardName));
            } catch (NullPointerException ex) {
                return "Card '" + cardName + "' not found in your collection.";
            }

            query = new ArrayList<>(cardDAO.findByName(cardName));

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                List<Card> cards = new ArrayList<>();

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonObject jsonObject = JsonParser.parseString(response.getBody()).getAsJsonObject();
                    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");

                    Type cardListType = new TypeToken<List<Card>>() {
                    }.getType();
                    cards = new Gson().fromJson(cardsArray, cardListType);
                }

                for (DatabaseCard databaseCard : query) {
                    if (databaseCard.getName().equalsIgnoreCase(cardName)) {
                        for (Card card : cards) {
                            if (databaseCard.getMultiverseId() == card.getMultiverseid()) {
                                cardDAO.delete(databaseCard.getName());
                                break;
                            }
                        }
                    }
                }

                return cardName + " removed from collection.";

            } catch (Exception ex) {
                return "Deletion error.";
            }
        } else {
            return null;
        }
    }

    /**
     * removes a card from the collection based on card Id number in stored database
     *
     * @return refreshes the main page
     */
    @Override
    @Transactional
    public String removeCardFromCollection(long id) {

        cardDAO.delete(id);

        return "redirect:/app/collection";
    }
}