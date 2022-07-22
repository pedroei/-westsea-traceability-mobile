package ipvc.estg.myapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "componente_table")
class Componente(

    @PrimaryKey
    val referenceNumber: String,
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "quantidade")
    val quantidade: String,
    @ColumnInfo(name = "designationActivity")
    val designationActivity: String,
    @ColumnInfo(name = "isSerialNumber")
    val isSerialNumber: Boolean,
    @ColumnInfo(name = "designation")
    val designation: String,
    @ColumnInfo(name = "productType")
    val productType: String,
    @ColumnInfo(name = "initialAmount")
    val initialAmount: Int

) : Serializable {

}





