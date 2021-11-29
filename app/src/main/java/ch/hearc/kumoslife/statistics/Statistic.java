package ch.hearc.kumoslife.statistics;

public class Statistic
{
    private final String name;
    private double value;
    private final double progress;

    public Statistic(String name, double value, double progress)
    {
        this.name = name;
        this.value = value;
        this.progress = progress;
    }

    public void progress()
    {
        value += progress;
        if (value > 100)
            value = 100;
    }

    public void decrease(double decreaseValue)
    {
        value -= decreaseValue;
        if (value < 0)
            value = 0;
    }

    public double getValue()
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
