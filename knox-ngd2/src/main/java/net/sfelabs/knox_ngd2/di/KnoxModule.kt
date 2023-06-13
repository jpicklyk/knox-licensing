package net.sfelabs.knox_ngd2.di

import android.content.Context
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
object KnoxModule {

    @Provides
    @Ngd2Sdk
    @Singleton
    fun provideKnoxCustomDeviceManager() =
        CustomDeviceManager.getInstance()!!
    @Provides
    @Ngd2Sdk
    @Singleton
    fun provideKnoxEnterpriseDeviceManager(@ApplicationContext context: Context) =
        EnterpriseDeviceManager.getInstance(context)!!

    @Provides
    @Ngd2Sdk
    @Singleton
    fun provideKnoxSettingsManager(): SettingsManager =
        CustomDeviceManager.getInstance().settingsManager!!


    @Provides
    @Ngd2Sdk
    @Singleton
    fun provideKnoxSystemManager(): SystemManager =
        CustomDeviceManager.getInstance().systemManager!!

    @Provides
    @Ngd2Sdk
    @Singleton
    fun provideKnoxRestrictionPolicy(enterpriseDeviceManager: EnterpriseDeviceManager) =
        enterpriseDeviceManager.restrictionPolicy!!
}