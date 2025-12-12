package com.example.beerstack.`data`

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.beerstack.`data`.BeerDB.ItemDao
import com.example.beerstack.`data`.BeerDB.ItemDao_Impl
import com.example.beerstack.`data`.UserDB.UserDao
import com.example.beerstack.`data`.UserDB.UserDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _userDao: Lazy<UserDao> = lazy {
    UserDao_Impl(this)
  }

  private val _itemDao: Lazy<ItemDao> = lazy {
    ItemDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "a41b34d34ec0d713d7b1f03651bce028", "bcfbcce443444b25549a86a1e4cdcb81") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users` (`userid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userName` TEXT NOT NULL, `userPassword` TEXT NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `items` (`beerid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `beername` TEXT NOT NULL, `beerprice` INTEGER NOT NULL, `beerimage` TEXT, `beerrating` REAL NOT NULL, `beeraverage` REAL NOT NULL, `ownerId` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a41b34d34ec0d713d7b1f03651bce028')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `users`")
        connection.execSQL("DROP TABLE IF EXISTS `items`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsUsers: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsers.put("userid", TableInfo.Column("userid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("userName", TableInfo.Column("userName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsers.put("userPassword", TableInfo.Column("userPassword", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsers: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsers: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsers: TableInfo = TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers)
        val _existingUsers: TableInfo = read(connection, "users")
        if (!_infoUsers.equals(_existingUsers)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users(com.example.beerstack.data.UserDB.User).
              | Expected:
              |""".trimMargin() + _infoUsers + """
              |
              | Found:
              |""".trimMargin() + _existingUsers)
        }
        val _columnsItems: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsItems.put("beerid", TableInfo.Column("beerid", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("beername", TableInfo.Column("beername", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("beerprice", TableInfo.Column("beerprice", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("beerimage", TableInfo.Column("beerimage", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("beerrating", TableInfo.Column("beerrating", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("beeraverage", TableInfo.Column("beeraverage", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsItems.put("ownerId", TableInfo.Column("ownerId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysItems: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesItems: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoItems: TableInfo = TableInfo("items", _columnsItems, _foreignKeysItems, _indicesItems)
        val _existingItems: TableInfo = read(connection, "items")
        if (!_infoItems.equals(_existingItems)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |items(com.example.beerstack.data.BeerDB.Item).
              | Expected:
              |""".trimMargin() + _infoItems + """
              |
              | Found:
              |""".trimMargin() + _existingItems)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "users", "items")
  }

  public override fun clearAllTables() {
    super.performClear(false, "users", "items")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UserDao::class, UserDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ItemDao::class, ItemDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun userDao(): UserDao = _userDao.value

  public override fun itemDao(): ItemDao = _itemDao.value
}
