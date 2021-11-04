package ch.hearc.kumoslife.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R

class ShopActivity : AppCompatActivity()
{
	lateinit var adapter: ItemAdapter

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_shop)

		val list: ArrayList<Item> = ArrayList()
		list.add(Food("Frites", 10.0, 5.0))
		list.add(Food("Soupe", 5.00,5.0))
		list.add(Food("Glace", 15.0, 5.0))
		list.add(Food("Chocolat", 20.0, 5.0))

		adapter = ItemAdapter()
		adapter.setData(list)

		val recyclerView: RecyclerView = findViewById(R.id.shopRecyclerView)
		recyclerView.stopScroll()
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter

		findViewById<Button>(R.id.returnToMainButton).setOnClickListener() {
			finish()
		}
	}
}