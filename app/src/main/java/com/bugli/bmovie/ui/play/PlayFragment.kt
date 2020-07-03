package com.bugli.bmovie.ui.play

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugli.bmovie.R
import com.bugli.bmovie.data.model.Movie
import com.bugli.bmovie.ui.MainActivity
import com.bugli.bmovie.util.GetPoint
import com.bugli.bmovie.util.InjectorUtil
import com.bugli.bmovie.util.TimeUtil
import com.bugli.bmovie.util.permission.MPermission
import com.bugli.bmovie.util.permission.MPermissionListener
import com.bugli.bmovie.util.permission.MPermissionUtils
import com.bugli.bmovie.widget.HorizontalProgressBar
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_play.*
import kotlinx.android.synthetic.main.play_controller.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.Serializable


class PlayFragment : Fragment(), View.OnClickListener, Serializable {
    private lateinit var mMovie: Movie
    private lateinit var playView: PlayerView
    private var player: SimpleExoPlayer? = null
    private lateinit var mediaSource: MediaSource
    private lateinit var viewModel: PlayViewModel
    private lateinit var allList: MutableList<String>
    private lateinit var playRV: RecyclerView
    private lateinit var downloadRV: RecyclerView
    private lateinit var playRVAdapter: PlayRVAdapter
    private lateinit var downloadRVAdapter: DownloadRVAdapter
    private lateinit var controllerLL: RelativeLayout
    private lateinit var playOrPause: AppCompatImageView
    private lateinit var lastView: AppCompatImageView
    private lateinit var nextView: AppCompatImageView
    private lateinit var resizeView: AppCompatImageView
    private lateinit var rootLayout: RelativeLayout

    private lateinit var playLayout: RelativeLayout
    private lateinit var listLL: LinearLayout

    private lateinit var currentTimeView: TextView
    private lateinit var videoTimeView: TextView
    private lateinit var progressBar: HorizontalProgressBar
    private lateinit var mContext: Context
    private var isPortrait = true
    private var cVolume = 0.8f
    private var isEndPlay = false
    private var lastProgress = 0
    private var lastVolume = 0
    private var currentVideo = 1
    //上半部分控制

    private lateinit var topController: LinearLayout
    private lateinit var adjustLL: LinearLayout
    private lateinit var speedLL: LinearLayout
    private lateinit var backView: AppCompatImageView
    private lateinit var volumeView: AppCompatImageView
    private lateinit var speedView: TextView
    private lateinit var playName: TextView
    private lateinit var speedTV1: TextView
    private lateinit var speedTV2: TextView
    private lateinit var speedTV3: TextView
    private lateinit var speedTV4: TextView
    private lateinit var volumeWeightView: HorizontalProgressBar

    private var videoChanged = false

    private var m3u8List: MutableList<String> = ArrayList()
    private var downloadList: MutableList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    Log.e("bugli", "-===$cVolume")
                    //加载播放和下载列表  第二个参数为一行数量
                    playRV.layoutManager = GridLayoutManager(mContext, 4)
                    playRVAdapter = PlayRVAdapter(mContext, m3u8List)
                    playRV.adapter = playRVAdapter

                    downloadRV.layoutManager = GridLayoutManager(mContext, 4)
                    downloadRVAdapter = DownloadRVAdapter(mContext, downloadList)
                    downloadRV.adapter = downloadRVAdapter

                    initRVClick()
                    releasePlayer()
                    initPlay(m3u8List[currentVideo - 1])
                }

                1 -> {
                    speedView.text = msg.obj.toString()
                    if (player != null) {
                        player!!.playbackParameters =
                            PlaybackParameters(msg.obj.toString().toFloat(), 1f)
                        adjustLL.visibility = View.GONE
                        speedLL.visibility = View.GONE
                    }
                }
                2 -> {
                    if (player != null) {
                        player!!.playWhenReady = !player!!.playWhenReady
                        player!!.playbackState
                        //切换背景
                        if (player!!.playWhenReady) {
                            playOrPause.background =
                                ResourcesCompat.getDrawable(mContext.resources, R.color.pause, null)
                        } else {
                            playOrPause.background =
                                ResourcesCompat.getDrawable(mContext.resources, R.color.play, null)
                        }
                    }
                }
                3 -> {
                    if (activity != null && !isPortrait) {
                        (activity!! as MainActivity).hideStatusAndNav()
                    }
                    //如果控制栏不在可见则设置隐藏
                    if (!playView.isControllerVisible) {
                        speedLL.visibility = View.GONE
                        volumeWeightView.visibility = View.GONE
                    }
                    //播放中
                    //更新时间
                    //切换了video
                    if (videoChanged) {
                        videoChanged = false
                        progressBar.setMax(player!!.duration.toInt())
                        videoTimeView.text = TimeUtil.formatTime(player!!.duration)
                    }
                    //翻转或者第一次初始化
                    if (progressBar.getMax() == -1000) {
                        progressBar.setMax(player!!.duration.toInt())
                        videoTimeView.text = TimeUtil.formatTime(player!!.duration)
                        //hui复播放进度
                        if (lastProgress != 0) {
                            progressBar.setProgress(lastProgress)
                            player!!.seekTo(lastProgress.toLong())
                            currentTimeView.text =
                                TimeUtil.formatTime(lastProgress.toLong())
                        }
                    }
                    //手动切换了进度
                    if (lastProgress != progressBar.getProgress() || isEndPlay) {
                        isEndPlay = false
                        player!!.seekTo(progressBar.getProgress().toLong())
                        currentTimeView.text =
                            TimeUtil.formatTime(progressBar.getProgress().toLong())
                        lastProgress = progressBar.getProgress()
                    } else {
                        //正常更新时间和进度
                        progressBar.setProgress(player!!.contentPosition.toInt())
                        currentTimeView.text = TimeUtil.formatTime(player!!.currentPosition)
                        lastProgress = progressBar.getProgress()
                    }

                    //更新声音
                    if (lastVolume != volumeWeightView.getProgress()) {
                        player!!.volume = volumeWeightView.getProgress() / 100f
                        cVolume = player!!.volume
                        lastVolume = volumeWeightView.getProgress()
                        if (player!!.volume == 0f) {
                            volumeView.background = ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.volume_disable,
                                null
                            )
                        } else {
                            volumeView.background = ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.volume,
                                null
                            )
                        }
                    }
                }

                4 -> {
                    //重置
                    isEndPlay = true
                    progressBar.setProgress(0)
                    player!!.playWhenReady = false
                    playOrPause.background =

                        ResourcesCompat.getDrawable(resources, R.color.play, null)
                }
                5 -> {
                    if (speedLL.visibility == View.GONE) {
                        adjustLL.visibility = View.VISIBLE
                        speedLL.visibility = View.VISIBLE
                        volumeWeightView.visibility = View.GONE
                    } else {
                        adjustLL.visibility = View.GONE
                        speedLL.visibility = View.GONE
                        volumeWeightView.visibility = View.GONE
                    }
                }

                6 -> {
                    if (volumeWeightView.visibility == View.GONE) {
                        adjustLL.visibility = View.VISIBLE
                        speedLL.visibility = View.GONE
                        volumeWeightView.visibility = View.VISIBLE
                        //设置大小
                        volumeWeightView.setProgress((player!!.volume * volumeWeightView.getMax()).toInt())
                    } else {
                        adjustLL.visibility = View.GONE
                        speedLL.visibility = View.GONE
                        volumeWeightView.visibility = View.GONE
                    }
                }

            }
        }
    }

    private fun adjustView() {
        //调整playView
        val lp = playLayout.layoutParams as RelativeLayout.LayoutParams
        //调整为 16 ： 9
        lp.height = GetPoint.getWinWH(mContext).x * 9 / 16
        playLayout.layoutParams = lp

        //

    }


    private fun initRVClick() {
        playRVAdapter.setGetListener(object : PlayRVAdapter.GetListener {
            override fun onClick(position: Int) {
                currentVideo = position + 1
                //创建新的player之前先释放之前的Player
                releasePlayer()
                Toast.makeText(mContext, m3u8List[position] + "开始播放", Toast.LENGTH_SHORT).show()
                videoChanged = true
                initPlay(m3u8List[position])
            }

        })

        downloadRVAdapter.setGetListener(object : DownloadRVAdapter.GetListener {
            override fun onClick(position: Int) {
                /////
                if (MPermissionUtils.needRequestPermission() && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { //判断是否需要动态申请权限
                    MPermission.with(activity!!)
                        .requestCode(100)
                        .permission(
                            arrayOf(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        ) //需要申请的权限(支持不定长参数)
                        .callBack(object : MPermissionListener {
                            override fun onPermit(
                                requestCode: Int,
                                vararg permission: String?
                            ) { //允许权限的回调
                                Log.e("bugli", "onPermit")
                                downloadVideo(downloadList[position]) //处理具体下载过程
                            }

                            override fun onCancel(
                                requestCode: Int,
                                vararg permission: String?
                            ) { //拒绝权限的回调
                                Log.e("bugli", "onCancel")
                                MPermissionUtils.goSetting(mContext) //跳转至当前app的权限设置界面
                            }
                        })
                        .send()
                } else {
                    downloadVideo(downloadList[position]) //处理具体下载过程
                }


            }
        })

    }


    //处理下载
    fun downloadVideo(url: String) {
        Toast.makeText(mContext, "$url+开始下载", Toast.LENGTH_SHORT).show()
        Log.e("bugli", "启动下载$url")
        viewModel.downloadVideo(url)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            lastProgress = savedInstanceState.getInt("lastProgress")
            currentVideo = savedInstanceState.getInt("currentVideo")
            cVolume = savedInstanceState.getFloat("cVolume")
        }
        mMovie = (activity!! as MainActivity).getMMovie()
        isPortrait =
            (activity!! as MainActivity).requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //初始化viewModel
//        viewModel = ViewModelProvider(this).get(PlayViewModel::class.java)
        //
        mContext = context!!
        viewModel = ViewModelProvider(
            this,
            InjectorUtil.getPlayModelFactory()
        ).get(PlayViewModel::class.java)
        initView()
        //获取播放链接和下载链接
        GlobalScope.launch {
            val job = launch {
                allList = viewModel.getPlayList(mMovie.playUrl)
            }
            job.join()
            //分割播放链接和下载链接
            var f = 0
            for (e in allList) {
                if (e == "download") {
                    f = 1
                    continue
                }
                if (f == 0) {
                    m3u8List.add(e)
                } else {
                    downloadList.add(e)
                }
            }
            val msg = Message()
            msg.what = 0
            mHandler.sendMessage(msg)
            viewModel.saveVideo(m3u8List,downloadList,mMovie.movieCode)
        }
        initClick()
    }

    private fun initView() {
        rootLayout = root_play
        playLayout = play_view
        playView = pv
        playRV = play_list_rv
        downloadRV = download_list_rv
        controllerLL = controller
        lastView = last_video
        nextView = next_video
        playOrPause = play_or_pause
        resizeView = resize

        currentTimeView = current_time
        videoTimeView = video_time
        progressBar = video_progress
        listLL = list_ll
        //controller
        topController = top_controller
        adjustLL = adjust_ll
        speedLL = speed_ll
        backView = back
        volumeView = volume
        speedView = play_speed
        playName = playing_name
        speedTV1 = speed_1
        speedTV2 = speed_2
        speedTV3 = speed_3
        speedTV4 = speed_4
        volumeWeightView = volume_weight
        volumeWeightView.setMax(100)
        volumeWeightView.setProgress((100 * cVolume).toInt())
        lastVolume = (100 * cVolume).toInt()
        if (cVolume == 0f) {
            volumeView.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.volume_disable,
                null
            )
        }
        //横屏只显示播放View
        if (!isPortrait) {
            listLL.visibility = View.GONE
            //居中并设置背景为黑色
            rootLayout.gravity = Gravity.CENTER
            rootLayout.setBackgroundColor(Color.BLACK)

            val lp = playLayout.layoutParams as RelativeLayout.LayoutParams
            lp.width = GetPoint.getWinWH(mContext).x
            lp.height = GetPoint.getWinWH(mContext).y - GetPoint.getStatusBarHeight(mContext)
            playLayout.layoutParams = lp
        } else {
            adjustView()
        }


    }

    private fun initClick() {
        lastView.setOnClickListener(this)
        nextView.setOnClickListener(this)
        playOrPause.setOnClickListener(this)
        resizeView.setOnClickListener(this)

        backView.setOnClickListener(this)
        volumeView.setOnClickListener(this)
        speedView.setOnClickListener(this)

        speedTV1.setOnClickListener(this)
        speedTV2.setOnClickListener(this)
        speedTV3.setOnClickListener(this)
        speedTV4.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initPlay(path: String) {
        Toast.makeText(mContext, "开始播放第${currentVideo}集", Toast.LENGTH_SHORT).show()
        playOrPause.background =
            ResourcesCompat.getDrawable(mContext.resources, R.color.pause, null)
        val bandwidthMeter = DefaultBandwidthMeter()
        val userAgent =
            Util.getUserAgent(mContext, "ExoPlayerDemo")
        val mediaDataSourceFactory = DefaultDataSourceFactory(
            mContext, bandwidthMeter,
            DefaultHttpDataSourceFactory(userAgent, bandwidthMeter)
        )
        val uri = Uri.parse(path)
        if (path.endsWith(".m3u8")) {
            mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri)
        }
        val trackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)
        val renderersFactory =
            DefaultRenderersFactory(
                mContext,
                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
            )
        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector)
        player!!.addListener(object : Player.EventListener {
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onSeekProcessed() {
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                Log.e("bugli", "PlayError…………………………")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onTimelineChanged(
                timeline: Timeline?,
                manifest: Any?,
                reason: Int
            ) {
            }

            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) { //playbackState 2 准备  3 播放中  4 结束
                if (playbackState == 4) {
                    //结束
                    Log.e("bugli", "playEnd")
                    val msg = Message()
                    msg.what = 4
                    mHandler.sendMessage(msg)
                }
            }

        })
//        player!!.playbackParameters = PlaybackParameters(0f, 1f) //播放速度
        player!!.prepare(mediaSource)
        playView.player = player
        player!!.volume = cVolume //0f-1f
        player!!.playWhenReady = true

        GlobalScope.launch {
            //获取视频时长
            while (true) {
                delay(1000)
                if (player!!.duration > 0) {
                    val msg = Message()
                    msg.what = 3
                    mHandler.sendMessage(msg)
                }
            }
        }
        //重新设置名称
        playName.text = "第" + currentVideo + "集" + "  " + mMovie.movieName
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.play_or_pause -> {
                val msg = Message()
                msg.what = 2
                mHandler.sendMessage(msg)
            }

            R.id.last_video -> {
                if (currentVideo > 1) {
                    currentVideo--
                    releasePlayer()
                    videoChanged = true
                    initPlay(m3u8List[currentVideo - 1])
                }
            }
            R.id.next_video -> {
                if (currentVideo < m3u8List.size) {
                    currentVideo++
                    releasePlayer()
                    videoChanged = true
                    initPlay(m3u8List[currentVideo - 1])
                }
            }
            R.id.resize -> {
                Toast.makeText(mContext, "全屏", Toast.LENGTH_SHORT).show()
                isPortrait = if (isPortrait) {
                    (activity!! as MainActivity).setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    false
                } else {
                    (activity!! as MainActivity).setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    true
                }
            }
            R.id.back -> {
                //返回 重置 MainActivity 的currentFragment
                if (!isPortrait) {
                    (activity!! as MainActivity).setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                    isPortrait = true
                } else {
                    //返回
                    (activity!! as MainActivity).backFragment()
                }
            }
            R.id.volume -> {
                //显示声音调节进度条
                val msg = Message()
                msg.what = 6
                mHandler.sendMessage(msg)
            }
            R.id.play_speed -> {
                //显示播放速度
                val msg = Message()
                msg.what = 5
                mHandler.sendMessage(msg)
            }
            R.id.speed_1 -> {
                //0.5
                val msg = Message()
                msg.what = 1
                msg.obj = 0.5
                mHandler.sendMessage(msg)
            }
            R.id.speed_2 -> {
                //1.0
                val msg = Message()
                msg.what = 1
                msg.obj = 1.0
                mHandler.sendMessage(msg)
            }
            R.id.speed_3 -> {
                //1.5
                val msg = Message()
                msg.what = 1
                msg.obj = 1.5
                mHandler.sendMessage(msg)
            }
            R.id.speed_4 -> {
                //2.0
                val msg = Message()
                msg.what = 1
                msg.obj = 2.0
                mHandler.sendMessage(msg)
            }
        }
    }

    override fun onPause() {
        //暂停  可设置Resume中恢复
        if (player != null && player!!.playWhenReady) {
            val msg = Message()
            msg.what = 2
            mHandler.sendMessage(msg)
        }
        super.onPause()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("lastProgress", player!!.currentPosition.toInt())
        super.onSaveInstanceState(outState)
        outState.putInt("currentVideo", currentVideo)
        outState.putFloat("cVolume", cVolume)
        Log.e("bugli", "playFragment==onSaveInstanceState")
    }

    override fun onDestroy() {
        Log.e("bugli", "playFragmentDestroy")
        releasePlayer()
        super.onDestroy()

    }

    fun releasePlayer() {
        if (player != null) {
            player!!.release()
        }
    }
}