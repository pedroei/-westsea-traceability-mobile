package ipvc.estg.myapplication.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ipvc.estg.myapplication.Dao.ComponenteDao
import ipvc.estg.myapplication.models.Componente
import kotlinx.coroutines.flow.Flow


class ComponenteRepository(private val componenteDao: ComponenteDao) {

    val allComponents: Flow<List<Componente>> = componenteDao.getAllComponents()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getComponenteById(id: String): LiveData<Componente> {
        return componenteDao.getComponenteById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(componente: Componente) {
        componenteDao.insert(componente)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteById(id: String) {
        componenteDao.deleteById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateById(
        referenceNumber: String,
        id: String,
        quantidade: String,
        designationActivity: String,
        isSerialNumber: Boolean,
        designation: String,
        productType: String,
        initialAmount: Int
    ) {
        componenteDao.updateById(referenceNumber,id,quantidade,designationActivity,isSerialNumber,designation,productType,initialAmount)
    }


}