package com.nyiit.jailinquery.di.module;

import com.nyiit.jailinquery.JailInqueryActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Dagger2 Activity Module
 */

@Module
public abstract class JailInqueryActivityModule {
    @ContributesAndroidInjector
    public abstract JailInqueryActivity contributesJailInqueryActivity();
}
