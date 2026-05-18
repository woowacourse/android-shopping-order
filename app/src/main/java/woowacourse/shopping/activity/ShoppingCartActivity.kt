@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ApiViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.recomment.ShoppingCartRecommendSection
import woowacourse.shopping.ui.screen.OrderButton
import woowacourse.shopping.ui.screen.ShoppingCartScreen
import woowacourse.shopping.ui.state.ShoppingCartState
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory
import woowacourse.shopping.ui.viewmodel.ShoppingCartItemViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingCartRecommendViewModel
import woowacourse.shopping.ui.viewmodel.ShoppingCartRecommendViewModel.ShoppingCartStep

@OptIn(ExperimentalMaterial3Api::class)
class ShoppingCartActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(appContainer = app.appContainer)
    }
    private val apiViewModelFactory: ApiViewModelFactory by lazy {
        ApiViewModelFactory(app.retrofitService)
    }

    private val shoppingCartItemViewModel: ShoppingCartItemViewModel by viewModels { screenViewModelFactory }
    private val shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel by viewModels { screenViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeApiViewModel()
        observeScreenEvents()
        shoppingCartViewModel.requestCartItems()

        setContent {
            val shoppingCartItems by shoppingCartViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val selectedProductIds by shoppingCartViewModel.selectedProductIds.collectAsStateWithLifecycle()
            val selectedItemCount = selectedProductIds.size
            val recommendUiState by shoppingCartRecommendViewModel.uiState.collectAsStateWithLifecycle()

            val screenState =
                shoppingCartItemViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val isLoading = shoppingCartViewModel.isLoading.collectAsStateWithLifecycle()
            val errorMessage = shoppingCartViewModel.errorMessage.collectAsStateWithLifecycle()
            val hasApiError = errorMessage.value != null
            val visibleItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    screenState.value.items
                }
            val visiblePagedItems =
                if (hasApiError) {
                    emptyList()
                } else {
                    screenState.value.pagedItems
                }
            LaunchedEffect(shoppingCartItems, selectedProductIds) {
                shoppingCartRecommendViewModel.updateCartSnapshot(
                    shoppingCartItems = shoppingCartItems,
                    selectedCartProductIds = selectedProductIds,
                )
            }
            val state =
                ShoppingCartState(
                    items = visibleItems,
                    selectedProductIds = selectedProductIds,
                    isLoading = isLoading.value,
                    errorMessage = errorMessage.value,
                    currentPage = screenState.value.currentPage,
                    selectedItemCount = selectedItemCount,
                    canOrder = selectedItemCount > 0 && !isLoading.value,
                    canMoveToPreviousPage =
                        if (hasApiError) false else screenState.value.canMoveToPreviousPage,
                    canMoveToNextPage = if (hasApiError) false else screenState.value.canMoveToNextPage,
                )

            AndroidShoppingTheme {
                BackHandler(enabled = recommendUiState.currentStep == ShoppingCartStep.RECOMMENT) {
                    shoppingCartRecommendViewModel.moveToCart()
                }
                if (recommendUiState.currentStep == ShoppingCartStep.CART) {
                    ShoppingCartScreen(
                        shoppingCartItems = visiblePagedItems,
                        getQuantityPrice = shoppingCartItemViewModel::getQuantityPrice,
                        state = state,
                        onBackClick = shoppingCartItemViewModel::onBackClick,
                        onRemoveShoppingItemClick = { shoppingCartItem ->
                            shoppingCartItemViewModel.removeShoppingItem(shoppingCartItem)
                            shoppingCartViewModel.removeShoppingItem(shoppingCartItem)
                        },
                        onToggleShoppingItemSelectionClick = { productId, isSelected ->
                            shoppingCartViewModel.setShoppingCartProductSelection(
                                productId = productId,
                                isSelected = isSelected,
                            )
                        },
                        onIncreaseShoppingItemQuantityClick = { shoppingCartItem ->
                            shoppingCartViewModel.increaseShoppingItemQuantity(shoppingCartItem)
                        },
                        onDecreaseShoppingItemQuantityClick = { shoppingCartItem ->
                            shoppingCartViewModel.decreaseShoppingItemQuantity(shoppingCartItem)
                        },
                    ) {
                        PageNavigation(
                            currentPage = screenState.value.currentPage,
                            canMoveToPreviousPage = if (hasApiError) false else screenState.value.canMoveToPreviousPage,
                            canMoveToNextPage = if (hasApiError) false else screenState.value.canMoveToNextPage,
                            onBeforePageClick = shoppingCartItemViewModel::moveToPreviousPage,
                            onNextPageClick = shoppingCartItemViewModel::moveToNextPage,
                        )
                        OrderButton(
                            shoppingCartItems = shoppingCartItems,
                            selectedProductIds = selectedProductIds,
                            shoppingCartSelectItemCount = selectedItemCount,
                            onOrderButtonClick = { selectedIds ->
                                if (
                                    selectedIds.isEmpty() ||
                                    recommendUiState.recommendedShoppingItems.isEmpty()
                                ) {
                                    return@OrderButton
                                }
                                shoppingCartRecommendViewModel.moveToRecommend()
                            },
                            checked = shoppingCartItems.isNotEmpty() && selectedItemCount == shoppingCartItems.size,
                            orderComplete = shoppingCartItems.isNotEmpty(),
                            totalPrice = shoppingCartViewModel.getTotalPrice(shoppingCartItems),
                            onToggleShoppingItemSelectionClick = { productIds, isSelected ->
                                shoppingCartViewModel.setShoppingCartProductsSelection(
                                    productIds = productIds,
                                    isSelected = isSelected,
                                )
                            },
                        )
                    }
                } else {
                    ShoppingCartRecommendSection(
                        recommendedShoppingItems = recommendUiState.recommendedShoppingItems,
                        baseSelectedCartItemCount = selectedProductIds.size,
                        totalPrice = recommendUiState.selectedCartTotalPrice + recommendUiState.selectedRecommendTotalPrice,
                        onBackClick = shoppingCartItemViewModel::onBackClick,
                        onOrderButtonClick = {},
                        onAddToCartClick = { shoppingItem ->
                            shoppingCartViewModel.addOrIncreaseByProductId(
                                productId = shoppingItem.getProductId(),
                                amount = 1,
                            )
                        },
                        onQuantityPlusClick = { shoppingItem ->
                            shoppingCartViewModel.addOrIncreaseByProductId(
                                productId = shoppingItem.getProductId(),
                                amount = 1,
                            )
                        },
                        onQuantityMinusClick = { shoppingItem ->
                            shoppingCartViewModel.decreaseByProductId(
                                productId = shoppingItem.getProductId(),
                            )
                        },
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shoppingCartViewModel.requestCartItems()
    }

    private fun observeApiViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shoppingCartViewModel.shoppingCartItems.collect { shoppingCartItems ->
                    app.appContainer.remoteShoppingStateSyncer.syncCartItems(shoppingCartItems)
                    shoppingCartItemViewModel.refresh()
                }
            }
        }
    }

    private fun observeScreenEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shoppingCartItemViewModel.event.collect { event ->
                    when (event) {
                        ShoppingCartItemViewModel.ShoppingCartEvent.NavigateBack -> finish()
                    }
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
