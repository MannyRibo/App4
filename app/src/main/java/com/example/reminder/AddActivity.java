package com.example.reminder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reminder.model.BucketListItem;
import com.example.reminder.ui.MainActivity;

public class AddActivity extends AppCompatActivity {

    private EditText NieuweTitle;
    private EditText NieuweDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

//Init local variables
        NieuweTitle = findViewById(R.id.editText_update);
        NieuweDescription = findViewById(R.id.editText_update2);

        Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nieuweTitle = NieuweTitle.getText().toString();
                String nieuweDescription = NieuweDescription.getText().toString();


                BucketListItem  nieuweBucketListItem = new BucketListItem(nieuweTitle, nieuweDescription);

                if ((!TextUtils.isEmpty(nieuweTitle)) && (!TextUtils.isEmpty(nieuweDescription))) {
                    nieuweBucketListItem.setTitle(nieuweTitle);
                    nieuweBucketListItem.setDescription(nieuweDescription);
                    //Prepare the return parameter and return
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MainActivity.BUCKETLISTITEM_TOEVOEGEN, nieuweBucketListItem);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(AddActivity.this, "Voer een titel en omschrijving in", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
