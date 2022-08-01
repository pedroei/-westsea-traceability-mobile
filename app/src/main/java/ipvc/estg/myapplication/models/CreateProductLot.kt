package ipvc.estg.myapplication.models

data class CreateProductLot(
    val referenceNumber: String,
    val isSerialNumber: Boolean,
    val designation: String,
    val productType: String,
    val initialAmount: Int,
    val documentKeys: ArrayList<String>
)