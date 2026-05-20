@file:Suppress("FunctionName")

package woowacourse.shopping.ui.cart

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
import woowacourse.shopping.di.AppViewModelFactory
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendSection
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel
import woowacourse.shopping.ui.recommend.ShoppingCartRecommendViewModel.ShoppingCartStep

@OptIn(ExperimentalMaterial3Api::class)
class ShoppingCartActivity : ComponentActivity() {
    private val app: ShoppingApplication by lazy { application as ShoppingApplication }

    private val viewModelFactory: AppViewModelFactory by lazy {
        AppViewModelFactory(
            appContainer = app.appContainer,
        )
    }

    private val shoppingCartRecommendViewModel: ShoppingCartRecommendViewModel by viewModels { viewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { viewModelFactory }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeScreenEvents()

        setContent {
            val shoppingCartItems by shoppingCartViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val selectedProductIds by shoppingCartViewModel.selectedProductIds.collectAsStateWithLifecycle()
            val recommendUiState by shoppingCartRecommendViewModel.uiState.collectAsStateWithLifecycle()

            val screenState =
                shoppingCartViewModel.screenState.collectAsStateWithLifecycle()
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
            val selectableCartProductIds =
                visibleItems
                    .map { shoppingCartItem -> shoppingCartItem.product.id }
                    .toSet()
            val selectedVisibleProductIds = selectedProductIds.intersect(selectableCartProductIds)
            val selectedItemCount = selectedVisibleProductIds.size
            val selectedCartTotalPrice =
                shoppingCartItems
                    .filter { shoppingCartItem -> shoppingCartItem.product.id in selectedVisibleProductIds }
                    .sumOf { shoppingCartItem -> shoppingCartItem.getProductQuantityPrice() }
            LaunchedEffect(shoppingCartItems, selectedProductIds) {
                shoppingCartRecommendViewModel.updateCartSnapshot(
                    shoppingCartItems = shoppingCartItems,
                    selectedCartProductIds = selectedVisibleProductIds,
                )
            }
            val state =
                ShoppingCartState(
                    items = visibleItems,
                    selectedProductIds = selectedVisibleProductIds,
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
                BackHandler(enabled = recommendUiState.currentStep == ShoppingCartStep.RECOMMEND) {
                    shoppingCartRecommendViewModel.moveToCart()
                }
                if (recommendUiState.currentStep == ShoppingCartStep.CART) {
                    ShoppingCartScreen(
                        shoppingCartItems = visiblePagedItems,
                        getQuantityPrice = shoppingCartViewModel::getQuantityPrice,
                        state = state,
                        onBackClick = shoppingCartViewModel::onBackClick,
                        onRemoveShoppingItemClick = { shoppingCartItem ->
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
                            onBeforePageClick = shoppingCartViewModel::moveToPreviousPage,
                            onNextPageClick = shoppingCartViewModel::moveToNextPage,
                        )
                        OrderButton(
                            shoppingCartItems = visibleItems,
                            selectedProductIds = selectedVisibleProductIds,
                            shoppingCartSelectItemCount = selectedItemCount,
                            onOrderButtonClick = { selectedIds ->
                                if (
                                    selectedIds.isEmpty() ||
                                    recommendUiState.recommendedShoppingItems.isEmpty()
                                ) {
                                    return@OrderButton
                                }
                                shoppingCartRecommendViewModel.moveToRecommend(
                                    baseCartItems = shoppingCartItems,
                                    baseSelectedCartProductIds = selectedVisibleProductIds,
                                )
                            },
                            checked =
                                selectableCartProductIds.isNotEmpty() &&
                                    selectedVisibleProductIds.size == selectableCartProductIds.size,
                            orderComplete = visibleItems.isNotEmpty(),
                            totalPrice = selectedCartTotalPrice,
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
                        baseSelectedCartItemCount = recommendUiState.baseSelectedCartItemCount,
                        totalPrice = recommendUiState.selectedCartTotalPrice + recommendUiState.selectedRecommendTotalPrice,
                        onBackClick = shoppingCartViewModel::onBackClick,
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

    private fun observeScreenEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shoppingCartViewModel.event.collect { event ->
                    when (event) {
                        ShoppingCartViewModel.ShoppingCartEvent.NavigateBack -> finish()
                    }
                }
            }
        }
    }
}
