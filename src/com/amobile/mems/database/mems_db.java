package com.amobile.mems.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.amobile.mems.helpers.AssetsHelper;
import com.amobile.mems.helpers.CatalogCell;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: alekse
 * Date: 25.04.13
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public class mems_db {
    public static String Lock = "dblock";
    public static Context con;
    private static final String DB_NAME = "mems_db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_FAVORITES = "FAVORITES";
    private static final String TABLE_LASTSEEN = "LASTSEEN";
    private static final String TABLE_FILES = "FILES";

    public static final String DB_FILES_CREATE = "CREATE TABLE 'FILES' ('id' INTEGER PRIMARY KEY NOT NULL UNIQUE , 'path' TEXT, 'name' TEXT)";
    public static final String DB_FAVORITES_CREATE = "CREATE TABLE 'FAVORITES' ('id' INTEGER PRIMARY KEY NOT NULL UNIQUE , 'path' TEXT, 'name' TEXT UNIQUE)";
    public static final String DB_LASTSEEN_CREATE = "CREATE TABLE 'LASTSEEN' ('id' INTEGER PRIMARY KEY NOT NULL UNIQUE , 'path' TEXT, 'name' TEXT UNIQUE)";
    private final Context mCtx;
    ProgressDialog pd;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;


    public mems_db(Context ctx) {
        mCtx = ctx;
        con = ctx;
    }

    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        try {
            mDB = mDBHelper.getWritableDatabase();
        } catch (Exception ex) {
            String err = ex.getMessage().toString();
        }
    }

    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    public Cursor getFavorites() {
        mDB.beginTransaction();
        Cursor c = mDB.query(TABLE_FAVORITES, null, null, null, null, null, null);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        return c;
    }

    public Cursor getFiles() {
        mDB.beginTransaction();
        Cursor c = mDB.query(TABLE_FILES, null, null, null, null, null, null);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        return c;
    }

    public Cursor getLastseen() {
        mDB.beginTransaction();
        Cursor c = mDB.query(TABLE_LASTSEEN, null, null, null, null, null, "id DESC");
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        return c;
    }

    public void deleteFromFavorites(String id) {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_FAVORITES, "id = " + id, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public void deleteFromLastseen(String id) {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_LASTSEEN, "id = " + id, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public void deleteFromLastseen2(String id) {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_LASTSEEN, "id < " + id, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public void addToFavorite(CatalogCell value)//Добавить пользователя
    {
        ContentValues cv = new ContentValues();
        cv.put("path", value.path);
        cv.put("name", value.fileName);
        mDB.beginTransaction();
        mDB.insert(TABLE_FAVORITES, null, cv);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    public void addToFiles(String path, String name)//Добавить пользователя
    {
        ContentValues cv = new ContentValues();
        cv.put("path", path);
        cv.put("name", name);
        mDB.beginTransaction();
        mDB.insert(TABLE_FILES, null, cv);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    public void addToLastseen(CatalogCell value)//Добавить пользователя
    {
        ContentValues cv = new ContentValues();
        cv.put("path", value.path);
        cv.put("name", value.fileName);
        mDB.beginTransaction();
        mDB.insert(TABLE_LASTSEEN, null, cv);
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
    }

    public void delAllFavorites()    //Удалить пользователя
    {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_FAVORITES, null, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public void delAllLastseen()    //Удалить пользователя
    {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_LASTSEEN, null, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public void delAllFiles()    //Удалить пользователя
    {
        synchronized (Lock) {
            mDB.beginTransaction();
            mDB.delete(TABLE_FILES, null, null);
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
        }
    }

    public ArrayList<CatalogCell> GetAllFavorites()  //Получить все favorites
    {
        ArrayList<CatalogCell> ret = new ArrayList<CatalogCell>();
        mDB.beginTransaction();
        Cursor c = getFavorites();
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        if (c.moveToFirst()) {
            do {
                CatalogCell result = new CatalogCell();
                result.id = c.getInt(c.getColumnIndex("id"));
                result.path = c.getString(c.getColumnIndex("path"));
                result.fileName = c.getString(c.getColumnIndex("name"));
                result.isFolder = false;
                ret.add(result);
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    public ArrayList<CatalogCell> GetAllFilesInFolder(String foldername)  //Получить все мемы в папке
    {
        //pd = ProgressDialog.show(mCtx, "Подготовка данных", "Пожалуйста подождите...", true, true);
        ArrayList<CatalogCell> ret = new ArrayList<CatalogCell>();
        mDB.beginTransaction();
        Cursor c = getFiles();
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex("path")).equals(foldername)) {
                    CatalogCell result = new CatalogCell();
                    result.id = c.getInt(c.getColumnIndex("id"));
                    result.path = c.getString(c.getColumnIndex("path"));
                    result.fileName = c.getString(c.getColumnIndex("name"));
                    result.isFolder = false;
                    ret.add(result);
                }
            } while (c.moveToNext());
        }
        c.close();
        if (pd != null && pd.isShowing()) pd.dismiss();
        return ret;
    }

    public ArrayList<CatalogCell> GetAllFilesInMainFolder(AssetManager mgr)  //Получить все папки мемов
    {
      //  pd = ProgressDialog.show(mCtx, "Подготовка данных", "Пожалуйста подождите...", true, true);
        ArrayList<CatalogCell> ret = new ArrayList<CatalogCell>();
        Integer id = 0;
        final String count_raws = "SELECT COUNT(id) AS id FROM FILES ";
        Cursor mCursor = mDB.rawQuery(count_raws, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(mCursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (id > 0) //берем все данные из базы
        {

            ArrayList<String> folders = getAllFolders();
            for (String folder : folders) {
                final String query = "SELECT * FROM FILES WHERE path='" + folder.replace("'","") + "' LIMIT 1";
                Cursor tmp_cursor = mDB.rawQuery(query, null);
                if (tmp_cursor.moveToFirst()) {
                    do {
                        CatalogCell result = new CatalogCell();
                        result.id = tmp_cursor.getInt(tmp_cursor.getColumnIndex("id"));
                        result.path = tmp_cursor.getString(tmp_cursor.getColumnIndex("path"));
                        result.fileName = tmp_cursor.getString(tmp_cursor.getColumnIndex("name"));
                        result.isFolder = true;
                        ret.add(result);
                    } while (tmp_cursor.moveToNext());
                }
                tmp_cursor.close();
            }
            if (pd != null && pd.isShowing()) pd.dismiss();
        }
        else //загружаем все из assets
        {
           // pd = ProgressDialog.show(mCtx, "Подготовка данных", "Пожалуйста подождите...", true, true);
            String[] assets_folders= AssetsHelper.getFolders(mgr);
            for(int i=0;i<assets_folders.length;i++)
            {
                String[] fileNames=AssetsHelper.getFileNamesinFolder(mgr,assets_folders[i]);
                for(int j=0;j<fileNames.length;j++)
                {
                    addToFiles(assets_folders[i],fileNames[j]);
                }
            }
            ArrayList<String> folders = getAllFolders();
            for (String folder : folders) {
                final String query = "SELECT * FROM FILES WHERE path='" + folder.replace("'","") + "' LIMIT 1";
                Cursor tmp_cursor = mDB.rawQuery(query, null);
                if (tmp_cursor.moveToFirst()) {
                    do {
                        CatalogCell result = new CatalogCell();
                        result.id = tmp_cursor.getInt(tmp_cursor.getColumnIndex("id"));
                        result.path = tmp_cursor.getString(tmp_cursor.getColumnIndex("path"));
                        result.fileName = tmp_cursor.getString(tmp_cursor.getColumnIndex("name"));
                        result.isFolder = true;
                        ret.add(result);
                    } while (tmp_cursor.moveToNext());
                }
                tmp_cursor.close();
            }
            if (pd != null && pd.isShowing()) pd.dismiss();
        }
        return ret;


    }

    public ArrayList<String> getAllFolders() {
        ArrayList<String> result = new ArrayList<String>();
        mDB.beginTransaction();
        Cursor c = getFiles();
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        if (c.moveToFirst()) {
            do {
                String tmp = c.getString(c.getColumnIndex("path"));
                if (!result.contains(tmp))
                    result.add(tmp);
            } while (c.moveToNext());
        }
        c.close();
        return result;

    }

    public Integer isFavorite(CatalogCell tmp) {
        Integer result = 0;
        mDB.beginTransaction();
        Cursor c = getFavorites();
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex("path")).equals(tmp.path) && c.getString(c.getColumnIndex("name")).equals(tmp.fileName)) {
                    result = c.getInt(c.getColumnIndex("id"));
                    return result;
                }

            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public void filterLastSeen() {
        Integer id = 0;
        Integer id2 = 0;
        final String MY_QUERY = "SELECT MAX(id) AS id FROM LASTSEEN";
        final String MY_QUERY2 = "SELECT COUNT(id) AS id FROM LASTSEEN ";
        Cursor mCursor = mDB.rawQuery(MY_QUERY, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id = mCursor.getInt(mCursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        mCursor = mDB.rawQuery(MY_QUERY2, null);
        try {
            if (mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                id2 = mCursor.getInt(mCursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (id > 20 && id2 > 20) {
            id = id - 20;
            deleteFromLastseen2(id.toString());
        }
    }

    public ArrayList<CatalogCell> GetAllLastseen()  //Получить все последние просмотренные
    {
        ArrayList<CatalogCell> ret = new ArrayList<CatalogCell>();
        mDB.beginTransaction();
        Cursor c = getLastseen();
        mDB.setTransactionSuccessful();
        mDB.endTransaction();
        if (c.moveToFirst()) {
            do {
                CatalogCell result = new CatalogCell();
                result.id = c.getInt(c.getColumnIndex("id"));
                result.path = c.getString(c.getColumnIndex("path"));
                result.fileName = c.getString(c.getColumnIndex("name"));
                result.isFolder = false;
                ret.add(result);
            } while (c.moveToNext());
        }
        c.close();
        return ret;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // ������� � ��������� ��

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_FILES_CREATE);
            db.execSQL(DB_FAVORITES_CREATE);
            db.execSQL(DB_LASTSEEN_CREATE);

        }


        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion != oldVersion) {
                mCtx.deleteDatabase(DB_NAME);
            }
        }
    }
}