package ch.hearc.kumoslife.views.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

        adapter = ItemAdapter()

        val recyclerView: RecyclerView = findViewById(R.id.shopRecyclerView)
        recyclerView.stopScroll()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.onItemClick = { item ->
            Toast.makeText(applicationContext, item.name, Toast.LENGTH_SHORT).show()
        }

        adapter.getImageId = this::getImageRId

        // Back to main
        findViewById<Button>(R.id.returnToMainButton).setOnClickListener() {
            finish()
        }
    }

    private fun getImageRId(s: String): Int
    {
        return resources.getIdentifier(s, "drawable", packageName)
    }

    private fun updateResult(list : List<Item>)
    {
        adapter.setData(list)
    }

}