package woowacourse.shopping.presentation.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.presentation.SingleLiveData

class OrderViewModel : ViewModel() {
    private val _toastMessage = SingleLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    init {
        fetchData()
    }

    private fun fetchData() {
//        viewModelScope.launch {
//            recentProductRepository
//                .getMostRecentProduct()
//                .onSuccess { recentProduct ->
//                    recentCategory = recentProduct?.category ?: ""
//
//                    recommendProductsUseCase(recentCategory)
//                        .onSuccess { products ->
//                            val recommendItems = products.map { it.toPresentation() }
//                            _recommendProducts.value = recommendItems
//                        }.onFailure {
//                            _toastMessage.value = R.string.recommend_toast_load_fail
//                        }
//                }.onFailure {
//                    _toastMessage.value = R.string.recommend_toast_recent_load_fail
//                }
//        }
    }
}
