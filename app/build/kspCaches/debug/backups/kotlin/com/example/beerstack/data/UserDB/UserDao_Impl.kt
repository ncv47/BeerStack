package com.example.beerstack.`data`.UserDB

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUser: EntityInsertAdapter<User>

  private val __deleteAdapterOfUser: EntityDeleteOrUpdateAdapter<User>

  private val __updateAdapterOfUser: EntityDeleteOrUpdateAdapter<User>
  init {
    this.__db = __db
    this.__insertAdapterOfUser = object : EntityInsertAdapter<User>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `users` (`userid`,`userName`,`userPassword`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindLong(1, entity.userid.toLong())
        statement.bindText(2, entity.userName)
        statement.bindText(3, entity.userPassword)
      }
    }
    this.__deleteAdapterOfUser = object : EntityDeleteOrUpdateAdapter<User>() {
      protected override fun createQuery(): String = "DELETE FROM `users` WHERE `userid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindLong(1, entity.userid.toLong())
      }
    }
    this.__updateAdapterOfUser = object : EntityDeleteOrUpdateAdapter<User>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `users` SET `userid` = ?,`userName` = ?,`userPassword` = ? WHERE `userid` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: User) {
        statement.bindLong(1, entity.userid.toLong())
        statement.bindText(2, entity.userName)
        statement.bindText(3, entity.userPassword)
        statement.bindLong(4, entity.userid.toLong())
      }
    }
  }

  public override suspend fun insert(item: User): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfUser.insert(_connection, item)
  }

  public override suspend fun delete(item: User): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfUser.handle(_connection, item)
  }

  public override suspend fun update(item: User): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfUser.handle(_connection, item)
  }

  public override fun getItem(id: Int): Flow<User> {
    val _sql: String = "SELECT * from users WHERE userid = ?"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _columnIndexOfUserid: Int = getColumnIndexOrThrow(_stmt, "userid")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserPassword: Int = getColumnIndexOrThrow(_stmt, "userPassword")
        val _result: User
        if (_stmt.step()) {
          val _tmpUserid: Int
          _tmpUserid = _stmt.getLong(_columnIndexOfUserid).toInt()
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserPassword: String
          _tmpUserPassword = _stmt.getText(_columnIndexOfUserPassword)
          _result = User(_tmpUserid,_tmpUserName,_tmpUserPassword)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.example.beerstack.`data`.UserDB.User'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllItems(): Flow<List<User>> {
    val _sql: String = "SELECT * from users ORDER BY userName ASC"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfUserid: Int = getColumnIndexOrThrow(_stmt, "userid")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserPassword: Int = getColumnIndexOrThrow(_stmt, "userPassword")
        val _result: MutableList<User> = mutableListOf()
        while (_stmt.step()) {
          val _item: User
          val _tmpUserid: Int
          _tmpUserid = _stmt.getLong(_columnIndexOfUserid).toInt()
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserPassword: String
          _tmpUserPassword = _stmt.getText(_columnIndexOfUserPassword)
          _item = User(_tmpUserid,_tmpUserName,_tmpUserPassword)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun login(username: String, password: String): User? {
    val _sql: String = "SELECT * FROM users WHERE userName = ? AND userPassword = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, username)
        _argIndex = 2
        _stmt.bindText(_argIndex, password)
        val _columnIndexOfUserid: Int = getColumnIndexOrThrow(_stmt, "userid")
        val _columnIndexOfUserName: Int = getColumnIndexOrThrow(_stmt, "userName")
        val _columnIndexOfUserPassword: Int = getColumnIndexOrThrow(_stmt, "userPassword")
        val _result: User?
        if (_stmt.step()) {
          val _tmpUserid: Int
          _tmpUserid = _stmt.getLong(_columnIndexOfUserid).toInt()
          val _tmpUserName: String
          _tmpUserName = _stmt.getText(_columnIndexOfUserName)
          val _tmpUserPassword: String
          _tmpUserPassword = _stmt.getText(_columnIndexOfUserPassword)
          _result = User(_tmpUserid,_tmpUserName,_tmpUserPassword)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
