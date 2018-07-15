package com.toksaitov.doodles.dummy;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Doodle.class}, version = 1)
public abstract class DoodleRoomDatabase extends RoomDatabase {

    public abstract DoodleDao doodleDao();

    private static RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback(){
            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db){
                super.onOpen(db);
                new PopulateDbAsync(INSTANCE).execute();
            }
        };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final DoodleDao mDao;

        PopulateDbAsync(DoodleRoomDatabase db) {
            mDao = db.doodleDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
//            mDao.deleteAll();
//            Doodle doodle1 = new Doodle("/data/data/com.toksaitov.doodles/image_1.webp");
//            mDao.insert(doodle1);
//            Doodle doodle2 = new Doodle("/data/data/com.toksaitov.doodles/image_2.webp");
//            mDao.insert(doodle2);

            return null;
        }
    }

    private static DoodleRoomDatabase INSTANCE;
    static DoodleRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DoodleRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                        Room.databaseBuilder(
                            context.getApplicationContext(),
                            DoodleRoomDatabase.class,
                            "doodle_database"
                        ).addCallback(sRoomDatabaseCallback).build();
                }
            }
        }

        return INSTANCE;
    }

}