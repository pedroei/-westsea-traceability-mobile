package ipvc.estg.myapplication.viewModel

import androidx.lifecycle.*
import ipvc.estg.myapplication.models.Componente
import ipvc.estg.myapplication.repository.ComponenteRepository
import kotlinx.coroutines.launch


class ComponenteViewModel(private val repository: ComponenteRepository) : ViewModel() {

    val allComponents: LiveData<List<Componente>> = repository.allComponents.asLiveData()


    fun getComponenteById(id: String): LiveData<Componente> {
        return repository.getComponenteById(id)
    }


    fun insert(componente: Componente) = viewModelScope.launch {
        repository.insert(componente)
    }

    suspend fun deleteById(id: String) {
        repository.deleteById(id)
    }

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
        repository.updateById(referenceNumber,id,quantidade,designationActivity,isSerialNumber,designation,productType,initialAmount)
    }

}

class ComponenteViewModelFactory(private val repository: ComponenteRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ComponenteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ComponenteViewModel(repository) as T

        }
        throw IllegalAccessException("Unknown view Model Class");
    }

}
