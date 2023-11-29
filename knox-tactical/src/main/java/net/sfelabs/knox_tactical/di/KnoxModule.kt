package net.sfelabs.knox_tactical.di

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
    @TacticalSdk
    @Singleton
    fun provideKnoxCustomDeviceManager() =
        CustomDeviceManager.getInstance()!!
    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxEnterpriseDeviceManager(@ApplicationContext context: Context) =
        EnterpriseDeviceManager.getInstance(context)!!

    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxSettingsManager(): SettingsManager =
        CustomDeviceManager.getInstance().settingsManager!!


    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxSystemManager(): SystemManager =
        CustomDeviceManager.getInstance().systemManager!!

    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxRestrictionPolicy(enterpriseDeviceManager: EnterpriseDeviceManager) =
        enterpriseDeviceManager.restrictionPolicy!!

    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxApplicationPolicy(enterpriseDeviceManager: EnterpriseDeviceManager) =
        enterpriseDeviceManager.applicationPolicy!!

    @Provides
    @TacticalSdk
    @Singleton
    fun provideKnoxHdmManager(enterpriseDeviceManager: EnterpriseDeviceManager) =
        enterpriseDeviceManager.hypervisorDeviceManager!!
}