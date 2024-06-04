package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.Repository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.ErrorType
import woowacourse.shopping.presentation.ui.EventState
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.UpdateUiModel
import woowacourse.shopping.presentation.ui.detail.model.DetailCartProduct
import kotlin.concurrent.thread

class ProductDetailViewModel(
    private val repository: Repository,
) : ViewModel(), DetailActionHandler {
    private val _cartProduct = MutableLiveData<UiState<DetailCartProduct>>(UiState.Loading)
    val cartProduct: LiveData<UiState<DetailCartProduct>> get() = _cartProduct

    private val _recentProduct = MutableLiveData<UiState<RecentProduct>>(UiState.Loading)
    val recentProduct: LiveData<UiState<RecentProduct>> get() = _recentProduct

    private val _errorHandler = MutableLiveData<EventState<ErrorType>>()
    val errorHandler: LiveData<EventState<ErrorType>> get() = _errorHandler

    private val _cartHandler = MutableLiveData<EventState<UpdateUiModel>>()
    val cartHandler: LiveData<EventState<UpdateUiModel>> get() = _cartHandler

    private val _navigateHandler = MutableLiveData<EventState<CartProduct>>()
    val navigateHandler: LiveData<EventState<CartProduct>> get() = _navigateHandler

    private val updateUiModel: UpdateUiModel = UpdateUiModel()

    fun setCartProduct(cartProduct: CartProduct) =
        thread {
            saveRecentProduct(cartProduct)
            _cartProduct.postValue(UiState.Success(DetailCartProduct.fromCartProduct(cartProduct)))
        }

    fun findOneRecentProduct() =
        thread {
            repository.findOneRecent().onSuccess {
                if (it == null) {
                    _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_LOAD))
                } else {
                    _recentProduct.postValue(UiState.Success(it))
                }
            }.onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_LOAD))
            }
        }

    override fun onAddToCart(detailCartProduct: DetailCartProduct) {
        thread {
            when (detailCartProduct.isNew) {
                true -> {
                    repository.postCartItem(CartItemRequest.fromCartProduct(detailCartProduct.cartProduct))
                        .onSuccess {
                            _cartProduct.postValue(UiState.Success(detailCartProduct))
                            saveRecentProduct(detailCartProduct.cartProduct)
                        }.onFailure {
                            _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS))
                        }
                }

                false -> {
                    repository.patchCartItem(
                        id = detailCartProduct.cartProduct.cartId.toInt(),
                        quantityRequest =
                            QuantityRequest(
                                detailCartProduct.cartProduct.quantity,
                            ),
                    ).onSuccess {
                        _cartProduct.postValue(UiState.Success(detailCartProduct))
                        saveRecentProduct(detailCartProduct.cartProduct)
                    }.onFailure {
                        _errorHandler.postValue(EventState(ErrorType.ERROR_PRODUCT_PLUS_MINUS))
                    }
                }
            }
            updateUiModel.add(
                detailCartProduct.cartProduct.productId,
                detailCartProduct.cartProduct,
            )
            _cartHandler.postValue(EventState(updateUiModel))
        }
    }

    override fun onNavigateToDetail(recentProduct: RecentProduct) {
        _navigateHandler.value = EventState(recentProduct.toCartProduct())
    }

    override fun onPlus(cartProduct: CartProduct) {
        thread {
            cartProduct.plusQuantity()
            _cartProduct.postValue(
                UiState.Success(
                    (_cartProduct.value as UiState.Success).data.copy(cartProduct = cartProduct),
                ),
            )
        }
    }

    override fun onMinus(cartProduct: CartProduct) {
        thread {
            cartProduct.minusQuantity()
            _cartProduct.postValue(
                UiState.Success(
                    (_cartProduct.value as UiState.Success).data.copy(cartProduct = cartProduct),
                ),
            )
        }
    }

    override fun saveRecentProduct(cartProduct: CartProduct) {
        thread {
            repository.saveRecentProduct(cartProduct.toRecentProduct()).onFailure {
                _errorHandler.postValue(EventState(ErrorType.ERROR_RECENT_INSERT))
            }
        }
    }
}
