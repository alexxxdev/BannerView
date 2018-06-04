package com.github.alexxxdev.bannerview

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.alexxxdev.bannerview.demo.R

class MainActivity : AppCompatActivity() {

    private val imgs = listOf<ImageInfo>(
            ImageInfo("http://img.fliptab.io/nature/1920x1200/1877105.jpg"),
            ImageInfo("http://img.fliptab.io/nature/1920x1200/1384659.jpg"),
            ImageInfo("http://img.fliptab.io/nature/1920x1200/45196695.jpg"),
            ImageInfo("http://img.fliptab.io/nature/1920x1200/108931846.jpg"),
            ImageInfo("http://img.fliptab.io/nature/1920x1200/14113966.jpg"),
            ImageInfo("http://img.fliptab.io/nature/1920x1200/95695769.jpg")
    )

    class ImageInfo(val url: String) : ItemInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bannerView = findViewById<BannerView>(R.id.bannerView)

        val adapter = Adapter(this, imgs)
        bannerView.setAdapter(adapter)

        bannerView.setOnSelectedBannerListener(object : OnSelectedBannerListener{
            override fun onSelectedBanner(view: View, position: Int) {
                Log.d("BannerLayoutManager", "bannerView " + position)
            }
        })

    }

    class Adapter(val context: Context, imgs: List<ImageInfo>) : InfiniteAdapter<Adapter.ViewHolder, ImageInfo>(imgs) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
            return ViewHolder(view)
        }

        override fun onBind(holder: ViewHolder, item: ImageInfo, position: Int) {
            Glide.with(context)
                    .load(item.url)
                    .apply(RequestOptions().centerCrop())
                    .into(holder.img)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img: ImageView = itemView as ImageView
        }
    }
}
