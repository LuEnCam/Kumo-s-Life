package ch.hearc.kumoslife;

public class Statistic
{
    private final String name;
    private int value;

    public Statistic(String name, int value)
    {
        this.name = name;
        this.value = value;
    }

    public void setValue(int value)
    {
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
