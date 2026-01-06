package apero.quanta.picai.di

import android.content.Context
import androidx.room.Room
import apero.quanta.picai.data.local.room.PicAIDatabase
import apero.quanta.picai.data.local.room.dao.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PicAIDatabase {
        return Room.databaseBuilder(
            context,
            PicAIDatabase::class.java,
            "picai_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: PicAIDatabase): HistoryDao {
        return database.historyDao()
    }
}