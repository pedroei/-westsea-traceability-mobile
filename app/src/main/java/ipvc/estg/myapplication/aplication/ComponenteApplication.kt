package ipvc.estg.myapplication.aplication

import android.app.Application
import ipvc.estg.myapplication.db.ComponenteRoomDatabase
import ipvc.estg.myapplication.repository.ComponenteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class ComponenteApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ComponenteRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ComponenteRepository(database.componenteDao()) }
}