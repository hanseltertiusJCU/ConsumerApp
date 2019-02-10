package com.example.android.consumerapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.consumerapp.entity.NoteItem;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.android.consumerapp.db.DatabaseContract.NoteColumns.CONTENT_URI;
import static com.example.android.consumerapp.db.DatabaseContract.NoteColumns.DATE;
import static com.example.android.consumerapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.example.android.consumerapp.db.DatabaseContract.NoteColumns.TITLE;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    // Buat variable untuk digunakan
    private EditText edtTitle, edtDescription;
    private NoteItem noteItem = null;
    private boolean isUpdate = false;
    private ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Inisiasi variable
        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        Button btnSubmit = findViewById(R.id.btn_submit);
        resolver = getContentResolver();
        btnSubmit.setOnClickListener(this);

        // Dapatin uri dari intent
        Uri uri = getIntent().getData();

        // Code ini berguna untuk mengecek uri
        if(uri != null){
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if(cursor != null){
                if(cursor.moveToFirst())
                    noteItem = new NoteItem(cursor);
                cursor.close();
            }
        }

        // Code ini berguna untuk merubah title action bar dan button text jika modenya itu sedang update data/tambah baru
        String actionBarTitle;
        String btnActionTitle;
        if(noteItem != null){
            isUpdate = true;
            actionBarTitle = "Update";
            btnActionTitle = "Simpan";

            edtTitle.setText(noteItem.getTitle());
            edtDescription.setText(noteItem.getDescription());
        } else {
            actionBarTitle = "Tambah Baru";
            btnActionTitle = "Submit";
        }
        btnSubmit.setText(btnActionTitle);
        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_submit){
            String title = edtTitle.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();

            boolean isEmptyField = false;
            if(TextUtils.isEmpty(title)){
                isEmptyField = true;
                edtTitle.setError("Field tidak boleh kosong");
            }

            if(!isEmptyField){
                ContentValues mContentValues = new ContentValues();
                mContentValues.put(TITLE, title);
                mContentValues.put(DESCRIPTION, description);
                mContentValues.put(DATE, getCurrentDate());

                // Cek jika modenya itu sedang update data, jika tidak maka kita oper URI ke Content Resolver dengan insert method
                if(isUpdate){
                    // Update data
                    Uri uri = getIntent().getData();
                    resolver.update(uri, mContentValues, null, null);
                    Toast.makeText(this, "Satu catatan berhasil diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    // Insert data
                    resolver.insert(CONTENT_URI, mContentValues);
                    Toast.makeText(this, "Satu catatan berhasil diinputkan", Toast.LENGTH_SHORT).show();
                }
                resolver.notifyChange(CONTENT_URI, new MainActivity.DataObserver(new Handler(), this)); // Untuk mengirim sinyal ke observer yg sudah terdaftar krn ada perubahan
                finish();
            }
        }
    }

    private String getCurrentDate() {
        // Method ini berguna untuk merepresentasikan format data yg diperlukan
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        // Format date ke dalam string dengan format method
        return dateFormat.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(isUpdate){
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        } else if(item.getItemId() == R.id.action_delete){
            showAlertDeleteDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDeleteDialog() {
        String dialogMessage = "Apakah anda yakin ingin menghapus item ini?";
        String dialogTitle = "Hapus note";
        // Membangun AlertDialog sbg konfirmasi bahwa sebuah item akan di delete
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Uri uri = getIntent().getData();
                        resolver.delete(uri, null, null);
                        resolver.notifyChange(CONTENT_URI, new MainActivity.DataObserver(new Handler(), FormActivity.this));

                        Toast.makeText(FormActivity.this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show();

                        finish();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
