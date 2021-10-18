package com.example.rssfeedpractice

import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var myRv: RecyclerView
    lateinit var rvAdapter: RVAdapter
    lateinit var listView: ListView
    var feeds = mutableListOf<Feeds>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        myRv = findViewById(R.id.rvFeeds)
        FetchFeeds().execute()
    }

    private inner class FetchFeeds : AsyncTask<Void, Void, MutableList<Feeds>>() {
        val parser = XMLParser()
        override fun doInBackground(vararg params: Void?): MutableList<Feeds> {
            val url = URL("https://stackoverflow.com/feeds")
            val urlConnection = url.openConnection() as HttpURLConnection
            feeds =

                urlConnection.getInputStream()?.let {
                    parser.parse(it)
                }
                        as MutableList<Feeds>
            return feeds
        }

        override fun onPostExecute(result: MutableList<Feeds>?) {
            super.onPostExecute(result)
            rvAdapter=RVAdapter(feeds,this@MainActivity)
            myRv.adapter = rvAdapter
            myRv.layoutManager = LinearLayoutManager(applicationContext)
        }

    }

}