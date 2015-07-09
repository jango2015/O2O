package so.contacts.hub.thirdparty.cinema.widget;


public abstract interface OnSeatClickListener
{
    public abstract void viewTouched();

    public abstract boolean cancel(int paramInt1, int paramInt2, boolean paramBoolean);

    public abstract boolean choose(int paramInt1, int paramInt2, boolean paramBoolean);

    public abstract void selectSeatMax();
}