package com.enping.transformers

import androidx.room.Room
import com.enping.transformers.data.TransformerRepo
import com.enping.transformers.data.TransformerRepoImpl
import com.enping.transformers.data.source.local.LocalDataSource
import com.enping.transformers.data.source.local.LocalDataSourceImpl
import com.enping.transformers.data.source.local.TransformerDatabase
import com.enping.transformers.data.source.remote.RemoteDataSource
import com.enping.transformers.data.source.remote.RemoteDataSourceImpl
import com.enping.transformers.ui.MainViewModel
import com.enping.transformers.ui.list.TransformerEditViewModel
import okhttp3.HttpUrl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<HttpUrl> { HttpUrl.parse("https://transformers-api.firebaseapp.com/")!! }

    single {
        Room.databaseBuilder(get(), TransformerDatabase::class.java, "transformer-db").build()
    }

    single<LocalDataSource> { LocalDataSourceImpl(get()) }

    single<RemoteDataSource> { RemoteDataSourceImpl(get()) }

    single<TransformerRepo> { TransformerRepoImpl(get(), get()) }
}

val vmModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { TransformerEditViewModel(get()) }
}