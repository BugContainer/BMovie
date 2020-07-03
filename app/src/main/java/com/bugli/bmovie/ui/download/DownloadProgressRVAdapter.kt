package com.bugli.bmovie.ui.download

import android.content.Context
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Video
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.widget.HorizontalProgressBar


class DownloadProgressRVAdapter(context: Context) :
    RecyclerView.Adapter<DownloadProgressRVAdapter.MyViewHolder>() {
    private val mContext = context
    private var longClicked = false
    private var map: LinkedHashMap<String, Int> = LinkedHashMap()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var downloadName = itemView.findViewById<TextView>(R.id.item_name)
        var downloadController = itemView.findViewById<ImageView>(R.id.download_controller)
        var downloadDeleted = itemView.findViewById<ImageView>(R.id.download_delete)
        var checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)
        var progressBar = itemView.findViewById<HorizontalProgressBar>(R.id.download_progressbar)
        var rootLL = itemView.findViewById<LinearLayout>(R.id.root_download_ll)
        var controllerLL = itemView.findViewById<LinearLayout>(R.id.download_controller_ll)

        init {
            progressBar.setEnableCircle(false)
            progressBar.setEnableTouch(false)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_download_progress, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return map.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        if (map.isNotEmpty()) {
            val s = map.keys
            val name = s.elementAt(position)
            holder.downloadName.text = name
            holder.progressBar.setProgress(map[name]!!)
            if (map[name] == holder.progressBar.getMax()) {
                //下载完成 隐藏 下载控制按钮
                holder.downloadController.visibility = View.INVISIBLE
                holder.progressBar.setProgressColor(
                    ResourcesCompat.getColor(
                        mContext.resources,
                        R.color.colorGreen,
                        null
                    )
                )
            } else {
                holder.downloadController.background =
                    ResourcesCompat.getDrawable(mContext.resources, R.color.pause, null)
            }

            holder.rootLL.setOnLongClickListener {
                longClicked = !longClicked
                notifyDataSetChanged()
                Log.e("bugli", "long")
                true
            }
            if (longClicked) {
                holder.controllerLL.visibility = View.GONE
                holder.checkBox.visibility = View.VISIBLE
                holder.checkBox.isChecked = false
                // 设置背景颜色变暗
                val window: Window = GetPoint.findActivity(mContext)!!.window
                val lp: WindowManager.LayoutParams = window.attributes
                lp.alpha = 0.6f //调节透明度
                window.attributes = lp
            } else {
                holder.controllerLL.visibility = View.VISIBLE
                holder.checkBox.visibility = View.GONE
                // 恢复背景
                val window: Window = GetPoint.findActivity(mContext)!!.window
                val lp: WindowManager.LayoutParams = window.attributes
                lp.alpha = 1f //调节透明度
                window.attributes = lp
            }

        }

    }

    //更新下载进度
    fun updateDownloadProgress(list: MutableList<Video>) {
        for (e in list) {
            if (e.cacheProgress != -1) {
                map[e.videoName] = e.cacheProgress
            }
        }
        Log.e("bugli", "mapSize === ${map.size}")
        notifyDataSetChanged()
    }

}