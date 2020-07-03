package com.bugli.bmovie.ui.recommend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.util.GetPoint
import kotlinx.android.synthetic.main.item_recyclerview.view.*

class RVMyAdapter(context: Context) :
    RecyclerView.Adapter<RVMyAdapter.MyViewHolder>() {
    var mContext: Context = context
    var list: MutableList<Movie> = ArrayList()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.tv
        var rootLL: LinearLayout = itemView.item_rv

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = list[position].movieName
        val screenHeight = GetPoint.getWinWH(mContext).y
        val screenWidth = GetPoint.getWinWH(mContext).x
        val lp1: RecyclerView.LayoutParams =
            holder.rootLL.layoutParams as RecyclerView.LayoutParams
        //topView 和 BottomView 占了 1/4 ，所以只剩下 3/4
        lp1.height = screenHeight * 3 / 4 - screenHeight / 4
        lp1.width = screenWidth - screenWidth / 4
        holder.rootLL.layoutParams = lp1
    }


    fun updateData(movies: MutableList<Movie>) {
        list.clear()
        list.addAll(movies)
        notifyDataSetChanged()
    }
}