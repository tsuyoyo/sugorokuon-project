package tsuyogoro.sugorokuon.recommend.database

import android.arch.persistence.room.Database

@Database(entities = [RecommendKeyword::class], version = 1)
abstract class RecommendKeywordsDatabase {
    abstract fun recommendKeywordsDao(): RecommendKeywordsDao
}