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
    private final MutableLiveData<Boolean> mShowLoadMoreDiscovery = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mShowClearFilter = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();
    private final MutableLiveData<String> mWelcomeMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mIsLoggedIn = new MutableLiveData<>();
    
    private final FieldRepository mFieldRepository;
    private List<Venue> mAllVenues = new ArrayList<>(); 
    private List<Venue> mAllPromotedVenues = new ArrayList<>();
    
    private List<Venue> mCurrentFilteredList = new ArrayList<>();
    private List<Venue> mCurrentPromotedFilteredList = new ArrayList<>();
    
    private final int PAGE_SIZE = 6;
    private int mDisplayedCount = 0;
    private int mDisplayedPromotedCount = 0;
    private String mCurrentQuery = "";

    public HomeViewModel() {
        mFieldRepository = new FieldRepository();
    }

    public LiveData<List<Venue>> getDisplayList() { return mDisplayList; }
    public LiveData<List<Venue>> getPromotedFields() { return mPromotedList; }
    public LiveData<Boolean> getShowLoadMore() { return mShowLoadMore; }
    public LiveData<Boolean> getShowLoadMoreDiscovery() { return mShowLoadMoreDiscovery; }
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
        if (!mAllPromotedVenues.isEmpty()) return;
        refreshPromotedFields();
    }

    public void refreshPromotedFields() {
        mIsLoading.setValue(true);
        mFieldRepository.fetchPromotedFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mAllPromotedVenues = venueList;
                applyDiscoveryFilterAndReset();
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

    public void searchDiscovery(String query) {
        mCurrentQuery = query.toLowerCase().trim();
        applyDiscoveryFilterAndReset();
    }

    private void applyFilterAndResetPagination() {
        mCurrentFilteredList = filterList(mAllVenues);
        mDisplayedCount = 0;
        mDisplayList.setValue(new ArrayList<>()); 
        loadNextPage();
    }

    private void applyDiscoveryFilterAndReset() {
        mCurrentPromotedFilteredList = filterList(mAllPromotedVenues);
        mDisplayedPromotedCount = 0;
        mPromotedList.setValue(new ArrayList<>());
        loadNextPageDiscovery();
    }

    private List<Venue> filterList(List<Venue> source) {
        if (mCurrentQuery.isEmpty()) return new ArrayList<>(source);
        List<Venue> filtered = new ArrayList<>();
        for (Venue venue : source) {
            String name = venue.getVenue_name() != null ? venue.getVenue_name().toLowerCase() : "";
            String sport = venue.getSport_name() != null ? venue.getSport_name().toLowerCase() : "";
            if (name.contains(mCurrentQuery) || sport.contains(mCurrentQuery)) {
                filtered.add(venue);
            }
        }
        return filtered;
    }

    public void loadNextPage() {
        mDisplayedCount = paginate(mCurrentFilteredList, mDisplayList, mDisplayedCount, mShowLoadMore);
    }

    public void loadNextPageDiscovery() {
        mDisplayedPromotedCount = paginate(mCurrentPromotedFilteredList, mPromotedList, mDisplayedPromotedCount, mShowLoadMoreDiscovery);
    }

    private int paginate(List<Venue> source, MutableLiveData<List<Venue>> target, int currentCount, MutableLiveData<Boolean> loadMoreFlag) {
        int start = currentCount;
        int end = Math.min(start + PAGE_SIZE, source.size());
        List<Venue> currentDisplay = target.getValue();
        if (currentDisplay == null) currentDisplay = new ArrayList<>();
        
        if (start < end) {
            for (int i = start; i < end; i++) {
                currentDisplay.add(source.get(i));
            }
            target.setValue(new ArrayList<>(currentDisplay));
        }
        loadMoreFlag.setValue(end < source.size());
        return end;
    }

    public void clearFilter() { search(""); }
}
