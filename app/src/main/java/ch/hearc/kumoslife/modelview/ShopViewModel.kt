package ch.hearc.kumoslife.modelview

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ch.hearc.kumoslife.model.AppDatabase
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.Item
import ch.hearc.kumoslife.model.shop.ShopDao
import java.util.concurrent.Executors

class ShopViewModel: ViewModel()
{
    private lateinit var db: AppDatabase
    private lateinit var shopDao: ShopDao

    // Static instance: singleton
    companion object
    {
        private var instance: ShopViewModel? = null;

        fun getInstance(activity: AppCompatActivity): ShopViewModel
        {
            if (instance == null)
            {
                instance = ViewModelProvider(activity).get(ShopViewModel::class.java)
            }
            return instance as ShopViewModel
        }

        fun getInstance(): ShopViewModel
        {
            return instance as ShopViewModel
        }
    }

    fun setDatabase(db: AppDatabase)
    {
        this.db = db
        shopDao = db.shopDao()
    }

    fun getAllFood(updateResults: (List<Item>) -> Unit)
    {
        Executors.newSingleThreadExecutor().execute {
            updateResults(shopDao.getAllFood())
        }
    }

    fun deleteAllFood()
    {
        Executors.newSingleThreadExecutor().execute {
            shopDao.deleteAll()
        }
    }

    // Provisional, must be chek if database is already filled
    fun resetFood()
    {
        Executors.newSingleThreadExecutor().execute {
            shopDao.deleteAll()
            shopDao.insert(Food(0, "Apple", 30, 20))
            shopDao.insert(Food(0, "Apricot", 30, 20))
            shopDao.insert(Food(0, "Cherry", 30, 20))
            shopDao.insert(Food(0, "Grapes", 30, 20))
            shopDao.insert(Food(0, "Pineapple", 30, 20))
            shopDao.insert(Food(0, "Strawberry", 30, 20))
            shopDao.insert(Food(0, "Pear", 30, 20))
            shopDao.insert(Food(0, "Banana", 30, 20))
            shopDao.insert(Food(0, "ChillPepper", 20, 10))
            shopDao.insert(Food(0, "Tomato", 30, 20))
            shopDao.insert(Food(0, "Carrot", 30, 20))
            shopDao.insert(Food(0, "Paprika", 30, 20))
            shopDao.insert(Food(0, "Broccoli", 30, 20))
            shopDao.insert(Food(0, "Burger", 60, 40))
            shopDao.insert(Food(0, "FrenchFries", 40, 30))
            shopDao.insert(Food(0, "Kebab", 70, 50))
            shopDao.insert(Food(0, "Sandwich", 50, 40))
            shopDao.insert(Food(0, "Hotdog", 50, 20))
            shopDao.insert(Food(0, "Turkey", 50, 20))
            shopDao.insert(Food(0, "Bread", 50, 20))
            shopDao.insert(Food(0, "IceCream", 50, 30))
            shopDao.insert(Food(0, "Chocolate", 40, 30))
            shopDao.insert(Food(0, "Croissant", 40, 30))
            shopDao.insert(Food(0, "Cake", 40, 30))
            shopDao.insert(Food(0, "Candy", 20, 10))
            shopDao.insert(Food(0, "Pie", 40, 30))

        }
    }

    fun insertFood(food: Food)
    {
        // Insertion in data base
        Executors.newSingleThreadExecutor().execute {
            shopDao.insert(food)
        }
    }
}