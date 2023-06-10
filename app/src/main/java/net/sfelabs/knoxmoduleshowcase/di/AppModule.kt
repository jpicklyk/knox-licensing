package net.sfelabs.knoxmoduleshowcase.di

import android.content.Context
import android.net.ConnectivityManager
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

}