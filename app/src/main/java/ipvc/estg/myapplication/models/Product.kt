package ipvc.estg.myapplication.models

data class Product(
    val docType :String,
    val referenceNumber: String,
    val isSerialNumber: Boolean,
    val designation: String,
    val productType: String,
    val initialQuantity: Int,
    val availableQuantity: Int,
    val documentKeys: ArrayList<String>,
    val ID: String
)
