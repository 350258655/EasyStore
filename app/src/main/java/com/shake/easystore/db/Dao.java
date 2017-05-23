package com.shake.easystore.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.shake.easystore.bean.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shake on 17-5-18.
 * 对数据库进行增删改查
 */
public class Dao {

    private AddressDB mHelper;

    private static Dao mDao;

    private static final String FORM_NAME = "shake";

    private List<Address> mAddressList = new ArrayList<Address>();

    private OnCompleteListener mListener;

    private Dao(Context context) {
        //创建数据库
        this.mHelper = new AddressDB(context, 1);
    }

    /**
     * 以单例模式来获取Dao
     *
     * @param context
     * @return
     */
    public static Dao getDao(Context context) {
        if (mDao == null) {
            //要注意防止内存泄露
            mDao = new Dao(context.getApplicationContext());
        }
        return mDao;
    }


    /**
     * 添加数据
     *
     * @param address
     */
    public void add(Address address) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //封装数据
        ContentValues values = new ContentValues();
        values.put("name", address.getName());
        values.put("phone", address.getPhone());
        values.put("detailAddress", address.getDetailAddress());
        values.put("isDefault", address.getIsDefault());
        values.put("address", address.getAddress());

        //添加数据
        db.insert(FORM_NAME, null, values);
    }

    /**
     * 添加数据
     *
     * @param address
     */
    public void add(Address address, OnCompleteListener onCompleteListener) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //封装数据
        ContentValues values = new ContentValues();
        values.put("name", address.getName());
        values.put("phone", address.getPhone());
        values.put("detailAddress", address.getDetailAddress());
        values.put("isDefault", String.valueOf(address.getIsDefault()));
        values.put("address", address.getAddress());

        //添加数据
        db.insert(FORM_NAME, null, values);

        this.mListener = onCompleteListener;
        mListener.onDone(null);
    }


    /**
     * 更新数据
     */
    public void update(Address address, OnCompleteListener onCompleteListener) {

        Log.i("TAG", "DAO，update，传递过来的ID是多少: " + address.getId());

        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //封装数据
        ContentValues values = new ContentValues();
        values.put("name", address.getName());
        values.put("phone", address.getPhone());
        values.put("detailAddress", address.getDetailAddress());
        values.put("isDefault", String.valueOf(address.getIsDefault()));
        values.put("address", address.getAddress());
        String id = address.getId();
        Log.i("TAG", "DAO，update，修改的ID是多少：" + id + ",它是不是默认的：" + address.getIsDefault());
        //更新数据
        db.update(FORM_NAME, values, "id=?", new String[]{id});
        this.mListener = onCompleteListener;
        mListener.onDone(null);
    }

    /**
     * 更新数据
     */
    public void update(Address address) {


        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //封装数据
        ContentValues values = new ContentValues();
        values.put("name", address.getName());
        values.put("phone", address.getPhone());
        values.put("detailAddress", address.getDetailAddress());
        values.put("isDefault", String.valueOf(address.getIsDefault()));
        values.put("address", address.getAddress());
        String id = address.getId();
        Log.i("TAG", "DAO，update，修改的ID是多少：" + id + ",它是不是默认的：" + address.getIsDefault());
        //更新数据
        db.update(FORM_NAME, values, "id=?", new String[]{id});

    }


    /**
     * 修改全部默认值为false
     */
    public void upDateIsDefaultToFalse() {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql = "UPDATE shake SET isDefault = 'false'";
        db.execSQL(sql);

    }

    /**
     * 设置默认收货地址
     *
     * @param id
     */
    public void upDateTheDefault(String id) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();


        String sql = "UPDATE shake SET isDefault = 'true' WHERE id = '" + id + "'";
        Log.i("TAG", "DAO,执行更新的SQL语句: " + sql);
        db.execSQL(sql);
    }


    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //删除数据
        db.delete(FORM_NAME, "id=?", new String[]{id});
    }


    /**
     * 查询数据
     *
     * @return
     */
    public List<Address> query() {

        //查询全部，每次查询之前，都把集合中的数据先清空
        if (mAddressList.size() > 0) {
            mAddressList.clear();
        }

        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //查询表中的所有数据
        Cursor cursor = db.query(FORM_NAME, null, null, null, null, null, null);

        //封装查询出来的对象
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String detailAddress = cursor.getString(cursor.getColumnIndex("detailAddress"));
            String isDefault = cursor.getString(cursor.getColumnIndex("isDefault"));

            //封装数据并且添加到集合中
            Address addressObject = new Address(String.valueOf(id), name, address, phone, detailAddress, Boolean.valueOf(isDefault));
            mAddressList.add(addressObject);
        }

        return mAddressList;
    }

    /**
     * 查询数据
     *
     * @return
     */
    public void query(OnCompleteListener listener) {
        //查询全部，同时把默认地址的信息 插入到首行的位置
        queryAll();

        this.mListener = listener;
        listener.onDone(mAddressList);
    }

    private void queryAll() {
        //查询全部，每次查询之前，都把集合中的数据先清空
        if (mAddressList.size() > 0) {
            mAddressList.clear();
        }

        //获取数据库对象
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //查询表中的所有数据
        Cursor cursor = db.query(FORM_NAME, null, null, null, null, null, null);

        //封装查询出来的对象
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String phone = cursor.getString(cursor.getColumnIndex("phone"));
            String isDefault = cursor.getString(cursor.getColumnIndex("isDefault"));
            String detailAddress = cursor.getString(cursor.getColumnIndex("detailAddress"));

            Log.i("TAG", "查询，ID是多少：" + id + ",名字是：" + name + ",是不是被选中的:" + isDefault);

            //封装数据并且添加到集合中
            Address addressObject = new Address(String.valueOf(id), name, address, phone, detailAddress, Boolean.valueOf(isDefault));


            //TODO 假如是true，那么让它位于第一位
            if (addressObject.getIsDefault()) {
                mAddressList.add(0, addressObject);
            } else {
                mAddressList.add(addressObject);
            }

        }

    }



    /**
     * 操作完成的接口
     */
    public interface OnCompleteListener {
        void onDone(List<Address> list);
    }


}
