package com.android.bignerdranch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.bignerdranch.database.CrimeBaseHelper;
import com.android.bignerdranch.database.CrimeDbSchema.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    //this class represents the linking chain between the DB and the fragment/activity
    private static CrimeLab sCrimeLab;//to create a singleton
    private Context mContext;
    private SQLiteDatabase mDatabase;


    private CrimeLab(Context context){
        mContext= context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    //method that returns the singleton of crimeLab not its list of crimes
    public static CrimeLab get(Context context){
        if(sCrimeLab == null)
            sCrimeLab=new CrimeLab(context);

        return sCrimeLab;
    }

    public void addCrime(Crime c){
        ContentValues values= getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }


    public List<Crime> getCrimes (){
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return crimes;

    }

    public Crime getCrime(UUID id) {

        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " = ?", new String[] { id.toString() }
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();

        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime){
        File filesDir= mContext.getFilesDir();
        return new File(filesDir,crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    public void deleteCrime(Crime crime){
        String uuidString = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID+" = ?",new String[] {uuidString});

    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
        null, // columns - null selects all
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
);
        return new CrimeCursorWrapper(cursor);
    }



    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }
}
