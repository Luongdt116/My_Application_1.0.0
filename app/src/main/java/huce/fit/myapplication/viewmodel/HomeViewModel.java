package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> mFieldList = new MutableLiveData<>();
    private MutableLiveData<List<Venue>> mPromotedFieldList = new MutableLiveData<>();
    private FieldRepository mFieldRepository;

    public HomeViewModel() {
        mFieldRepository = new FieldRepository();
    }

    public LiveData<List<Venue>> getFields() {
        return mFieldList;
    }

    public LiveData<List<Venue>> getPromotedFields() {
        return mPromotedFieldList;
    }

    public void fetchFieldsFromFirebase() {
        mFieldRepository.fetchAllFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mFieldList.setValue(venueList);
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    public void fetchPromotedFieldsFromFirebase() {
        mFieldRepository.fetchPromotedFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mPromotedFieldList.setValue(venueList);
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }
}
