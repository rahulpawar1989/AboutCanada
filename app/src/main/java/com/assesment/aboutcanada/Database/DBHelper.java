package com.assesment.aboutcanada.Database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.assesment.aboutcanada.model.CityInfoRow;

public class DBHelper extends SQLiteOpenHelper {

    public static final String CITY_INFO_COLUMN_ID = "id";
    private static final String DATABASE_NAME = "CityDBName.db";
    private static final String CITY_INFO_TABLE_NAME = "cityinfo";
    private static final String CITY_INFO_COLUMN_TITTLE = "tittle";
    private static final String CITY_INFO_COLUMN_DESCRIPTION = "description";
    private static final String CITY_INFO_COLUMN_IMAGEREF = "cityinfoimgref";
    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + CITY_INFO_TABLE_NAME + "" +
            "(" +
            CITY_INFO_COLUMN_ID + " TEXT PRIMARY KEY," +
            CITY_INFO_COLUMN_TITTLE + " text ," + CITY_INFO_COLUMN_DESCRIPTION + " text ," +
            CITY_INFO_COLUMN_IMAGEREF + " text )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS CITY_INFO_TABLE_NAME");
        onCreate(db);
    }

    public boolean insertCityInfo(String tittle, String desc, String imgref) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CITY_INFO_COLUMN_TITTLE, tittle);
        contentValues.put(CITY_INFO_COLUMN_DESCRIPTION, desc);
        contentValues.put(CITY_INFO_COLUMN_IMAGEREF, imgref);

        db.insert(CITY_INFO_TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<CityInfoRow> getAllData() {
        ArrayList<CityInfoRow> mCityInfoRow = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CITY_INFO_TABLE_NAME, null);
        if (res.moveToFirst()) {
            do {
                CityInfoRow cir = new CityInfoRow();
                cir.setTitle(res.getString(1));
                cir.setDescription(res.getString(2));
                cir.setImageHref(res.getString(3));
                mCityInfoRow.add(cir);
            } while (res.moveToNext());
        }
        db.close();
        return mCityInfoRow;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CITY_INFO_TABLE_NAME);
        return numRows;
    }

}