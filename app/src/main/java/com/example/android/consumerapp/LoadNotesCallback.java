package com.example.android.consumerapp;

import android.database.Cursor;

interface LoadNotesCallback {
    void postExecute(Cursor notes);
}
