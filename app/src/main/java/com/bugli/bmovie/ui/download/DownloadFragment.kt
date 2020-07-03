package com.bugli.bmovie.ui.download

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Video
import com.bugli.bmovie.ui.play.PlayViewModel
import com.bugli.bmovie.util.InjectorUtil
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DownloadFragment : Fragment() {
    private var rv: RecyclerView? = null
    var list: MutableList<Video> = ArrayList()
    private var adapter: DownloadProgressRVAdapter? = null
    lateinit var viewModel: PlayViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_download, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            InjectorUtil.getPlayModelFactory()
        ).get(PlayViewModel::class.java)
        //
        rv = download_progress_rv
        rv!!.layoutManager = LinearLayoutManager(context)
        adapter = DownloadProgressRVAdapter(context!!)
        rv!!.adapter = adapter


        GlobalScope.launch {
            //获取视频时长
            while (true) {
                delay(1000)
                updateDownloadProgress()
            }
        }
    }

    //更新下载进度
    private fun updateDownloadProgress() {

        GlobalScope.launch {
            val job = launch {
                list = viewModel.getAllDownloadProgress()
            }
            job.join()
            //获取到下载进度
            if (list.isNotEmpty()) {
                if (adapter != null) {
                    val msg = Message()
                    msg.what = 0
                    mHandler.sendMessage(msg)
                }
            }
        }
    }


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    adapter!!.updateDownloadProgress(list)

                }
            }
        }
    }


}