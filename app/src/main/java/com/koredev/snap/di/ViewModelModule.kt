package com.koredev.snap.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.koredev.snap.ui.snap.SnapViewModel
import com.koredev.snap.ui.snaps.SnapsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SnapViewModel::class)
    abstract fun bindSnapViewModel(snapViewModel: SnapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SnapsViewModel::class)
    abstract fun bindSnapsViewModel(snapsViewModel: SnapsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: SnapViewModelFactory): ViewModelProvider.Factory
}