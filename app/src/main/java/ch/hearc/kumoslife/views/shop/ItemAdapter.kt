package ch.hearc.kumoslife.views.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.hearc.kumoslife.R
import ch.hearc.kumoslife.model.shop.Item
import kotlin.collections.ArrayList

class ItemAdapter() : RecyclerView.Adapter<ItemAdapter.ViewHolder>()
{
    private var dataList: List<Item> = ArrayList()
    var onItemClick: ((Item) -> Unit)? = null
    var getImageId: ((String) -> Int)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val name: TextView = itemView.findViewById(R.id.nameItem)
        val prize: TextView = itemView.findViewById(R.id.prizeItem)
        val infos: TextView = itemView.findViewById(R.id.infosItem)
        val image: ImageView = itemView.findViewById(R.id.itemImageView)

        init
        {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        val viewItem = inflater.inflate(R.layout.shop_element, parent, false)

        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val data = dataList[position]
        holder.name.text = data.name
        holder.prize.text = data.prize.toString()
        holder.infos.text = data.info()
        var id: Int? = getImageId?.invoke(data.name.lowercase())
        if (id == null)
        {
            id = R.drawable.apple
        }
        holder.image.setImageResource(id)
    }

    override fun getItemCount(): Int
    {
        return dataList.size
    }


    fun setData(dataList: List<Item>)
    {
        this.dataList = dataList
    }
}