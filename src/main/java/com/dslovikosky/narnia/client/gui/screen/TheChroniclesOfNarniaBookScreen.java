package com.dslovikosky.narnia.client.gui.screen;

import com.dslovikosky.narnia.client.gui.control.ButtonPane;
import com.dslovikosky.narnia.client.gui.control.HChainPane;
import com.dslovikosky.narnia.client.gui.control.ImagePane;
import com.dslovikosky.narnia.client.gui.control.LabelComponent;
import com.dslovikosky.narnia.client.gui.control.StackPane;
import com.dslovikosky.narnia.client.gui.control.TextBoxComponent;
import com.dslovikosky.narnia.client.gui.event.KeyEvent;
import com.dslovikosky.narnia.client.gui.font.TtfFontLoader;
import com.dslovikosky.narnia.client.gui.layout.ChainLayout;
import com.dslovikosky.narnia.client.gui.layout.Dimensions;
import com.dslovikosky.narnia.client.gui.layout.Gravity;
import com.dslovikosky.narnia.client.gui.layout.Position;
import com.dslovikosky.narnia.client.gui.layout.TextAlignment;
import com.dslovikosky.narnia.common.constants.Constants;
import com.dslovikosky.narnia.common.constants.ModSoundEvents;
import com.dslovikosky.narnia.common.model.chapter.Book;
import com.dslovikosky.narnia.common.model.chapter.Chapter;
import com.dslovikosky.narnia.common.model.chapter.PlayableActor;
import com.ibm.icu.text.RuleBasedNumberFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.codehaus.plexus.util.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.List;

public class TheChroniclesOfNarniaBookScreen extends BaseScreen {
    private static final RuleBasedNumberFormat NUMBER_FORMATTER = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);

    private final List<Chapter> chapters;
    private final ButtonPane forwardButton;
    private final ButtonPane backwardButton;
    private final TextBoxComponent leftTitleBox;
    private final HChainPane leftStartSceneOptions;

    private int chapterIndex = 0;

    public TheChroniclesOfNarniaBookScreen(final Book book) {
        super(book.title());
        this.chapters = book.chapters();

        final StackPane backgroundPane = new StackPane();
        backgroundPane.setPrefSize(new Dimensions(256, 256, false));
        backgroundPane.setGravity(Gravity.CENTER);

        final ImagePane backgroundImage = new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/background.png"), ImagePane.DisplayMode.STRETCH);
        backgroundImage.setGravity(Gravity.CENTER);
        backgroundPane.add(backgroundImage);

        final StackPane leftPage = new StackPane();
        leftPage.setOffset(new Position(0.04, 0.05, true));
        leftPage.setPrefSize(new Dimensions(0.5 - 2 * 0.04, 0.85, true));
        leftPage.setGravity(Gravity.TOP_LEFT);

        final StackPane rightPage = new StackPane();
        rightPage.setOffset(new Position(-0.06, 0.05, true));
        rightPage.setPrefSize(new Dimensions(0.5 - 2 * 0.04, 0.6, true));
        rightPage.setGravity(Gravity.TOP_RIGHT);

        leftTitleBox = new TextBoxComponent(TtfFontLoader.getNarniaFont(40f), "");
        leftTitleBox.setTextAlignment(TextAlignment.ALIGN_CENTER);
        leftTitleBox.setPrefSize(new Dimensions(1, 0.14, true));
        leftTitleBox.setTextColor(new Color(16, 140, 0));
        leftPage.add(leftTitleBox);

        final ImagePane leftTitleDivider = new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/title_divider.png"), ImagePane.DisplayMode.STRETCH);
        leftTitleDivider.setPrefSize(new Dimensions(100, 10, false));
        leftTitleDivider.setGravity(Gravity.TOP_CENTER);
        leftTitleDivider.setOffset(new Position(0.0, 0.14, true));
        leftPage.add(leftTitleDivider);

        final TextBoxComponent rightTitleBox = new TextBoxComponent(TtfFontLoader.getNarniaFont(40f), "");
        rightTitleBox.setTextAlignment(TextAlignment.ALIGN_CENTER);
        rightTitleBox.setPrefSize(new Dimensions(1, 0.25, true));
        rightTitleBox.setTextColor(new Color(16, 140, 0));
        rightPage.add(rightTitleBox);

        backgroundImage.add(leftPage);
        backgroundImage.add(rightPage);

        forwardButton = new ButtonPane(
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/forward_button.png"), ImagePane.DisplayMode.STRETCH),
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/forward_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                null);
        forwardButton.setPrefSize(new Dimensions(10.0, 10.0, false));
        forwardButton.setOffset(new Position(-0.03, -0.10, true));
        forwardButton.setGravity(Gravity.BOTTOM_RIGHT);
        forwardButton.addOnClick(event -> advancePage());
        backwardButton = new ButtonPane(
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/backward_button.png"), ImagePane.DisplayMode.STRETCH),
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/backward_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                null);
        backwardButton.setPrefSize(new Dimensions(10.0, 10.0, false));
        backwardButton.setOffset(new Position(0.03, -0.10, true));
        backwardButton.setGravity(Gravity.BOTTOM_LEFT);
        backwardButton.addOnClick(event -> rewindPage());
        backgroundImage.add(forwardButton);
        backgroundImage.add(backwardButton);

        contentPane.addKeyListener(event -> {
            if (event.getEventType() == KeyEvent.KeyEventType.Press) {
                if (event.getKey() == GLFW.GLFW_KEY_A || event.getKey() == GLFW.GLFW_KEY_LEFT) {
                    rewindPage();
                    event.consume();
                } else if (event.getKey() == GLFW.GLFW_KEY_D || event.getKey() == GLFW.GLFW_KEY_RIGHT) {
                    advancePage();
                    event.consume();
                }
            }
        });

        final ButtonPane cancelSceneButton = new ButtonPane(
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                TtfFontLoader.getTextFont(26f, true));
        cancelSceneButton.setPrefSize(new Dimensions(60.0, 10.0, false));
        cancelSceneButton.setOffset(new Position(0.0, -10.0, false));
        cancelSceneButton.setGravity(Gravity.BOTTOM_CENTER);
        cancelSceneButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
        cancelSceneButton.setText("Cancel Scene");
//        leftPage.add(cancelSceneButton);

        final LabelComponent leftStartSceneLabel = new LabelComponent(TtfFontLoader.getTextFont(26f, true), Component.translatable("gui.narnia.book.start_chapter_as").getString());
        leftStartSceneLabel.setPrefSize(new Dimensions(1.0, 0.04, true));
        leftStartSceneLabel.setOffset(new Position(0.0, -20.0, false));
        leftStartSceneLabel.setGravity(Gravity.BOTTOM_CENTER);
        leftStartSceneLabel.setTextAlignment(TextAlignment.ALIGN_CENTER);
        leftStartSceneLabel.setTextColor(new Color(16, 140, 0));
        leftPage.add(leftStartSceneLabel);

        leftStartSceneOptions = new HChainPane(ChainLayout.SPREAD);
        leftStartSceneOptions.setPrefSize(new Dimensions(0.85, 0.04, true));
        leftStartSceneOptions.setOffset(new Position(0.0, -10.0, false));
        leftStartSceneOptions.setGravity(Gravity.BOTTOM_CENTER);

        leftPage.add(leftStartSceneOptions);

        contentPane.add(backgroundPane);

        refreshPages();
    }

    private boolean hasPreviousPage() {
        return chapterIndex > 0;
    }

    private boolean hasNextPage() {
        return chapterIndex < chapters.size() - 1;
    }

    private void rewindPage() {
        if (!hasPreviousPage()) {
            return;
        }
        chapterIndex = Math.max(0, chapterIndex - 1);
        refreshPages();
    }

    private void advancePage() {
        if (!hasNextPage()) {
            return;
        }
        chapterIndex = Math.min(chapters.size() - 1, chapterIndex + 1);
        refreshPages();
    }

    private void refreshPages() {
        Minecraft.getInstance().player.playSound(ModSoundEvents.PAGE_TURN.get(), 1f, 1f);
        final Chapter currentChapter = chapters.get(chapterIndex);

        forwardButton.setVisible(hasNextPage());
        backwardButton.setVisible(hasPreviousPage());

        leftTitleBox.setText(Component.translatable("gui.narnia.book.chapter_header", StringUtils.capitalise(NUMBER_FORMATTER.format(chapterIndex + 1)), currentChapter.title()).getString());

        leftStartSceneOptions.getChildren().clear();
        final List<PlayableActor> actors = currentChapter.actors();
        for (final PlayableActor actor : actors) {
            final ButtonPane startSceneButton = new ButtonPane(
                    new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                    new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                    TtfFontLoader.getTextFont(26f, true));
            startSceneButton.setPrefSize(new Dimensions(Math.max(0.3, 0.9f / actors.size()), 1.0, true));
            startSceneButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
            startSceneButton.setText(String.format("%s", actor.getName().getString()));
            leftStartSceneOptions.add(startSceneButton);
        }
        leftStartSceneOptions.calcChildrenBounds();
    }
}
