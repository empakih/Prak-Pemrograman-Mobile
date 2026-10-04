package com.pemmob.mfqh.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.mfqh.data.model.Category
import com.pemmob.mfqh.data.model.Product
import com.pemmob.mfqh.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ProductUiState {
    object Loading : ProductUiState
    data class Success(val categories: List<Category>, val products: List<Product>) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

class ProductViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                _uiState.value = ProductUiState.Loading
                val categories = ApiClient.instance.getCategories()
                val products = ApiClient.instance.getProducts().map { product ->
                    val matchedCategory = categories.find { it.id == product.category_id }
                    product.copy(category = matchedCategory)
                }
                _uiState.value = ProductUiState.Success(
                    categories = categories,
                    products = products
                )
            } catch (e: Exception) {
                _uiState.value = ProductUiState.Error(
                    message = e.message ?: "Terjadi kesalahan saat mengambil data"
                )
            }
        }
    }
}
