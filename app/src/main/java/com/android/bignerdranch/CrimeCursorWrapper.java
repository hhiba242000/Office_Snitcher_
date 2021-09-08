package com.android.bignerdranch;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.android.bignerdranch.database.CrimeDbSchema.*;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
//now your data is stored in a SQL table, a cursor is your finger moving around and looking for data
    //this class takes the id pointed at and "wraps" into a crime object
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString =
                getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title =
                getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date =
                getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved =
                getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect =
                getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime=new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;


    }
}
