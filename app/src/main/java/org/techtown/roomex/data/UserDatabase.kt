package org.techtown.roomex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.techtown.roomex.model.User

/* entities = 사용할 엔티티 선언, version = 엔티티 구조 변경 시 구분해주는 역할
   exportSchema = 스키마 내보내기 설정 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao() : UserDao
    companion object{
        /* @Volatile = 접근가능한 변수의 값을 cache를 통해 사용하지 않고
        thread가 직접 main memory에 접근 하게하여 동기화. */
        @Volatile
        private var instance : UserDatabase? = null
        var winner : User=User(0,"",0)
        // 싱글톤으로 생성 (자주 생성 시 성능 손해). 이미 존재할 경우 생성하지 않고 바로 반환
        fun getDatabase(context : Context) : UserDatabase? {
            if(instance == null){
                synchronized(UserDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    ).addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            fillInDb(context.applicationContext)
                        }
                    }).build()
                }
            }
            return instance
        }

        // 데이터 미리 채우기
        fun fillInDb(context: Context){
            CoroutineScope(Dispatchers.IO).launch {
                getDatabase(context)!!.userDao().addUserDb(
                    USER_DATA
                )
            }
        }
    }
}

private val USER_DATA = arrayListOf(
    User(0,"Han",20),
    User(0,"Lee",25)
)