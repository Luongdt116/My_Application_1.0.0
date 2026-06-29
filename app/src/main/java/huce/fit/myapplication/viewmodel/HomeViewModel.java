package huce.fit.myapplication.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Venue>> mDisplayList = new MutableLiveData<>();
    private final MutableLiveData<List<Venue>> mPromotedList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowLoadMore = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowClearFilter = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final MutableLiveData<String> mWelcomeMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoggedIn = new MutableLiveData<>();
    
    private final FieldRepository mFieldRepository;
    private List<Venue> mAllVenues = new ArrayList<>(); 
    private List<Venue> mFilteredList = new ArrayList<>();
    
    private final int PAGE_SIZE = 6;
    private int mDisplayedCount = 0;
    private String mCurrentQuery = "";

    public HomeViewModel() {
        mFieldRepository = new FieldRepository();
    }

    public LiveData<List<Venue>> getDisplayList() { return mDisplayList; }
    public LiveData<List<Venue>> getPromotedFields() { return mPromotedList; }
    public LiveData<Boolean> getShowLoadMore() { return mShowLoadMore; }
    public LiveData<Boolean> getShowClearFilter() { return mShowClearFilter; }
    public LiveData<Boolean> getIsLoading() { return mIsLoading; }
    public LiveData<String> getWelcomeMessage() { return mWelcomeMessage; }
    public LiveData<Boolean> getIsLoggedIn() { return mIsLoggedIn; }

    public void checkLoginStatus(Context context) {
        SharedPreferences pref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        boolean loggedIn = pref.getBoolean("isLoggedIn", false);
        mIsLoggedIn.setValue(loggedIn);
        if (loggedIn) {
            mWelcomeMessage.setValue("Chào, " + pref.getString("username", "Người dùng"));
        }
    }

    public void fetchFieldsFromFirebase() {
        if (!mAllVenues.isEmpty()) return;
        refreshFields();
    }

    // Ép buộc tải lại dữ liệu (Dùng cho Swipe Refresh)
    public void refreshFields() {
        mIsLoading.setValue(true);
        mFieldRepository.fetchAllFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mAllVenues = venueList;
                applyFilterAndResetPagination();
                mIsLoading.setValue(false);
            }
            @Override public void onFailure(String error) { mIsLoading.setValue(false); }
        });
    }

    public void fetchPromotedFieldsFromFirebase() {
        refreshPromotedFields();
    }

    // Ép buộc tải lại khuyến mãi
    public void refreshPromotedFields() {
        mIsLoading.setValue(true);
        mFieldRepository.fetchPromotedFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mPromotedList.setValue(venueList);
                mIsLoading.setValue(false);
            }
            @Override public void onFailure(String error) { mIsLoading.setValue(false); }
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
        mFilteredList = filtered;
        mDisplayedCount = 0;
        mDisplayList.setValue(new ArrayList<>()); 
        loadNextPage();
    }

    public void loadNextPage() {
        int start = mDisplayedCount;
        int end = Math.min(start + PAGE_SIZE, mFilteredList.size());
        List<Venue> currentDisplay = mDisplayList.getValue();
        if (currentDisplay == null) currentDisplay = new ArrayList<>();
        
        if (start < end) {
            List<Venue> newList = new ArrayList<>(currentDisplay);
            for (int i = start; i < end; i++) {
                newList.add(mFilteredList.get(i));
            }
            mDisplayedCount = end;
            mDisplayList.setValue(newList);
        }
        mShowLoadMore.setValue(mDisplayedCount < mFilteredList.size());
    }

    public void clearFilter() { search(""); }
}
