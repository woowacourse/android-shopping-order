@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ApiViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.ui.ShoppingCartScreen
import woowacourse.shopping.ui.ShoppingCartState
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory
import woowacourse.shopping.ui.viewmodel.ShoppingCartItemViewModel

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
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        observeApiViewModel()
        observeScreenEvents()
        shoppingCartViewModel.requestCartItems()

        setContent {
            val screenState = shoppingCartItemViewModel.shoppingCartItems.collectAsStateWithLifecycle()
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
            val state =
                ShoppingCartState(
                    items = visibleItems,
                    isLoading = isLoading.value,
                    errorMessage = errorMessage.value,
                    currentPage = screenState.value.currentPage,
                    canMoveToPreviousPage = if (hasApiError) false else screenState.value.canMoveToPreviousPage,
                    canMoveToNextPage = if (hasApiError) false else screenState.value.canMoveToNextPage,
                )

            AndroidShoppingTheme {
                ShoppingCartScreen(
                    shoppingCartItems = visiblePagedItems,
                    getQuantityPrice = shoppingCartItemViewModel::getQuantityPrice,
                    state = state,
                    onBackClick = shoppingCartItemViewModel::onBackClick,
                    onRemoveShoppingItemClick = { shoppingCartItem ->
                        shoppingCartViewModel.removeShoppingItem(shoppingCartItem)
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
}
