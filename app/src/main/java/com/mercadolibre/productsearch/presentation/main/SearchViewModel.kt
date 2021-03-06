package com.mercadolibre.productsearch.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadolibre.productsearch.data.Result
import com.mercadolibre.productsearch.data.repositories.ProductsRepository
import com.mercadolibre.productsearch.domain.entities.Product
import com.mercadolibre.productsearch.presentation.base.Status
import com.mercadolibre.productsearch.presentation.base.UiStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val repository: ProductsRepository) : ViewModel() {

    private var _productsLiveData = MutableLiveData<UiStatus<List<Product>>>()
    val productsLiveData: LiveData<UiStatus<List<Product>>>
        get() = _productsLiveData

    val favoritesLiveData = repository.getFavoritesProducts()

    fun addFavorite(product: Product) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.insertFavorite(product)
        }
    }

    fun getProducts(inputSearch: String) = viewModelScope.launch {
        _productsLiveData.value = UiStatus(Status.LOADING)

        withContext(Dispatchers.IO) { repository.requestProductsByString(inputSearch) }.let { result ->
            when (result) {
                is Result.Success -> {
                    _productsLiveData.value = UiStatus(status = Status.SUCCESS, data = result.data)
                }
                is Result.Failure -> {
                    _productsLiveData.value =
                        UiStatus(status = Status.ERROR, error = result.exception)
                }
            }
        }
    }
}
