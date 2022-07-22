package ipvc.estg.myapplication.models

data class CreateActivity(
    val inputProductLots : HashMap<String,String>,
    val designation: String,
    val outputProductLot : OutputProductLot
)
data class OutputProductLot(
    val referenceNumber :String,
    val isSerialNumber :Boolean,
    val designation :String,
    val productType :String,
    val initialAmount :Int,
    val documentKeys :ArrayList<String>
)

