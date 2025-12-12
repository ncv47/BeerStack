package com.example.beerstack.`data`.BeerDB

import androidx.collection.LongSparseArray
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.example.beerstack.`data`.UserDB.User
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ItemDao_Impl(
  __db: RoomDatabase,
) : ItemDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfItem: EntityInsertAdapter<Item>

  private val __deleteAdapterOfItem: EntityDeleteOrUpdateAdapter<Item>

  private val __updateAdapterOfItem: EntityDeleteOrUpdateAdapter<Item>
  init {
    this.__db = __db
    this.__insertAdapterOfItem = object : EntityInsertAdapter<Item>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `items` (`beerid`,`beername`,`beerprice`,`beerimage`,`beerrating`,`beeraverage`,`ownerId`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Item) {
        statement.bindLong(1, entity.beerid.toLong())
        statement.bindText(2, entity.beername)
        statement.bindLong(3, entity.beerprice.toLong())
        val _tmpBeerimage: String? = entity.beerimage
        if (_tmpBeerimage == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpBeerimage)
        }
        statement.bindDouble(5, entity.beerrating)
        statement.bindDouble(6, entity.beeraverage)
        statement.bindLong(7, entity.ownerId.toLong())
      }
    }
    this.__deleteAdapterOfItem = object : EntityDeleteOrUpdateAdapter<Item>() {
      protected override fun createQuery(): String = "DELETE FROM `items` WHERE `beerid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Item) {
        statement.bindLong(1, entity.beerid.toLong())
      }
    }
    this.__updateAdapterOfItem = object : EntityDeleteOrUpdateAdapter<Item>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `items` SET `beerid` = ?,`beername` = ?,`beerprice` = ?,`beerimage` = ?,`beerrating` = ?,`beeraverage` = ?,`ownerId` = ? WHERE `beerid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Item) {
        statement.bindLong(1, entity.beerid.toLong())
        statement.bindText(2, entity.beername)
        statement.bindLong(3, entity.beerprice.toLong())
        val _tmpBeerimage: String? = entity.beerimage
        if (_tmpBeerimage == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpBeerimage)
        }
        statement.bindDouble(5, entity.beerrating)
        statement.bindDouble(6, entity.beeraverage)
        statement.bindLong(7, entity.ownerId.toLong())
        statement.bindLong(8, entity.beerid.toLong())
      }
    }
  }

  public override suspend fun insert(item: Item): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfItem.insert(_connection, item)
  }

  public override suspend fun delete(item: Item): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfItem.handle(_connection, item)
  }

  public override suspend fun update(item: Item): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfItem.handle(_connection, item)
  }

  public override fun getItem(id: Int): Flow<Item> {
    val _sql: String = "SELECT * from items WHERE beerid = ?"
    return createFlow(__db, false, arrayOf("items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfBeerid: Int = getColumnIndexOrThrow(_stmt, "beerid")
        val _columnIndexOfBeername: Int = getColumnIndexOrThrow(_stmt, "beername")
        val _columnIndexOfBeerprice: Int = getColumnIndexOrThrow(_stmt, "beerprice")
        val _columnIndexOfBeerimage: Int = getColumnIndexOrThrow(_stmt, "beerimage")
        val _columnIndexOfBeerrating: Int = getColumnIndexOrThrow(_stmt, "beerrating")
        val _columnIndexOfBeeraverage: Int = getColumnIndexOrThrow(_stmt, "beeraverage")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _result: Item
        if (_stmt.step()) {
          val _tmpBeerid: Int
          _tmpBeerid = _stmt.getLong(_columnIndexOfBeerid).toInt()
          val _tmpBeername: String
          _tmpBeername = _stmt.getText(_columnIndexOfBeername)
          val _tmpBeerprice: Int
          _tmpBeerprice = _stmt.getLong(_columnIndexOfBeerprice).toInt()
          val _tmpBeerimage: String?
          if (_stmt.isNull(_columnIndexOfBeerimage)) {
            _tmpBeerimage = null
          } else {
            _tmpBeerimage = _stmt.getText(_columnIndexOfBeerimage)
          }
          val _tmpBeerrating: Double
          _tmpBeerrating = _stmt.getDouble(_columnIndexOfBeerrating)
          val _tmpBeeraverage: Double
          _tmpBeeraverage = _stmt.getDouble(_columnIndexOfBeeraverage)
          val _tmpOwnerId: Int
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId).toInt()
          _result = Item(_tmpBeerid,_tmpBeername,_tmpBeerprice,_tmpBeerimage,_tmpBeerrating,_tmpBeeraverage,_tmpOwnerId)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.example.beerstack.`data`.BeerDB.Item'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllUsersWithBeer(): Flow<List<UserwithBeer>> {
    val _sql: String = "SELECT * FROM users ORDER BY userName ASC"
    return createFlow(__db, true, arrayOf("items", "users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfUserid: Int = getColumnIndexOrThrow(_stmt, "userid")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserPassword: Int = getColumnIndexOrThrow(_stmt, "userPassword")
        val _collectionLibrary: LongSparseArray<MutableList<Item>> = LongSparseArray<MutableList<Item>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfUserid)
          if (!_collectionLibrary.containsKey(_tmpKey)) {
            _collectionLibrary.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipitemsAscomExampleBeerstackDataBeerDBItem(_connection, _collectionLibrary)
        val _result: MutableList<UserwithBeer> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserwithBeer
          val _tmpUser: User
          val _tmpUserid: Int
          _tmpUserid = _stmt.getLong(_columnIndexOfUserid).toInt()
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserPassword: String
          _tmpUserPassword = _stmt.getText(_columnIndexOfUserPassword)
          _tmpUser = User(_tmpUserid,_tmpUserName,_tmpUserPassword)
          val _tmpLibraryCollection: MutableList<Item>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfUserid)
          _tmpLibraryCollection = checkNotNull(_collectionLibrary.get(_tmpKey_1))
          _item = UserwithBeer(_tmpUser,_tmpLibraryCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getItemsByOwner(ownerId: Int): Flow<List<Item>> {
    val _sql: String = "SELECT * FROM items WHERE ownerId = ?"
    return createFlow(__db, false, arrayOf("items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, ownerId.toLong())
        val _columnIndexOfBeerid: Int = getColumnIndexOrThrow(_stmt, "beerid")
        val _columnIndexOfBeername: Int = getColumnIndexOrThrow(_stmt, "beername")
        val _columnIndexOfBeerprice: Int = getColumnIndexOrThrow(_stmt, "beerprice")
        val _columnIndexOfBeerimage: Int = getColumnIndexOrThrow(_stmt, "beerimage")
        val _columnIndexOfBeerrating: Int = getColumnIndexOrThrow(_stmt, "beerrating")
        val _columnIndexOfBeeraverage: Int = getColumnIndexOrThrow(_stmt, "beeraverage")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _result: MutableList<Item> = mutableListOf()
        while (_stmt.step()) {
          val _item: Item
          val _tmpBeerid: Int
          _tmpBeerid = _stmt.getLong(_columnIndexOfBeerid).toInt()
          val _tmpBeername: String
          _tmpBeername = _stmt.getText(_columnIndexOfBeername)
          val _tmpBeerprice: Int
          _tmpBeerprice = _stmt.getLong(_columnIndexOfBeerprice).toInt()
          val _tmpBeerimage: String?
          if (_stmt.isNull(_columnIndexOfBeerimage)) {
            _tmpBeerimage = null
          } else {
            _tmpBeerimage = _stmt.getText(_columnIndexOfBeerimage)
          }
          val _tmpBeerrating: Double
          _tmpBeerrating = _stmt.getDouble(_columnIndexOfBeerrating)
          val _tmpBeeraverage: Double
          _tmpBeeraverage = _stmt.getDouble(_columnIndexOfBeeraverage)
          val _tmpOwnerId: Int
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId).toInt()
          _item = Item(_tmpBeerid,_tmpBeername,_tmpBeerprice,_tmpBeerimage,_tmpBeerrating,_tmpBeeraverage,_tmpOwnerId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __fetchRelationshipitemsAscomExampleBeerstackDataBeerDBItem(_connection: SQLiteConnection, _map: LongSparseArray<MutableList<Item>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipitemsAscomExampleBeerstackDataBeerDBItem(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `beerid`,`beername`,`beerprice`,`beerimage`,`beerrating`,`beeraverage`,`ownerId` FROM `items` WHERE `ownerId` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "ownerId")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfBeerid: Int = 0
      val _columnIndexOfBeername: Int = 1
      val _columnIndexOfBeerprice: Int = 2
      val _columnIndexOfBeerimage: Int = 3
      val _columnIndexOfBeerrating: Int = 4
      val _columnIndexOfBeeraverage: Int = 5
      val _columnIndexOfOwnerId: Int = 6
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<Item>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: Item
          val _tmpBeerid: Int
          _tmpBeerid = _stmt.getLong(_columnIndexOfBeerid).toInt()
          val _tmpBeername: String
          _tmpBeername = _stmt.getText(_columnIndexOfBeername)
          val _tmpBeerprice: Int
          _tmpBeerprice = _stmt.getLong(_columnIndexOfBeerprice).toInt()
          val _tmpBeerimage: String?
          if (_stmt.isNull(_columnIndexOfBeerimage)) {
            _tmpBeerimage = null
          } else {
            _tmpBeerimage = _stmt.getText(_columnIndexOfBeerimage)
          }
          val _tmpBeerrating: Double
          _tmpBeerrating = _stmt.getDouble(_columnIndexOfBeerrating)
          val _tmpBeeraverage: Double
          _tmpBeeraverage = _stmt.getDouble(_columnIndexOfBeeraverage)
          val _tmpOwnerId: Int
          _tmpOwnerId = _stmt.getLong(_columnIndexOfOwnerId).toInt()
          _item_1 = Item(_tmpBeerid,_tmpBeername,_tmpBeerprice,_tmpBeerimage,_tmpBeerrating,_tmpBeeraverage,_tmpOwnerId)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
