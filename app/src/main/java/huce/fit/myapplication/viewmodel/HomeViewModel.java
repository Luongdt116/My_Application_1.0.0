package huce.fit.myapplication.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import huce.fit.myapplication.objects.Venue;
import huce.fit.myapplication.repository.FieldRepository;

public class HomeViewModel extends AndroidViewModel {
    private MutableLiveData<List<Venue>> mFilteredList = new MutableLiveData<>();
    private MutableLiveData<List<String>> mHistoryList = new MutableLiveData<>();
    private FieldRepository mFieldRepository;
    private List<Venue> mFullList = new ArrayList<>();
    private SharedPreferences mPrefs;
    private static final String PREF_SEARCH_HISTORY = "search_history";

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mFieldRepository = new FieldRepository();
        mPrefs = application.getSharedPreferences("SearchPrefs", Context.MODE_PRIVATE);
        loadHistory();
    }

    public LiveData<List<Venue>> getFields() {
        return mFilteredList;
    }

    public LiveData<List<String>> getHistory() {
        return mHistoryList;
    }

    public void fetchFieldsFromFirebase() {
        mFieldRepository.fetchAllFields(new FieldRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<Venue> venueList) {
                mFullList = venueList;
                mFilteredList.setValue(venueList);
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    public void filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            mFilteredList.setValue(mFullList);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<Venue> filtered = new ArrayList<>();
        for (Venue venue : mFullList) {
            boolean matchesName = venue.getVenue_name() != null && venue.getVenue_name().toLowerCase().contains(lowerQuery);
            boolean matchesAddress = venue.getAddress_detail() != null && venue.getAddress_detail().toLowerCase().contains(lowerQuery);
            boolean matchesSport = venue.getSport_name() != null && venue.getSport_name().toLowerCase().contains(lowerQuery);

            if (matchesName || matchesAddress || matchesSport) {
                filtered.add(venue);
            }
        }
        mFilteredList.setValue(filtered);
    }

    // --- SEARCH HISTORY LOGIC ---

    private void loadHistory() {
        Set<String> set = mPrefs.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>());
        List<String> list = new ArrayList<>(set);
        // Ở đây có thể sort theo thời gian nếu lưu kèm timestamp, hiện tại lấy tạm list
        mHistoryList.setValue(list);
    }

    public void addHistory(String query) {
        if (query == null || query.trim().isEmpty()) return;
        query = query.trim();
        Set<String> set = mPrefs.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>());
        Set<String> newSet = new HashSet<>(set);
        newSet.add(query);
        mPrefs.edit().putStringSet(PREF_SEARCH_HISTORY, newSet).apply();
        loadHistory();
    }

    public void removeHistory(String query) {
        Set<String> set = mPrefs.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>());
        Set<String> newSet = new HashSet<>(set);
        newSet.remove(query);
        mPrefs.edit().putStringSet(PREF_SEARCH_HISTORY, newSet).apply();
        loadHistory();
    }

    public void clearAllHistory() {
        mPrefs.edit().remove(PREF_SEARCH_HISTORY).apply();
        loadHistory();
    }
}
