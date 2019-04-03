package com.example.reminder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.reminder.AddActivity;
import com.example.reminder.R;
import com.example.reminder.database.BucketListDatabase;
import com.example.reminder.model.BucketListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements RecyclerView.OnItemTouchListener {

	//instance variables
	private List<BucketListItem> mBucketListItems;
	private BucketListItemAdapter mAdapter;
	private RecyclerView mRecyclerView;

	private GestureDetector gestureDetector;
	//Constants used when calling the update activity
	public static final String BUCKETLISTITEM_TOEVOEGEN = "bucketlist";
	public static final int REQUESTCODE = 1234;
	private int mModifyPosition;

	private BucketListDatabase db;
	private Executor executor = Executors.newSingleThreadExecutor();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = BucketListDatabase.getDatabase(this);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		//Initialize the instance variables
		mRecyclerView = findViewById(R.id.recyclerView);
		mBucketListItems = new ArrayList<>();

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        // Delete an item from the shopping list on long press.
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null) {
                    int adapterPosition = mRecyclerView.getChildAdapterPosition(child);
                    deleteBucketListitem(mBucketListItems.get(adapterPosition));
                }
            }
        });

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Intent intent = new Intent(MainActivity.this, AddActivity.class);
				startActivityForResult(intent, REQUESTCODE);
			}
		});

		/*
		 * Add a touch helper to the RecyclerView to recognize when a user swipes to delete a list entry.
		 * An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
		 * and uses callbacks to signal when a user is performing these actions.
		 */
		ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
				new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
					@Override
					public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
						return false;
					}

					//Called when a user swipes left or right on a ViewHolder
					@Override
					public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
						//Get the index corresponding to the selected position
						int position = (viewHolder.getAdapterPosition());
						deleteBucketListitem(mBucketListItems.get(position));
						mBucketListItems.remove(position);
						mAdapter.notifyItemRemoved(position);
					}
				};

		ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
		itemTouchHelper.attachToRecyclerView(mRecyclerView);
		mRecyclerView.addOnItemTouchListener(this);
		getAllBucketListItems();
	}

	private void updateUI() {
		if (mAdapter == null) {
			mAdapter = new BucketListItemAdapter(mBucketListItems);
			mRecyclerView.setAdapter(mAdapter);
		} else {
			mAdapter.swapList(mBucketListItems);
		}
	}

	private void getAllBucketListItems() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				mBucketListItems = db.bucketListDao().getAllBucketListItems();

				// In a background thread the user interface cannot be updated from this thread.
				// This method will perform statements on the main thread again.
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		});
	}

	private void insertBucketListItem(final BucketListItem bucketListItem) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				db.bucketListDao().insertBucketListItem(bucketListItem);
				getAllBucketListItems(); // Because the Room database has been modified we need to get the new list of reminders.
			}
		});
	}

	private void updateBucketListItem(final BucketListItem bucketListItem) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				db.bucketListDao().updateBucketListItem(bucketListItem);
				getAllBucketListItems(); // Because the Room database has been modified we need to get the new list of reminders.
			}
		});
	}

	private void deleteBucketListitem(final BucketListItem bucketListItem) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				db.bucketListDao().deleteBucketListItem(bucketListItem);
				getAllBucketListItems(); // Because the Room database has been modified we need to get the new list of reminders.
			}
		});
	}

    private void deleteAllBucketListItems(final List<BucketListItem> bucketListItems) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                db.bucketListDao().deleteAllBucketListItems(bucketListItems);
                getAllBucketListItems();
            }
        });

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_delete_item) {
            deleteAllBucketListItems(mBucketListItems);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int mAdapterPosition = recyclerView.getChildAdapterPosition(child);

        if (child != null && gestureDetector.onTouchEvent(motionEvent)) {

            if (mBucketListItems.get(mAdapterPosition).isChecked() == false) {
                mBucketListItems.get(mAdapterPosition).setChecked(true);
            }

            if (mBucketListItems.get(mAdapterPosition).isChecked() == true) {
                mBucketListItems.get(mAdapterPosition).setChecked(false);
            }
        }

		return false;
	}

	@Override
	public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        gestureDetector.onTouchEvent(motionEvent);
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        int mAdapterPosition = recyclerView.getChildAdapterPosition(child);

        if (child != null && gestureDetector.onTouchEvent(motionEvent)) {

            if (mBucketListItems.get(mAdapterPosition).isChecked() == false) {
                mBucketListItems.get(mAdapterPosition).setChecked(true);
            }

            if (mBucketListItems.get(mAdapterPosition).isChecked() == true) {
                mBucketListItems.get(mAdapterPosition).setChecked(false);
            }
        }
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean b) {

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUESTCODE) {
			if (resultCode == RESULT_OK) {
				BucketListItem updatedBucketListItem = data.getParcelableExtra(MainActivity.BUCKETLISTITEM_TOEVOEGEN);
				// New timestamp: timestamp of update
				insertBucketListItem(updatedBucketListItem);
			}
		}
	}
}