package ch.hearc.kumoslife.views.shop

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.Item
import ch.hearc.kumoslife.modelview.ShopViewModel
import ch.hearc.kumoslife.modelview.StatisticViewModel

class ShopActivity : AppCompatActivity()
{
    lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()

        setContentView(R.layout.activity_shop)
        adapter = ItemAdapter()

        ShopViewModel.getInstance().getAllFood(this::updateResult)

        val recyclerView: RecyclerView = findViewById(R.id.shopRecyclerView)
        recyclerView.stopScroll()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.onItemClick = { item ->

            if (item is Food)
            {
                if (removeMoney(item.prize))
                {
                    val statisticViewModel = StatisticViewModel.getInstance();
                    val stat = statisticViewModel.getStatisticByName("Hunger")
                    if (stat != null)
                    {
                        //val prec: String = stat.name + stat.value
                        statisticViewModel.decrease(item.nutritiveValue.toDouble(), stat)


                        Toast.makeText(applicationContext, "Miam !\nKumo a encroe faim de " + stat.value, Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(applicationContext, "Oooops tu n'as pas assez d'argent !", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adapter.getImageId = this::getImageRId

        // Back to main
        findViewById<Button>(R.id.returnToMainButton).setOnClickListener() {
            finish()
        }

        val textView: TextView = findViewById<TextView>(R.id.money)
        textView.text = getMoney().toString()
    }


    private fun setMoney(money: Int)
    {
        val mPrefs = getSharedPreferences("bag", 0)
        val mEditor = mPrefs.edit()
        mEditor.putInt("money", money).commit()
        val textView: TextView = findViewById<TextView>(R.id.money)
        textView.text = getMoney().toString()

    }

    private fun getMoney(): Int
    {
        val mPrefs = getSharedPreferences("bag", 0)
        return mPrefs.getInt("money", 0)
    }

    private fun removeMoney(remove: Int): Boolean
    {
        val money = getMoney()
        if (money >= remove)
        {
            setMoney(getMoney() - remove)
            return true
        }
        return false
    }

    private fun getImageRId(s: String): Int
    {
        return resources.getIdentifier(s, "drawable", packageName)
    }

    private fun updateResult(list: List<Item>)
    {
        adapter.setData(list)
    }

}