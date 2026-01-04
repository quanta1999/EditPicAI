package apero.quanta.picai.di

import android.content.Context
import apero.quanta.picai.data.remote.service.AuthApiService
import apero.quanta.picai.data.remote.service.AiGenerationApiService
import apero.quanta.picai.data.remote.service.CategoryDatasource
import apero.quanta.picai.data.repository.GenImageRepositoryImpl
import apero.quanta.picai.domain.repository.GenImageRepository
import apero.quanta.picai.network.PicAIAuthRetrofit
import apero.quanta.picai.network.PicAIServiceRetrofit
import apero.quanta.picai.network.SignatureInterceptor
import apero.quanta.picai.network.adapter.ResultCallAdapterFactory
import apero.quanta.picai.network.auth.DeviceInfoProvider
import apero.quanta.picai.network.auth.TokenManager
import apero.quanta.picai.network.interceptor.AuthenticationInterceptor
import apero.quanta.picai.network.interceptor.ErrorHandlingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import apero.quanta.picai.BuildConfig


@Module
@InstallIn(SingletonComponent::class)
object NetWorkDI {
    @Provides
    @Singleton
    fun provideAuthApiService(
        @PicAIAuthRetrofit retrofit: Retrofit,
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideRealCategoryDataSource(
        aiGenerationApiService: AiGenerationApiService,
    ): CategoryDatasource {
        return CategoryDatasource(
            aiGenerationApiService = aiGenerationApiService,
        )
    }

    @Provides
    @Singleton
    fun provideGenerateApiService(
        @PicAIServiceRetrofit retrofit: Retrofit,
    ): AiGenerationApiService {
        return retrofit.create(AiGenerationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAIGenerationRepository(
        genImageRepositoryImpl: GenImageRepositoryImpl,
    ): GenImageRepository {
        return genImageRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideDeviceInfoProvider(
        @ApplicationContext context: Context,
    ): DeviceInfoProvider {
        return DeviceInfoProvider(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context,
    ): TokenManager {
        return TokenManager(context)
    }


    @Provides
    @Singleton
    @PicAIAuthRetrofit
    fun providePixArtAuthRetrofit(
        loggingInterceptor: HttpLoggingInterceptor,
        errorHandlingInterceptor: ErrorHandlingInterceptor,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(errorHandlingInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build(),
            )
            .build()
    }


    @Provides
    @Singleton
    @PicAIServiceRetrofit
    fun providePixArtServiceRetrofit(
        authenticationInterceptor: AuthenticationInterceptor,
        signatureInterceptor: SignatureInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        errorHandlingInterceptor: ErrorHandlingInterceptor,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVICE_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(errorHandlingInterceptor)
                    .addInterceptor(authenticationInterceptor)
                    .addInterceptor(signatureInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build(),
            )
            .build()
    }
}