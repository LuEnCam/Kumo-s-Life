package ch.hearc.kumoslife.model.shop

abstract class Item
{
    abstract var name: String
    abstract var prize: Int
    abstract fun info(): String
}

