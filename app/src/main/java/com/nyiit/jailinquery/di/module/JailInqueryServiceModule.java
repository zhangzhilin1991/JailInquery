package com.nyiit.jailinquery.di.module;

import com.nyiit.jailinquery.service.JailInqueryService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger2 Service Module
 */

@Module
public abstract class JailInqueryServiceModule {
    @ContributesAndroidInjector
    public abstract JailInqueryService contributesJailInqueryService();
}

