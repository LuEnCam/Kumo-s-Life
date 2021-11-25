package ch.hearc.kumoslife.shop

class Food(name: String?, prize: Double, nutritiveValue: Double, imageId: Int, ) : Item(name, prize, imageId) {
    private val nutritiveValue: Double?
    init {
        this.nutritiveValue = nutritiveValue
    }
    override fun info(): String {
        return "Nutritive Value: $nutritiveValue"
    }
}