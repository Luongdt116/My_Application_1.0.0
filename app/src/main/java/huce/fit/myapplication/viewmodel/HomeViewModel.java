package huce.fit.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Venue>> mPaginatedList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowLoadMore = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowClearFilter = new MutableLiveData<>();
    
    private final FieldRepository mFieldRepository;
    private List<Venue> mAllVenues = new ArrayList<>();
    private List<Venue> mCurrentFilteredList = new ArrayList<>();
    
    private final int PAGE_SIZE = 6;
    private int mDisplayedCount = 0;
    private String mCurrentQuery = "";

    public HomeViewModel() {
        mFieldRepository = new FieldRepository();
    }

    public LiveData<List<Venue>> getPaginatedFields() {
        return mPaginatedList;
    }

    public LiveData<Boolean> getShowLoadMore() {
        return mShowLoadMore;
    }

    public LiveData<Boolean> getShowClearFilter() {
        return mShowClearFilter;
    }

    public void fetchFieldsFromFirebase() {
        mFieldRepository.fetchAllFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mAllVenues = venueList;
                applyFilterAndResetPagination();
            }

            @Override
            public void onFailure(String error) {
                // Có thể thêm LiveData xử lý lỗi tại đây
            }
        });
    }

    public void search(String query) {
        mCurrentQuery = query.toLowerCase().trim();
        mShowClearFilter.setValue(!mCurrentQuery.isEmpty());
        applyFilterAndResetPagination();
    }

    private void applyFilterAndResetPagination() {
        List<Venue> filtered = new ArrayList<>();
        if (mCurrentQuery.isEmpty()) {
            filtered = new ArrayList<>(mAllVenues);
        } else {
            for (Venue venue : mAllVenues) {
                String name = venue.getVenue_name().toLowerCase();
                String sport = (venue.getSport_name() != null) ? venue.getSport_name().toLowerCase() : "";
                if (name.contains(mCurrentQuery) || sport.contains(mCurrentQuery)) {
                    filtered.add(venue);
                }
            }
        }
        mCurrentFilteredList = filtered;
        mDisplayedCount = 0;
        mPaginatedList.setValue(new ArrayList<>()); // Clear old list
        loadNextPage();
    }

    public void loadNextPage() {
        int start = mDisplayedCount;
        int end = Math.min(start + PAGE_SIZE, mCurrentFilteredList.size());
        
        List<Venue> currentDisplay = mPaginatedList.getValue();
        if (currentDisplay == null) currentDisplay = new ArrayList<>();
        
        if (start < end) {
            for (int i = start; i < end; i++) {
                currentDisplay.add(mCurrentFilteredList.get(i));
            }
            mDisplayedCount = end;
            mPaginatedList.setValue(new ArrayList<>(currentDisplay));
        }
        
        mShowLoadMore.setValue(mDisplayedCount < mCurrentFilteredList.size());
    }

    public void clearFilter() {
        search("");
    }
}
