package apero.quanta.picai.di

import apero.quanta.picai.data.repository.CategoryRepositoryImpl
import apero.quanta.picai.data.repository.GenImageRepositoryImpl
import apero.quanta.picai.data.repository.HistoryRepositoryImpl
import apero.quanta.picai.domain.repository.CategoryRepository
import apero.quanta.picai.domain.repository.GenImageRepository
import apero.quanta.picai.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun provideAIGenerationRepository(
        genImageRepositoryImpl: GenImageRepositoryImpl,
    ): GenImageRepository

}
