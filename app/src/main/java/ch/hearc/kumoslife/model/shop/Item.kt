package ch.hearc.kumoslife.model.shop

abstract class Item
{
    abstract var name: String
    abstract var prize: Double
    abstract var imageId: Int
    abstract fun info(): String
}

