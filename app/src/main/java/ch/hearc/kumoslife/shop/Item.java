package ch.hearc.kumoslife.shop;

import android.media.Image;

public abstract class Item
{
    private final String name;
    private double prize;

    public Item(String name, double prize)
    {
        this.name = name;
        this.prize = prize;
    }

    public String getName()
    {
        return name;
    }

    public double getPrize()
    {
        return prize;
    }

    public abstract String getInfos();


}
