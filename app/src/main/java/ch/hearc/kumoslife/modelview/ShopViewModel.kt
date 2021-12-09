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
    fun resetFood(getImageRId: (String) -> Int)
    {
        Executors.newSingleThreadExecutor().execute {
            shopDao.deleteAll()
            shopDao.insert(Food(0, "glace", 2.0, 3.0, getImageRId("glace")))
            shopDao.insert(Food(0, "frites", 2.0, 3.0, getImageRId("frites")))
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