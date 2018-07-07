package com.toksaitov.doodles.dummy;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class DoodleRepository {

    private DoodleDao doodleDao;
    private LiveData<List<Doodle>> allDoodles;

    DoodleRepository(Application application) {
        DoodleRoomDatabase db = DoodleRoomDatabase.getDatabase(application);
        doodleDao = db.doodleDao();
        allDoodles = doodleDao.getAllDoodles();
    }

    LiveData<List<Doodle>> getAllDoodles() {
        return allDoodles;
    }

    public void insert(Doodle doodle) {
        new InsertAsyncTask(doodleDao).execute(doodle);
    }
    public void update(Doodle doodle) {
        new UpdateAsyncTask(doodleDao).execute(doodle);
    }
    public void delete(Doodle doodle) {
        new DeleteAsyncTask(doodleDao).execute(doodle);
    }

    private static class InsertAsyncTask extends AsyncTask<Doodle, Void, Void> {
        private DoodleDao asyncTaskDao;

        InsertAsyncTask(DoodleDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doodle... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<Doodle, Void, Void> {
        private DoodleDao asyncTaskDao;

        UpdateAsyncTask(DoodleDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doodle... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }
    private static class DeleteAsyncTask extends AsyncTask<Doodle, Void, Void> {
        private DoodleDao asyncTaskDao;

        DeleteAsyncTask(DoodleDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Doodle... params) {
            asyncTaskDao.delete(params[0]);
            return null;
        }
    }

}