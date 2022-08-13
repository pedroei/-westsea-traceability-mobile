package ipvc.estg.myapplication.models

import java.io.File

data class CreateActivity(
    val inputProductLots: HashMap<String, String>,
    val designation: String,
    val outputReferenceNumber: String,
    val outputIsSerialNumber: Boolean,
    val outputDesignation: String,
    val outputProductType: String,
    val outputInitialAmount: Int,
    val outputDocuments: ArrayList<File>
)
