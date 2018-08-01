package com.emarsys.core.database.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.emarsys.core.database.CoreSQLiteDatabase;
import com.emarsys.core.database.helper.DbHelper;
import com.emarsys.core.util.Assert;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqliteRepository<T> implements Repository<T, SqlSpecification> {

    String tableName;
    DbHelper dbHelper;

    public AbstractSqliteRepository(String tableName, DbHelper dbHelper) {
        this.tableName = tableName;
        this.dbHelper = dbHelper;
    }

    protected abstract ContentValues contentValuesFromItem(T item);

    protected abstract T itemFromCursor(Cursor cursor);

    @Override
    final public void add(T item) {
        Assert.notNull(item, "Item must not be null!");

        ContentValues contentValues = contentValuesFromItem(item);

        CoreSQLiteDatabase database = dbHelper.getWritableCoreDatabase();
        database.beginTransaction();
        database.insert(tableName, null, contentValues);
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    final public List<T> query(SqlSpecification specification) {
        Assert.notNull(specification, "Specification must not be null!");

        CoreSQLiteDatabase database = dbHelper.getReadableCoreDatabase();
        Cursor cursor = database.rawQuery(
                specification.getSql(),
                specification.getArgs());

        return mapCursorToResultList(cursor);
    }

    @Override
    final public void remove(SqlSpecification specification) {
        Assert.notNull(specification, "Specification must not be null!");

        CoreSQLiteDatabase database = dbHelper.getWritableCoreDatabase();
        database.beginTransaction();
        database.delete(
                tableName,
                specification.getSql(),
                specification.getArgs());
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    @Override
    public boolean isEmpty() {
        CoreSQLiteDatabase database = dbHelper.getReadableCoreDatabase();
        Cursor cursor = database.rawQuery(
                String.format("SELECT COUNT(*) FROM %s;", tableName),
                null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
        cursor.close();
        return count == 0;
    }

    private List<T> mapCursorToResultList(Cursor cursor) {
        List<T> result = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                T item = itemFromCursor(cursor);
                result.add(item);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return result;
    }
}