package com.example.beerstack.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ItemDao_Impl implements ItemDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Item> __insertionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __deletionAdapterOfItem;

  private final EntityDeletionOrUpdateAdapter<Item> __updateAdapterOfItem;

  public ItemDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItem = new EntityInsertionAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `items` (`beerid`,`beername`,`beerprice`,`beerrating`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getBeerid());
        statement.bindString(2, entity.getBeername());
        statement.bindLong(3, entity.getBeerprice());
        statement.bindLong(4, entity.getBeerrating());
      }
    };
    this.__deletionAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `items` WHERE `beerid` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getBeerid());
      }
    };
    this.__updateAdapterOfItem = new EntityDeletionOrUpdateAdapter<Item>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `items` SET `beerid` = ?,`beername` = ?,`beerprice` = ?,`beerrating` = ? WHERE `beerid` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Item entity) {
        statement.bindLong(1, entity.getBeerid());
        statement.bindString(2, entity.getBeername());
        statement.bindLong(3, entity.getBeerprice());
        statement.bindLong(4, entity.getBeerrating());
        statement.bindLong(5, entity.getBeerid());
      }
    };
  }

  @Override
  public Object insert(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Item item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfItem.handle(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Item> getItem(final int id) {
    final String _sql = "SELECT * from items WHERE beerid = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<Item>() {
      @Override
      @NonNull
      public Item call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBeerid = CursorUtil.getColumnIndexOrThrow(_cursor, "beerid");
          final int _cursorIndexOfBeername = CursorUtil.getColumnIndexOrThrow(_cursor, "beername");
          final int _cursorIndexOfBeerprice = CursorUtil.getColumnIndexOrThrow(_cursor, "beerprice");
          final int _cursorIndexOfBeerrating = CursorUtil.getColumnIndexOrThrow(_cursor, "beerrating");
          final Item _result;
          if (_cursor.moveToFirst()) {
            final int _tmpBeerid;
            _tmpBeerid = _cursor.getInt(_cursorIndexOfBeerid);
            final String _tmpBeername;
            _tmpBeername = _cursor.getString(_cursorIndexOfBeername);
            final int _tmpBeerprice;
            _tmpBeerprice = _cursor.getInt(_cursorIndexOfBeerprice);
            final int _tmpBeerrating;
            _tmpBeerrating = _cursor.getInt(_cursorIndexOfBeerrating);
            _result = new Item(_tmpBeerid,_tmpBeername,_tmpBeerprice,_tmpBeerrating);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Item>> getAllItems() {
    final String _sql = "SELECT * from items ORDER BY beername ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"items"}, new Callable<List<Item>>() {
      @Override
      @NonNull
      public List<Item> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBeerid = CursorUtil.getColumnIndexOrThrow(_cursor, "beerid");
          final int _cursorIndexOfBeername = CursorUtil.getColumnIndexOrThrow(_cursor, "beername");
          final int _cursorIndexOfBeerprice = CursorUtil.getColumnIndexOrThrow(_cursor, "beerprice");
          final int _cursorIndexOfBeerrating = CursorUtil.getColumnIndexOrThrow(_cursor, "beerrating");
          final List<Item> _result = new ArrayList<Item>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Item _item;
            final int _tmpBeerid;
            _tmpBeerid = _cursor.getInt(_cursorIndexOfBeerid);
            final String _tmpBeername;
            _tmpBeername = _cursor.getString(_cursorIndexOfBeername);
            final int _tmpBeerprice;
            _tmpBeerprice = _cursor.getInt(_cursorIndexOfBeerprice);
            final int _tmpBeerrating;
            _tmpBeerrating = _cursor.getInt(_cursorIndexOfBeerrating);
            _item = new Item(_tmpBeerid,_tmpBeername,_tmpBeerprice,_tmpBeerrating);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
