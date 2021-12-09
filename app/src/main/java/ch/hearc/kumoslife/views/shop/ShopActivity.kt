package ch.hearc.kumoslife.views.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.model.shop.Food
import ch.hearc.kumoslife.model.shop.Item
import ch.hearc.kumoslife.modelview.ShopViewModel
import ch.hearc.kumoslife.views.shop.ItemAdapter

class ShopActivity : AppCompatActivity()
{
    lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_shop)

        ShopViewModel.getInstance().getAllFood(this::updateResult)
        /*list.add(Food(0,"Frites", 10.0, 5.0, getImageRId("frites")))
        list.add(Food(0,"Glace", 15.0, 5.0, getImageRId("glace")))*/

        adapter = ItemAdapter()


        val recyclerView: RecyclerView = findViewById(R.id.shopRecyclerView)
        recyclerView.stopScroll()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Back to main
        findViewById<Button>(R.id.returnToMainButton).setOnClickListener() {
            finish()
        }
    }

    private fun updateResult(list : List<Item>)
    {
        adapter.setData(list)
    }

    private fun getImageRId(s: String): Int
    {
        return resources.getIdentifier(s, "drawable", packageName)
    }
}