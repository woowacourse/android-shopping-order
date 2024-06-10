package woowacourse.shopping.presentation.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.data.local.mapper.toCartProduct
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.toRecentProduct
import woowacourse.shopping.presentation.base.BaseViewModel
import woowacourse.shopping.presentation.common.ErrorType
import woowacourse.shopping.presentation.common.EventState
import woowacourse.shopping.presentation.common.UpdateUiModel
import woowacourse.shopping.presentation.ui.detail.model.DetailNavigation
import woowacourse.shopping.presentation.ui.detail.model.DetailUiState

class ProductDetailViewModel(
    private val cartItemRepository: CartItemRepository,
    private val recentProductRepository: RecentProductRepository,
) : BaseViewModel(), DetailActionHandler {
    private val _uiState = MutableLiveData<DetailUiState>(DetailUiState())
    val uiState: LiveData<DetailUiState> get() = _uiState

    private val _navigateHandler = MutableLiveData<EventState<DetailNavigation>>()
    val navigateHandler: LiveData<EventState<DetailNavigation>> get() = _navigateHandler

    private val _updateHandler = MutableLiveData<EventState<UpdateUiModel>>()
    val updateHandler: LiveData<EventState<UpdateUiModel>> get() = _updateHandler
    private val updateUiModel: UpdateUiModel = UpdateUiModel()

    fun setCartProduct(
        cartProduct: CartProduct,
        isLast: Boolean,
    ) = viewModelScope.launch {
        delay(500L) // skeleton
        val currentState = _uiState.value ?: return@launch
        _uiState.postValue(
            currentState.copy(
                isNewCartProduct = cartProduct.quantity == 0,
                cartProduct =
                    cartProduct.copy(
                        quantity = if (cartProduct.quantity == 0) 1 else cartProduct.quantity,
                    ),
                isLast = isLast,
                isLoading = false,
            ),
        )
        saveRecentProduct(cartProduct)
    }

    fun findOneRecentProduct() =
        viewModelScope.launch {
            recentProductRepository.findOrNull().onSuccess {
                val currentState = _uiState.value ?: return@launch
                _uiState.postValue(
                    currentState.copy(
                        recentProduct = it,
                    ),
                )
            }.onFailure {
                showError(ErrorType.ERROR_PRODUCT_LOAD)
            }
        }

    override fun onSaveCart(cartProduct: CartProduct) =
        viewModelScope.launch {
            val isFirstSave = _uiState.value?.isNewCartProduct ?: return@launch
            when (isFirstSave) {
                true -> {
                    cartItemRepository.post(CartItemRequest.fromCartProduct(cartProduct))
                        .onSuccess {
                            val currentState = _uiState.value ?: return@launch
                            saveRecentProduct(cartProduct.copy(cartId = it.toLong()))
                            updateUiModel.add(
                                cartProduct.productId,
                                cartProduct.copy(cartId = it.toLong()),
                            )
                            _uiState.postValue(
                                currentState.copy(
                                    cartProduct = cartProduct,
                                ),
                            )
                        }.onFailure {
                            showError(ErrorType.ERROR_PRODUCT_PLUS)
                        }
                }

                false -> {
                    cartItemRepository.patch(
                        id = cartProduct.cartId.toInt(),
                        quantityRequestDto =
                            QuantityRequest(
                                cartProduct.quantity,
                            ),
                    ).onSuccess {
                        val currentState = _uiState.value ?: return@launch
                        saveRecentProduct(cartProduct)
                        updateUiModel.add(
                            cartProduct.productId,
                            cartProduct,
                        )
                        _uiState.postValue(
                            currentState.copy(
                                cartProduct = cartProduct,
                            ),
                        )
                    }.onFailure {
                        showError(ErrorType.ERROR_PRODUCT_PLUS_MINUS)
                    }
                }
            }
            _updateHandler.postValue(EventState(updateUiModel))
        }

    override fun onNavigateToDetail(recentProduct: RecentProduct) {
        _navigateHandler.value = EventState(DetailNavigation.ToDetail(recentProduct.toCartProduct()))
    }

    override fun onPlus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentState = _uiState.value ?: return@launch
            cartProduct.plusQuantity()

            _uiState.postValue(
                currentState.copy(
                    cartProduct = cartProduct,
                ),
            )
        }

    override fun onMinus(cartProduct: CartProduct) =
        viewModelScope.launch {
            val currentState = _uiState.value ?: return@launch
            cartProduct.minusQuantity()

            _uiState.postValue(
                currentState.copy(
                    cartProduct = cartProduct,
                ),
            )
        }

    override fun saveRecentProduct(cartProduct: CartProduct) =
        viewModelScope.launch {
            recentProductRepository.save(cartProduct.toRecentProduct()).onFailure {
                showError(ErrorType.ERROR_RECENT_INSERT)
            }
        }
}
