package ch.hearc.kumoslife;

import androidx.annotation.NonNull;

public class Statistic
{
    private final String name;
    private final int value;

    public Statistic(String name, int value)
    {
        this.name = name;
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("name: ");
        builder.append(name);
        builder.append(", value: ");
        builder.append(value);
        return builder.toString();
    }
}
