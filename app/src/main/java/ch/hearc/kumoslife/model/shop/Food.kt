package ch.hearc.kumoslife.model.shop

class Food(name: String?, prize: Double, val nutritiveValue: Double, imageId: Int) : Item(name, prize, imageId)
{
    override fun info(): String
    {
        return "Nutritive Value: $nutritiveValue"
    }
}