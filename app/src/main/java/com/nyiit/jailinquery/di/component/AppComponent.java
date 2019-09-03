package com.nyiit.jailinquery.di.component;

import android.app.Application;

import com.nyiit.jailinquery.JailinqueryApplication;
import com.nyiit.jailinquery.di.module.AppModule;
import com.nyiit.jailinquery.di.module.JailInqueryActivityModule;
import com.nyiit.jailinquery.di.module.JailInqueryServiceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Dagger2 Component入口
 */

@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        JailInqueryActivityModule.class,
        JailInqueryServiceModule.class,
})

public interface AppComponent {
    void inject(JailinqueryApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        //@BindsInstance
        //Builder service(FaceRecoService service);
        AppComponent build();
    }
}
