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
        ScreenViewModelFactory(
            appContainer = app.appContainer,
            retrofitService = app.retrofitService
        )
    }

    private val shoppingCartItemViewModel: ShoppingCartItemViewModel by viewModels { screenViewModelFactory }
    private val shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel by viewModels { screenViewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeScreenEvents()

        setContent {
            val shoppingCartItems by shoppingCartItemViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val selectedProductIds by shoppingCartItemViewModel.selectedProductIds.collectAsStateWithLifecycle()
            val selectedItemCount = selectedProductIds.size
            val recommendUiState by shoppingCartRecommendViewModel.uiState.collectAsStateWithLifecycle()

            val screenState =
                shoppingCartItemViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val uiState by shoppingCartItemViewModel.uiState.collectAsStateWithLifecycle()
            val isLoading = uiState.isLoading
            val errorMessage = uiState.errorMessage
            val hasApiError = errorMessage != null
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
                    shoppingCartItems = shoppingCartItems.items,
                    selectedCartProductIds = selectedProductIds,
                )
            }
            val state =
                ShoppingCartState(
                    items = visibleItems,
                    selectedProductIds = selectedProductIds,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    currentPage = screenState.value.currentPage,
                    selectedItemCount = selectedItemCount,
                    canOrder = selectedItemCount > 0 && !isLoading,
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
                        },
                        onToggleShoppingItemSelectionClick = { productId, isSelected ->
                            shoppingCartItemViewModel.setShoppingCartProductSelection(
                                productId = productId,
                                isSelected = isSelected,
                            )
                        },
                        onIncreaseShoppingItemQuantityClick = { shoppingCartItem ->
                            shoppingCartItemViewModel.increaseShoppingItemQuantity(shoppingCartItem)
                        },
                        onDecreaseShoppingItemQuantityClick = { shoppingCartItem ->
                            shoppingCartItemViewModel.decreaseShoppingItemQuantity(shoppingCartItem)
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
                            shoppingCartItems = shoppingCartItems.items,
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
                            checked = shoppingCartItems.items.isNotEmpty() && selectedItemCount == shoppingCartItems.items.size,
                            orderComplete = shoppingCartItems.items.isNotEmpty(),
                            totalPrice = shoppingCartItemViewModel.getTotalPrice(
                                shoppingCartItems = screenState.value.items,
                                selectedProductIds = selectedProductIds,
                            ),
                            onToggleShoppingItemSelectionClick = { productIds, isSelected ->
                                shoppingCartItemViewModel.setShoppingCartProductsSelection(
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
                            shoppingCartItemViewModel.addOrIncreaseByProductId(
                                productId = shoppingItem.getProductId(),
                            )
                        },
                        onQuantityPlusClick = { shoppingItem ->
                            shoppingCartItemViewModel.addOrIncreaseByProductId(
                                productId = shoppingItem.getProductId(),
                            )
                        },
                        onQuantityMinusClick = { shoppingItem ->
                            shoppingCartItemViewModel.decreaseByProductId(
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
        shoppingCartItemViewModel.requestCartItems()
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
