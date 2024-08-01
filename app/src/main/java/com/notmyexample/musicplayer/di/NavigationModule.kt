package com.notmyexample.musicplayer.di

import com.notmyexample.musicplayer.presentation.navigator.AppNavigator
import com.notmyexample.musicplayer.presentation.navigator.AppNavigatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
abstract class NavigationModule {

    @ActivityScoped
    @Binds
    abstract fun bindNavigator(impl: AppNavigatorImpl): AppNavigator
}