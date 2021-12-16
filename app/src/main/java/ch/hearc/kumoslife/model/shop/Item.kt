package ch.hearc.kumoslife.model.shop

abstract class Item(val name: String?, val prize: Double, val id: Int)
{
    abstract fun info(): String
}

