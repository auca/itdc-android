package com.toksaitov.doodles.dummy;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class DoodleViewModel extends AndroidViewModel {

    private DoodleRepository repository;

    private LiveData<List<Doodle>> allDoodles;

    public DoodleViewModel(Application application) {
        super(application);

        repository = new DoodleRepository(application);
        allDoodles = repository.getAllDoodles();
    }

    public LiveData<List<Doodle>> getAllDoodles() {
        return allDoodles;
    }

    public void insert(Doodle doodle) {
        repository.insert(doodle);
    }
    public void update(Doodle doodle) {
        repository.update(doodle);
    }
    public void delete(Doodle doodle) {
        repository.delete(doodle);
    }

}