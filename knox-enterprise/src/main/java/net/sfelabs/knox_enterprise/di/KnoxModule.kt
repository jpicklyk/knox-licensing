package net.sfelabs.knox_enterprise.di

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.EnterpriseKnoxManager
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import com.samsung.android.knox.restriction.RestrictionPolicy
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
    fun provideKnoxEnterpriseLicenseManager(@ApplicationContext context: Context): KnoxEnterpriseLicenseManager =
        KnoxEnterpriseLicenseManager.getInstance(context)

    @Provides
    @Singleton
    fun provideKnoxEnterpriseDeviceManager(@ApplicationContext context: Context): EnterpriseDeviceManager =
        EnterpriseDeviceManager.getInstance(context)

    @Provides
    @Singleton
    fun provideEnterpriseKnoxManager(@ApplicationContext context: Context): EnterpriseKnoxManager =
        EnterpriseKnoxManager.getInstance(context)

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
    fun provideKnoxRestrictionPolicy(@ApplicationContext context: Context): RestrictionPolicy =
        EnterpriseDeviceManager.getInstance(context).restrictionPolicy

    @Provides
    @Singleton
    fun provideAttestationPolicy(@ApplicationContext context: Context): EnhancedAttestationPolicy {
        val enterpriseKnoxManager: EnterpriseKnoxManager =
            EnterpriseKnoxManager.getInstance(context)
        return enterpriseKnoxManager.enhancedAttestationPolicy
    }
}