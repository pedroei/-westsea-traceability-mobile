package ipvc.estg.myapplication.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import ipvc.estg.myapplication.models.Componente
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponenteDao {

    @Query("Select * from componente_table")
    fun getAllComponents(): Flow<List<Componente>>

    @Insert(onConflict = IGNORE)
    suspend fun insert(componente: Componente)

    @Query("Delete from componente_table")
    fun deleteAll()

    @Query("Select * from componente_table Where referenceNumber = :id")
    fun getComponenteById(id: String): LiveData<Componente>

    @Query("Delete from componente_table Where referenceNumber = :id")
    fun deleteById(id: String)

    @Query("Update componente_table SET id=:id, quantidade=:quantidade, designationActivity=:designationActivity, isSerialNumber=:isSerialNumber, designation=:designation, productType=:productType, initialAmount=:initialAmount  Where referenceNumber = :referenceNumber")
    fun updateById(
        referenceNumber: String,
        id: String,
        quantidade: String,
        designationActivity: String,
        isSerialNumber: Boolean,
        designation: String,
        productType: String,
        initialAmount: Int
    )
}