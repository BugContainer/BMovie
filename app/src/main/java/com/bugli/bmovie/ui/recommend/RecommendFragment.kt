package com.bugli.bmovie.ui.recommend

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.ui.MainViewModel
import com.bugli.bmovie.util.InjectorUtil
import kotlinx.android.synthetic.main.center_recommend.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecommendFragment(bm: Bitmap) : Fragment() {
    var bitmap: Bitmap = bm
    lateinit var viewModel: MainViewModel
    private var myAdapter: RVMyAdapter? = null
    private lateinit var recyclerView: RecyclerView
    var mList: MutableList<Movie> = ArrayList()
    var mData: MutableList<Movie> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.center_recommend, container, false)
    }


    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            InjectorUtil.getMainModelFactory()
        ).get(MainViewModel::class.java)

        recyclerView = center_rv
        recyclerView.background = BitmapDrawable(resources, bitmap)
        recyclerView.layoutManager = CardLayoutManager()

        myAdapter = RVMyAdapter(context!!)
        recyclerView.adapter = myAdapter

        GlobalScope.launch {
            val job =
                launch { mList = viewModel.getRecommendMovies() }
            job.join()
            val job1 = launch {
                for (e in mList) {
                    mData = viewModel.getMovieIssues(e.playUrl)
                }
            }
            job1.join()
            val msg = Message()
            msg.what = 0
            mHandler.sendMessage(msg)
        }
    }

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                0 -> {
                    myAdapter!!.updateData(mData.subList(0, 12))
                    val itemTouchHelperCallback =
                        ItemTouchHelperCallback(mData.subList(0, 12), myAdapter!!)
                    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                    itemTouchHelper.attachToRecyclerView(recyclerView)
                }
            }

        }
    }
}