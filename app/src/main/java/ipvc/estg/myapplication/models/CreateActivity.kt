package ipvc.estg.myapplication.models

data class CreateActivity(
    val inputProductLots: HashMap<String, String>,
    val designation: String,
    val outputProductLot: CreateProductLot
)
