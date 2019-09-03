package com.nyiit.jailinquery.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.nyiit.jailinquery.tools.ConfigRespository;

import java.util.Map;

public class FaceRecoViewModel extends AndroidViewModel {

    private final LiveData<Map<String, ?>> mObservableConfigs;

    // public ObservableField<FaceData> faces = new ObservableField<>();

    public FaceRecoViewModel(@NonNull Application application, ConfigRespository configRespository) {
        super(application);
        //mProductId = productId;

        //mObservableComments = repository.loadComments(mProductId);
        //mObservableProduct = repository.loadProduct(mProductId);
        mObservableConfigs = configRespository.getConfig();
    }

    public LiveData<Map<String, ?>> getmObservableConfigs() {
        return mObservableConfigs;
    }

    /**
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        //private final int mProductId;

        private ConfigRespository configRespository;

        public Factory(@NonNull Application application, @NonNull ConfigRespository configRespository) {
            mApplication = application;
            //mProductId = productId;
            this.configRespository = configRespository;
            //mRepository = ((FaceRecoApplication) application).getRepository();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new FaceRecoViewModel(mApplication, configRespository);
        }
    }
}
