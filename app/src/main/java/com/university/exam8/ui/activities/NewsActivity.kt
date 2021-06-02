package com.university.exam8.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.realtyfeed.android.ui.tools.dataLoader.ApiMethod
import com.university.exam8.ui.interfaces.FutureCallBack
import com.university.exam8.R
import com.university.exam8.bean.NewsModel
import com.university.exam8.ui.adapters.NewsRecyclerViewAdapter
import com.university.exam8.ui.dataLoader.DataLoader
import com.university.exam8.ui.interfaces.OnLoadMoreListener
import kotlinx.android.synthetic.main.activity_news.*
//import kotlin.jemala.zauri.bla

class NewsActivity : AppCompatActivity() {

    private lateinit var newsRecyclerViewAdapter: NewsRecyclerViewAdapter
    private val news = mutableListOf<NewsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        init()
    }

    private fun init() {
        loadNews("")
        newsRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newsRecyclerViewAdapter =
            NewsRecyclerViewAdapter(
                newsRecyclerView,
                news
            )
        newsRecyclerView.adapter = newsRecyclerViewAdapter
        newsRecyclerViewAdapter.setOnLoadMoreListener(loadMoreListener)

    }

    private val loadMoreListener = object :
        OnLoadMoreListener {
        override fun onLoadMore() {
            if (news.size != 0) {
                if (!news[news.size - 1].isLast) {
                    newsRecyclerView.post {
                        val newsModel = NewsModel()
                        newsModel.isLast = true
                        news.add(newsModel)
                        newsRecyclerViewAdapter.notifyItemInserted(news.size - 1)
                        loadNews(news[news.size - 1].id.toString())
                    }
                }
            }
        }
    }

    private fun loadNews(lastNewsId: String) {
        val parameters = mutableMapOf<String, String>()
        if (lastNewsId.isNotEmpty())
            parameters["lastId"] = lastNewsId
        DataLoader.getRequest(
            progressBar,
            ApiMethod.news,
            parameters,
            object : FutureCallBack<String> {
                override fun done(result: String) {
                    progressBar.visibility = View.GONE
                    val lastPosition = news.size
                    if (lastPosition > 0) {
                        news.removeAt(news.size - 1)
                        newsRecyclerViewAdapter.notifyItemRemoved(news.size)
                    }
                    newsRecyclerViewAdapter.setLoaded()
                    news.addAll(Gson().fromJson(result, Array<NewsModel>::class.java).toList())
                    if (news.isNotEmpty() && news.size != 1) newsRecyclerViewAdapter.notifyItemMoved(
                        lastPosition,
                        news.size - 1
                    )
                    else newsRecyclerViewAdapter.notifyDataSetChanged()
                }

                override fun error(title: String, errorMessage: String) {
                    progressBar.visibility = View.GONE
                }

            }

        )
    }
}
