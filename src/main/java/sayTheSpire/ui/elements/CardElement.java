package sayTheSpire.ui.elements;

import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DiscardPileViewScreen;
import com.megacrit.cardcrawl.screens.DrawPileViewScreen;
import com.megacrit.cardcrawl.screens.ExhaustPileViewScreen;
import com.megacrit.cardcrawl.screens.MasterDeckViewScreen;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import java.util.ArrayList;
import sayTheSpire.ui.positions.*;
import sayTheSpire.buffers.BufferManager;
import sayTheSpire.Output;
import sayTheSpire.utils.CardUtils;
import sayTheSpire.utils.OutputUtils;

public class CardElement extends GameObjectElement {

    public enum CardLocation {
        HAND, GRID_SELECT, HAND_SELECT, MASTER_DECK_VIEW, EXHAUST_PILE_VIEW, DISCARD_PILE_VIEW, DRAW_PILE_VIEW, SHOP,
        COMPENDIUM, OTHER
    }

    protected AbstractCard card;
    private CardLocation location;

    public CardElement(AbstractCard card) {
        this(card, CardLocation.OTHER);
    }

    public CardElement(AbstractCard card, CardLocation location) {
        this(card, location, null);
    }

    public CardElement(AbstractCard card, CardLocation location, AbstractPosition position) {
        super("card", position);
        this.card = card;
        this.location = location;
    }

    public String handleBuffers(BufferManager buffers) {
        buffers.getBuffer("current card").setObject(card);
        buffers.getBuffer("upgrade preview").setObject(card);
        buffers.enableBuffer("current card", true);
        buffers.enableBuffer("upgrade preview", true);
        return "current card";
    }

    public String getExtrasString() {
        if (this.card.isFlipped || this.card.isLocked)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(CardUtils.getCardCostString(this.card));
        if (this.location == CardLocation.SHOP)
            sb.append(", " + this.getPriceString());
        return sb.toString();
    }

    public String getLabel() {
        if (this.card.isFlipped)
            return "face down card";
        else if (this.card.isLocked)
            return this.card.LOCKED_STRING;
        return this.card.name;
    }

    public GridPosition getGridPosition(ArrayList<AbstractCard> grid, int width) {
        if (grid == null)
            return null;
        int gridIndex = grid.indexOf(this.card);
        if (gridIndex < 0)
            return null;
        int row = gridIndex / width + 1;
        int column = gridIndex % width + 1;
        return new GridPosition(column, row);
    }

    public ListPosition getListPosition(ArrayList<AbstractCard> list) {
        if (list == null)
            return null;
        int index = list.indexOf(this.card);
        if (index < 0)
            return null;
        return new ListPosition(index, list.size());
    }

    public AbstractPosition getPosition() {
        int cardsPerLine = 5; // default
        switch (this.location) {
        case HAND:
            return this.getListPosition(OutputUtils.getPlayer().hand.group);
        case GRID_SELECT:
            ArrayList<AbstractCard> grid = AbstractDungeon.gridSelectScreen.targetGroup.group;
            return this.getGridPosition(grid, cardsPerLine);
        case HAND_SELECT:
            HandCardSelectScreen screen = AbstractDungeon.handCardSelectScreen;
            if (screen == null)
                return null;
            CardGroup unselectedGroup = (CardGroup) ReflectionHacks.getPrivate(screen, HandCardSelectScreen.class,
                    "hand");
            ArrayList<AbstractCard> unselected = unselectedGroup.group;
            ArrayList<AbstractCard> selected = screen.selectedCards.group;
            AbstractPosition result = this.getListPosition(selected);
            if (result == null)
                result = this.getListPosition(unselected);
            return result;
        case MASTER_DECK_VIEW:
            MasterDeckViewScreen masterScreen = AbstractDungeon.deckViewScreen;
            ArrayList<AbstractCard> group = (ArrayList<AbstractCard>) ReflectionHacks.getPrivate(masterScreen,
                    MasterDeckViewScreen.class, "tmpSortedDeck");
            if (group == null) {
                group = AbstractDungeon.player.masterDeck.group;
            }
            return this.getGridPosition(group, cardsPerLine);
        case EXHAUST_PILE_VIEW:
            CardGroup exhaustGroup = (CardGroup) ReflectionHacks.getPrivate(AbstractDungeon.exhaustPileViewScreen,
                    ExhaustPileViewScreen.class, "exhaustPileCopy");
            if (exhaustGroup == null)
                return null;
            cardsPerLine = (int) ReflectionHacks.getPrivate(AbstractDungeon.exhaustPileViewScreen,
                    ExhaustPileViewScreen.class, "CARDS_PER_LINE");
            return this.getGridPosition(exhaustGroup.group, cardsPerLine);
        case DRAW_PILE_VIEW:
            CardGroup drawGroup = (CardGroup) ReflectionHacks.getPrivate(AbstractDungeon.gameDeckViewScreen,
                    DrawPileViewScreen.class, "drawPileCopy");
            if (drawGroup == null)
                return null;
            cardsPerLine = (int) ReflectionHacks.getPrivate(AbstractDungeon.gameDeckViewScreen,
                    DrawPileViewScreen.class, "CARDS_PER_LINE");
            return this.getListPosition(drawGroup.group);
        case DISCARD_PILE_VIEW:
            CardGroup discardGroup = (CardGroup) ReflectionHacks.getPrivate(AbstractDungeon.discardPileViewScreen,
                    DiscardPileViewScreen.class, "discardPileCopy");
            if (discardGroup == null)
                return null;
            cardsPerLine = (int) ReflectionHacks.getPrivate(AbstractDungeon.discardPileViewScreen,
                    DiscardPileViewScreen.class, "CARDS_PER_LINE");
            return this.getGridPosition(discardGroup.group, cardsPerLine);
        default:
            return super.getPosition();
        }
    }

}
