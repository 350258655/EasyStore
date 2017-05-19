package com.shake.easystore.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shake on 17-5-18.
 */
public class AddressDB extends SQLiteOpenHelper {


    /**
     * 数据库名字
     */
    private static final String DB_NAME = "address.db";


    /**
     * 建表语句,创建一个叫 "shake" 的表
     */
    public static final String CREATE_TABLE = "create table shake ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "phone text, "
            + "detailAddress text, "
            + "isDefault text, "
            + "address text)";


    public AddressDB(Context context,int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL(CREATE_TABLE);
    }

    /**
     * 暂时忽略
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
