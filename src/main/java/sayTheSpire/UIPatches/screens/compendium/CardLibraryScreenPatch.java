import java.util.ArrayList;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.compendium.CardLibraryScreen;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import basemod.ReflectionHacks;
import sayTheSpire.ui.positions.CategoryListPosition;
import sayTheSpire.ui.elements.CardElement;
import sayTheSpire.Output;

public class CardLibraryScreenPatch {

    private static AbstractCard prevHoveredCard = null;

    @SpirePatch(clz = CardLibraryScreen.class, method = "update")
    public static class UpdatePatch {

        public static void Postfix(CardLibraryScreen __instance) {
            AbstractCard hoveredCard = (AbstractCard) ReflectionHacks.getPrivate(__instance, CardLibraryScreen.class,
                    "hoveredCard");
            if (hoveredCard == prevHoveredCard)
                return;
            prevHoveredCard = hoveredCard;
            if (hoveredCard == null) {
                return;
            }

            CardGroup visibleCards = (CardGroup) ReflectionHacks.getPrivate(__instance, CardLibraryScreen.class,
                    "visibleCards");
            if (visibleCards == null)
                return;

            int visibleCardCount = visibleCards.group.size();
            int index = visibleCards.group.indexOf(hoveredCard);
            ColorTabBar colorBar = (ColorTabBar) ReflectionHacks.getPrivate(__instance, CardLibraryScreen.class,
                    "colorBar");
            String category = "unknown cards";
            if (colorBar != null) {
                category = colorBar.curTab.name().toLowerCase() + " cards";
            }
            CategoryListPosition position = new CategoryListPosition(index, visibleCardCount, category);
            CardElement element = new CardElement(hoveredCard, CardElement.CardLocation.COMPENDIUM, position);
            Output.setUI(element);
        }
    }
}
