package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> mFieldList = new MutableLiveData<>();
    private List<Venue> allVenues = new ArrayList<>(); // Lưu trữ danh sách gốc để lọc
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
                allVenues = venueList; // Lưu lại danh sách gốc khi tải về
                mFieldList.setValue(venueList);
            }

            @Override
            public void onFailure(String error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    // Hàm lọc dữ liệu theo môn thể thao
    public void filterBySport(String sportName) {
        if (sportName == null || sportName.isEmpty() || sportName.equals("Tất cả")) {
            mFieldList.setValue(allVenues);
            return;
        }
        
        List<Venue> filteredList = new ArrayList<>();
        for (Venue v : allVenues) {
            if (v.getSport_name() != null && v.getSport_name().equalsIgnoreCase(sportName)) {
                filteredList.add(v);
            }
        }
        mFieldList.setValue(filteredList);
    }
}
