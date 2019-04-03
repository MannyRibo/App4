package com.example.reminder.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.reminder.model.BucketListItem;

@Database(entities = {BucketListItem.class}, version = 1, exportSchema = false)
public abstract class BucketListDatabase extends RoomDatabase {
	private final static String NAME_DATABASE = "bucketlist_database1";

	public abstract BucketListDao bucketListDao();

	private static volatile BucketListDatabase INSTANCE;

	public static BucketListDatabase getDatabase(final Context context) {
		if (INSTANCE == null) {
			synchronized (BucketListDatabase.class) {
				if (INSTANCE == null) {
					// Create database here
					INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
							BucketListDatabase.class, NAME_DATABASE)
							.build();
				}
			}
		}
		return INSTANCE;
	}

}
