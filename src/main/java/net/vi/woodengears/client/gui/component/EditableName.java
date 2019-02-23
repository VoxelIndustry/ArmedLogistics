package net.vi.woodengears.client.gui.component;

import net.voxelindustry.brokkgui.data.RectOffset;
import net.voxelindustry.brokkgui.data.RelativeBindingHelper;
import net.voxelindustry.brokkgui.element.GuiButton;
import net.voxelindustry.brokkgui.element.GuiLabel;
import net.voxelindustry.brokkgui.element.GuiTextfield;
import net.voxelindustry.brokkgui.event.KeyEvent;
import net.voxelindustry.brokkgui.panel.GuiAbsolutePane;
import org.lwjgl.input.Keyboard;

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

        this.setWidthRatio(1);
        this.setHeight(9);

        this.nameLabel = new GuiLabel(nameGetter.get());
        nameLabel.setExpandToText(true);
        this.addChild(nameLabel, 0, 0);

        this.editButton = new GuiButton();
        editButton.setID("edit-button");
        editButton.setSize(9, 9);
        this.addChild(editButton);
        RelativeBindingHelper.bindToPos(editButton, nameLabel, nameLabel.getWidthProperty(), null);

        this.nameField = new GuiTextfield();
        nameField.setID("edit-field");
        nameField.setVisible(false);
        nameField.setHeight(9);
        nameField.setTextPadding(RectOffset.EMPTY);
        this.addChild(nameField, 0, 0);

        editButton.setOnActionEvent(e ->
        {
            if (nameLabel.isVisible())
                this.startEditing();
            else
                this.stopEditing();
        });

        this.nameField.getEventDispatcher().addHandler(KeyEvent.PRESS, e ->
        {
            if (nameLabel.isVisible())
                return;
            if (e.getKey() != Keyboard.KEY_RETURN && e.getKey() != Keyboard.KEY_NUMPADENTER)
                return;
            this.stopEditing();
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
