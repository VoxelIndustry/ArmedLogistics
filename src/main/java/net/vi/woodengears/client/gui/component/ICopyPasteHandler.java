package net.vi.woodengears.client.gui.component;

public interface ICopyPasteHandler<T>
{
    void setClipboard(T value);

    T getClipboard();

    default T clearClipboard()
    {
        T value = getClipboard();
        setClipboard(null);
        return value;
    }
}
