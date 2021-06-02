package com.university.exam8.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.university.exam8.App
import com.university.exam8.ui.interfaces.OnLoadMoreListener
import com.university.exam8.R
import com.university.exam8.bean.NewsModel
import com.university.exam8.ui.tools.Tools
import kotlinx.android.synthetic.main.item_news_recyclerview_layout.view.*
//import kotlin.boris.jotos.ze.garuchava

class NewsRecyclerViewAdapter(
    private val recyclerView: RecyclerView,
    val news: MutableList<NewsModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_WALL_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }

    private var onLoadMoreListener: OnLoadMoreListener? = null
    private var isLoading = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private val visibleThreshold = 5
    private var screenHeight = 0
    private var newsRecyclerViewAdapter: NewsRecyclerViewAdapter = this

    init {
        screenHeight = Tools.getScreenDimenss().y
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                    if (onLoadMoreListener != null)
                        onLoadMoreListener!!.onLoadMore()
                    isLoading = true
                }
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_WALL_ITEM -> WallPostsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_news_recyclerview_layout, parent, false)
            )
            else -> LoadMorePostsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recyclerview_load_more_layout, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WallPostsViewHolder -> holder.onBind()
        }
    }

    override fun getItemCount() = news.size

    inner class WallPostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var model: NewsModel
        fun onBind() {
            model = news[adapterPosition]
            Glide.with(App.instance.getContext()).load(model.cover).into(itemView.newsCoverImageView)
            itemView.titleTextView.text = model.titleKA
        }
    }

    inner class LoadMorePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            news[position].isLast -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_WALL_ITEM
        }
    }
}