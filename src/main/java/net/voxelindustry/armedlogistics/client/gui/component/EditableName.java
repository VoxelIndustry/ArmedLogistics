package net.voxelindustry.armedlogistics.client.gui.component;

import net.voxelindustry.brokkgui.data.RectBox;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.input.GuiButton;
import net.voxelindustry.brokkgui.element.input.GuiTextfield;
import net.voxelindustry.brokkgui.event.KeyEvent;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EditableName extends GuiAbsolutePane
{
    private final GuiLabel     nameLabel;
    private final GuiButton    editButton;
    private final GuiTextfield nameField;

    private Consumer<String> nameSetter;

    public EditableName(Supplier<String> nameGetter, Consumer<String> nameSetter)
    {
        this.nameSetter = nameSetter;

        setHeight(9);

        nameLabel = new GuiLabel(nameGetter.get());
        nameLabel.setExpandToText(true);
        addChild(nameLabel, 0, 0);

        editButton = new GuiButton();
        editButton.setID("edit-button");
        editButton.setSize(9, 9);
        editButton.setzLevel(10);
        addChild(editButton);
        RelativeBindingHelper.bindToPos(editButton, nameLabel, nameLabel.getWidthProperty(), null);

        nameField = new GuiTextfield();
        nameField.setID("edit-field");
        nameField.setVisible(false);
        nameField.setHeight(9);
        nameField.setTextPadding(RectBox.EMPTY);
        addChild(nameField, 0, 0);

        editButton.setOnActionEvent(e ->
        {
            if (nameLabel.isVisible())
                startEditing();
            else
                stopEditing();
        });

        nameField.getEventDispatcher().addHandler(KeyEvent.PRESS, e ->
        {
            if (nameLabel.isVisible())
                return;
            if (e.getKey() != GLFW.GLFW_KEY_ENTER && e.getKey() != GLFW.GLFW_KEY_KP_ENTER)
                return;
            stopEditing();
        });
    }

    private void startEditing()
    {
        nameLabel.setVisible(false);
        nameField.setVisible(true);
        nameField.setWidth(nameLabel.getWidth() + 10);

        RelativeBindingHelper.bindToPos(editButton, nameField, nameField.getWidthProperty(), null);

        nameField.setText(nameLabel.getText());
        nameField.setCursorPos(nameField.getText().length());
    }

    private void stopEditing()
    {
        nameSetter.accept(nameField.getText());
        nameLabel.setText(nameField.getText());

        nameField.setVisible(false);
        nameLabel.setVisible(true);
        RelativeBindingHelper.bindToPos(editButton, nameLabel, nameLabel.getWidthProperty(), null);
    }
}
