package net.sfelabs.knox_common.di

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
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
    @Singleton
    fun provideKnoxEnterpriseLicenseManager(@ApplicationContext context: Context) =
        KnoxEnterpriseLicenseManager.getInstance(context)

    @Provides
    @Singleton
    fun provideKnoxEnterpriseDeviceManager(@ApplicationContext context: Context) =
        EnterpriseDeviceManager.getInstance(context)

    @Provides
    @Singleton
    fun provideKnoxSettingsManager(): SettingsManager =
        CustomDeviceManager.getInstance().settingsManager


    @Provides
    @Singleton
    fun provideKnoxSystemManager(): SystemManager =
        CustomDeviceManager.getInstance().systemManager

    @Provides
    @Singleton
    fun provideKnoxRestrictionPolicy(enterpriseDeviceManager: EnterpriseDeviceManager) =
        enterpriseDeviceManager.restrictionPolicy
}