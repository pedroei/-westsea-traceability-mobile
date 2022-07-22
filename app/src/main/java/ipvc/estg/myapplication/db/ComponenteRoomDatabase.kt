package ipvc.estg.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ipvc.estg.myapplication.Dao.ComponenteDao
import ipvc.estg.myapplication.models.Componente
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Componente::class], version=10, exportSchema = false)
abstract class ComponenteRoomDatabase: RoomDatabase() {

    abstract fun componenteDao() : ComponenteDao

    private class ComponenteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
//                        database.componenteDao().deleteAll()

                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

//                    database.componenteDao().deleteAll()
                }
            }
        }
    }


    //forma de chamar funções sem instanciar a própria classe
    companion object{
        //singleton
        @Volatile
        private var INSTANCE: ComponenteRoomDatabase? = null;

        //create singleton
        fun getDatabase(context: Context, scope: CoroutineScope):ComponenteRoomDatabase {
            return INSTANCE ?: synchronized(this){

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ComponenteRoomDatabase::class.java,
                    "componente_database"
                )
                    // estratégia de destruição
                    .fallbackToDestructiveMigration()
                    .addCallback(ComponenteDatabaseCallback(scope))
                    .build()

                INSTANCE = instance

                return instance
            }
        }


    }

}