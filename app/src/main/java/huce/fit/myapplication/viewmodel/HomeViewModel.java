package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> mFieldList = new MutableLiveData<>();
    private FieldRepository mFieldRepository;

    public HomeViewModel() {
        mFieldRepository = new FieldRepository();
    }

    public LiveData<List<Venue>> getFields() {
        return mFieldList;
    }

    public void fetchFieldsFromFirebase() {
        mFieldRepository.fetchAllFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mFieldList.setValue(venueList);
            }

            @Override
            public void onFailure(String error) {
                // Đã cập nhật tham số từ Exception thành String để khớp với FieldRepository
            }
        });
    }
}
