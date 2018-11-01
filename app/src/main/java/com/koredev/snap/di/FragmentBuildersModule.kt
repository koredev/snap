package com.koredev.snap.di

import com.koredev.snap.ui.snap.SnapFragment
import com.koredev.snap.ui.snaps.SnapsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeSnapFragment(): SnapFragment

    @ContributesAndroidInjector
    abstract fun contributeSnapsFragment(): SnapsFragment
}