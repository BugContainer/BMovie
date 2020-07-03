package com.bugli.bmovie.ui.play

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import kotlinx.android.synthetic.main.item_play_download.view.*

class DownloadRVAdapter(context: Context, list: MutableList<String>) :
    RecyclerView.Adapter<DownloadRVAdapter.MyViewHolder>() {
    private val downloadList = list
    private val mContext = context


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv: TextView = itemView.play_download_tv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_play_download, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv.text = "第${position + 1}集"
        holder.tv.setOnClickListener {
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