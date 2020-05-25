package com.edu.avas.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyCoursesDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyCourses.db";
    private static final String TABLE_NAME = "CoursesList";
    private static final String COL_1 = "COURSE_NAME";
    private static final String COL_2 = "COURSE_STATUS";
    private static final int DB_VER = 1;

    public MyCoursesDatabase(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(COURSE_NAME TEXT PRIMARY KEY, COURSE_STATUS TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addToList(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("Select * from " + TABLE_NAME + " where COURSE_NAME = ?");
        Cursor cursor = db.rawQuery(query, new String[]{name});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_1, name);
            contentValues.put(COL_2, "pending");

            long result = db.insert(TABLE_NAME,null,contentValues);
            return result != -1;
        }
    }

    public int checkInList(String name){
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("Select * from "+TABLE_NAME+" where COURSE_NAME = ?");
        Cursor cursor = db.rawQuery(query,new String[]{name});
        if(cursor.getCount()>0){
            return 1;
        }else{
            return 0;
        }
    }

    public void updateList(String status,String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = String.format("Update "+TABLE_NAME+" SET COURSE_STATUS = %s where COURSE_NAME = %s",status,name);
        db.execSQL(query);
    }

    public void removeFromList(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_NAME,"COURSE_NAME=?",new String[]{name});
    }

    public void deleteList(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }
}