package ipvc.estg.myapplication.models

import java.io.File

data class CreateProductLot(
    val referenceNumber: String,
    val isSerialNumber: Boolean,
    val designation: String,
    val productType: String,
    val initialAmount: Int,
    val documents: ArrayList<File>
)