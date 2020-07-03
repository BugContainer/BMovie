package com.bugli.bmovie.ui

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
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultsAdapter(context: Context, movies: MutableList<Movie>) :
    RecyclerView.Adapter<SearchResultsAdapter.MyViewHolder>() {
    var mContext: Context = context
    var list: List<Movie> = movies


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.result_tv
        val ll: LinearLayout = itemView.result_ll

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_search_result, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return if (list.size > 25) {
            25
        } else {
            list.size

        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = list[position].movieName
        val screenHeight = GetPoint.getWinWH(mContext).y
        val screenWidth = GetPoint.getWinWH(mContext).x
        val lp1: RecyclerView.LayoutParams =
            holder.ll.layoutParams as RecyclerView.LayoutParams
        //topView 和 BottomView 占了 1/4 ，所以只剩下 3/4
        lp1.height = screenHeight / 15
        lp1.width = screenWidth * 3 / 4
        holder.ll.layoutParams = lp1
        holder.setIsRecyclable(false)

//        holder.ll.gravity
        holder.ll.setOnClickListener {
            getListener!!.onClick(position)
        }
    }


    interface GetListener {
        fun onClick(position: Int)
    }

    private var getListener: GetListener? = null

    fun getGetListener(): GetListener? {
        return getListener
    }

    fun setGetListener(getListener: GetListener?) {
        this.getListener = getListener
    }
}