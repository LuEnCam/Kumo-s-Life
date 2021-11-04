package ch.hearc.kumoslife.shop;

public class Food extends Item
{
    private double nutritiveValue;
    public Food(String name, double prize, double nutritiveValue) {
        super(name, prize);
        this.nutritiveValue = nutritiveValue;
    }

    public double getNutritiveValue()
    {
        return nutritiveValue;
    }

    @Override
    public String getInfos() {
        return "Nutritive value : " + nutritiveValue;
    }
}
