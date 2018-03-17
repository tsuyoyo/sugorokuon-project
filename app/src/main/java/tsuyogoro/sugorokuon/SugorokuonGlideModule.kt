package tsuyogoro.sugorokuon

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class SugorokuonGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        builder?.setDiskCache(InternalCacheDiskCacheFactory(context, 1024 * 1024 * 10))
    }
}