package com.example.reminder.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.reminder.model.BucketListItem;

import java.util.List;

@Dao
public interface BucketListDao {

	@Query("SELECT * FROM bucketListItem")
	List<BucketListItem> getAllBucketListItems();

	@Insert
	void insertBucketListItem(BucketListItem bucketListItem);

	@Delete
	void deleteBucketListItem(BucketListItem bucketListItem);

	@Delete
	void deleteAllBucketListItems(List<BucketListItem> bucketListItems);

	@Update
	void updateBucketListItem(BucketListItem bucketListItem);
}

