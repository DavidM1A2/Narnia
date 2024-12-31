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
import com.dslovikosky.narnia.common.model.NarniaGlobalData;
import com.dslovikosky.narnia.common.model.chapter.Book;
import com.dslovikosky.narnia.common.model.chapter.Chapter;
import com.dslovikosky.narnia.common.model.chapter.Character;
import com.dslovikosky.narnia.common.model.chapter.Scene;
import com.dslovikosky.narnia.common.network.packet.JoinScenePacket;
import com.dslovikosky.narnia.common.network.packet.StartScenePacket;
import com.dslovikosky.narnia.common.network.packet.StopScenePacket;
import com.ibm.icu.text.RuleBasedNumberFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import org.codehaus.plexus.util.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;
import java.util.List;

public class TheChroniclesOfNarniaBookScreen extends BaseScreen {
    private static final RuleBasedNumberFormat NUMBER_FORMATTER = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);

    private final Book book;
    private final ButtonPane forwardButton;
    private final ButtonPane backwardButton;
    private final TextBoxComponent titleBox;
    private final ButtonPane startButton;
    private final ButtonPane stopButton;
    private final LabelComponent joinSceneLabel;
    private final HChainPane joinSceneOptions;

    private int chapterIndex = 0;

    public TheChroniclesOfNarniaBookScreen(final Book book) {
        super(book.title());
        this.book = book;

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

        titleBox = new TextBoxComponent(TtfFontLoader.getNarniaFont(40f), "");
        titleBox.setTextAlignment(TextAlignment.ALIGN_CENTER);
        titleBox.setPrefSize(new Dimensions(1, 0.14, true));
        titleBox.setTextColor(new Color(16, 140, 0));
        leftPage.add(titleBox);

        backgroundImage.add(leftPage);
        backgroundImage.add(rightPage);

        final ImagePane leftTitleDivider = new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/title_divider.png"), ImagePane.DisplayMode.STRETCH);
        leftTitleDivider.setPrefSize(new Dimensions(100, 10, false));
        leftTitleDivider.setGravity(Gravity.TOP_CENTER);
        leftTitleDivider.setOffset(new Position(0.0, 0.14, true));
        leftPage.add(leftTitleDivider);

        startButton = new ButtonPane(
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                TtfFontLoader.getTextFont(26f, true));
        startButton.setPrefSize(new Dimensions(50, 10, false));
        startButton.setOffset(new Position(0.0, 0.2, true));
        startButton.setGravity(Gravity.TOP_CENTER);
        startButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
        startButton.setText("Start Chapter");
        leftPage.add(startButton);

        stopButton = new ButtonPane(
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                TtfFontLoader.getTextFont(26f, true));
        stopButton.setPrefSize(new Dimensions(50, 10, false));
        stopButton.setOffset(new Position(0.0, 0.2, true));
        stopButton.setGravity(Gravity.TOP_CENTER);
        stopButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
        stopButton.setText("Stop Chapter");
        leftPage.add(stopButton);

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

        joinSceneLabel = new LabelComponent(TtfFontLoader.getTextFont(26f, true), Component.translatable("gui.narnia.book.join_chapter_as").getString());
        joinSceneLabel.setPrefSize(new Dimensions(1.0, 0.04, true));
        joinSceneLabel.setOffset(new Position(0.0, -20.0, false));
        joinSceneLabel.setGravity(Gravity.BOTTOM_CENTER);
        joinSceneLabel.setTextAlignment(TextAlignment.ALIGN_CENTER);
        joinSceneLabel.setTextColor(new Color(16, 140, 0));
        leftPage.add(joinSceneLabel);

        joinSceneOptions = new HChainPane(ChainLayout.SPREAD);
        joinSceneOptions.setPrefSize(new Dimensions(0.85, 0.04, true));
        joinSceneOptions.setOffset(new Position(0.0, -10.0, false));
        joinSceneOptions.setGravity(Gravity.BOTTOM_CENTER);

        leftPage.add(joinSceneOptions);

        contentPane.add(backgroundPane);

        refreshPages();
    }

    private boolean hasPreviousPage() {
        return chapterIndex > 0;
    }

    private boolean hasNextPage() {
        return chapterIndex < book.getChapters().size() - 1;
    }

    private void rewindPage() {
        if (!hasPreviousPage()) {
            return;
        }
        chapterIndex = Math.max(0, chapterIndex - 1);
        refreshPages();
        Minecraft.getInstance().player.playSound(ModSoundEvents.PAGE_TURN.get(), 1f, 1f);
    }

    private void advancePage() {
        if (!hasNextPage()) {
            return;
        }
        chapterIndex = Math.min(book.getChapters().size() - 1, chapterIndex + 1);
        refreshPages();
        Minecraft.getInstance().player.playSound(ModSoundEvents.PAGE_TURN.get(), 1f, 1f);
    }

    public void refreshPages() {
        final Chapter currentChapter = book.getChapters().get(chapterIndex);

        forwardButton.setVisible(hasNextPage());
        backwardButton.setVisible(hasPreviousPage());

        titleBox.setText(Component.translatable("gui.narnia.book.chapter_header", StringUtils.capitalise(NUMBER_FORMATTER.format(chapterIndex + 1)), currentChapter.title()).getString());

        final Scene activeScene = NarniaGlobalData.getInstance(true).getActiveScene();

        joinSceneOptions.getChildren().clear();
        joinSceneLabel.setVisible(false);
        startButton.setVisible(false);
        stopButton.setVisible(false);
        if (activeScene == null) {
            // Show a "Start Chapter" button
            startButton.setVisible(true);
            startButton.clearMouseListeners();
            startButton.addOnClick(event -> PacketDistributor.sendToServer(new StartScenePacket(currentChapter)));
        } else if (activeScene.getChapter() == currentChapter) {
            if (currentChapter.isParticipatingIn(activeScene, Minecraft.getInstance().player)) {
                // Show a "Leave Chapter" button
                stopButton.setVisible(true);
                stopButton.clearMouseListeners();
                stopButton.addOnClick(event -> PacketDistributor.sendToServer(new StopScenePacket()));
            } else {
                // Show a "Join Chapter As" label and buttons per actor
                joinSceneLabel.setVisible(true);
                final List<? extends Character> characters = currentChapter.characters().get();
                final int joinOptionCount = characters.size() + 1;
                final ButtonPane joinSceneButton = new ButtonPane(
                        new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                        new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                        TtfFontLoader.getTextFont(26f, true));
                joinSceneButton.setPrefSize(new Dimensions(Math.max(0.3, 0.9f / joinOptionCount), 1.0, true));
                joinSceneButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
                joinSceneButton.setText("Spectator");
                joinSceneButton.addOnClick(event -> PacketDistributor.sendToServer(new JoinScenePacket()));
                joinSceneOptions.add(joinSceneButton);
                for (final Character character : characters) {
                    if (character.isPlayable()) {
                        final ButtonPane startSceneButton = new ButtonPane(
                                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button.png"), ImagePane.DisplayMode.STRETCH),
                                new ImagePane(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/narnia_book/book_button_hovered.png"), ImagePane.DisplayMode.STRETCH),
                                TtfFontLoader.getTextFont(26f, true));
                        startSceneButton.setPrefSize(new Dimensions(Math.max(0.3, 0.9f / joinOptionCount), 1.0, true));
                        startSceneButton.setTextAlignment(TextAlignment.ALIGN_CENTER);
                        startSceneButton.setText(String.format("%s", character.getName().getString()));
                        startSceneButton.addOnClick(event -> PacketDistributor.sendToServer(new JoinScenePacket(character)));
                        joinSceneOptions.add(startSceneButton);
                    }
                }
            }
        } else {
            // Show nothing
        }
        joinSceneOptions.calcChildrenBounds();
    }
}
