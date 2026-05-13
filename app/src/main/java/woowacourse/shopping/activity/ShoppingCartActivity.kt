@file:Suppress("FunctionName")

package woowacourse.shopping.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.ApiViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.OrderViewModel
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.mapper.toOrderInfo
import woowacourse.shopping.model.ShoppingCartItem
import woowacourse.shopping.ui.ShoppingCartScreen
import woowacourse.shopping.ui.component.OrderButton
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.ui.viewmodel.ScreenViewModelFactory
import woowacourse.shopping.ui.viewmodel.ShoppingCartItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
class ShoppingCartActivity : ComponentActivity() {
    private val appContainer by lazy { (application as ShoppingApplication).appContainer }

    private val screenViewModelFactory: ScreenViewModelFactory by lazy {
        ScreenViewModelFactory(
            shoppingCartRepository = appContainer.shoppingCartRepository,
            shoppingItemRepository = appContainer.shoppingItemRepository,
            visitStore = appContainer.visitStore,
            networkStatusMonitor = appContainer.networkStatusMonitor,
        )
    }
    private val apiViewModelFactory: ApiViewModelFactory by lazy { ApiViewModelFactory() }
    private val shoppingCartItemViewModel: ShoppingCartItemViewModel by viewModels { screenViewModelFactory }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { apiViewModelFactory }
    private val orderViewModel: OrderViewModel by viewModels { apiViewModelFactory }

    companion object {
        private const val UNKNOWN_CATEGORY = "UNKNOWN"

        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        observeApiViewModel()
        requestCartItemsFromApi()
        shoppingCartItemViewModel.refresh()
        setContent {
            val shoppingCartItemsState by shoppingCartItemViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                launch {
                    shoppingCartItemViewModel.event.collect { event ->
                        when (event) {
                            ShoppingCartItemViewModel.ShoppingCartEvent.NavigateBack -> finish()
                        }
                    }
                }
                launch {
                    orderViewModel.event.collect { event ->
                        when (event) {
                            is OrderViewModel.OrderEvent.Success -> {
                                appContainer.recommendationStore.updateRecommendedCategory(
                                    resolveRecommendedCategory(
                                        shoppingCartViewModel.shoppingCartItems.value,
                                    ),
                                )
                                finish()
                            }

                            is OrderViewModel.OrderEvent.Failure -> {
                                // no-op
                            }
                        }
                    }
                }
            }
            AndroidShoppingTheme {
                ShoppingCartScreen(
                    shoppingCartItems = shoppingCartItemsState.pagedItems,
                    getQuantityPrice = shoppingCartItemViewModel::getQuantityPrice,
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
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                    ) {
                        PageNavigation(
                            currentPage = shoppingCartItemsState.currentPage,
                            canMoveToPreviousPage = shoppingCartItemsState.canMoveToPreviousPage,
                            canMoveToNextPage = shoppingCartItemsState.canMoveToNextPage,
                            onBeforePageClick = shoppingCartItemViewModel::moveToPreviousPage,
                            onNextPageClick = shoppingCartItemViewModel::moveToNextPage,
                        )
                        OrderButton(
                            onOrderButtonClick = ::requestOrder,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestCartItemsFromApi()
        shoppingCartItemViewModel.refresh()
    }

    private fun requestCartItemsFromApi() {
        shoppingCartViewModel.requestCartItems()
    }

    private fun observeApiViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                launch {
                    shoppingCartViewModel.shoppingCartItems.collect { shoppingCartItems ->
                        if (!shoppingCartViewModel.hasLoadedCartItems.value) {
                            return@collect
                        }
                        appContainer.remoteShoppingStateSyncer.syncCartItems(shoppingCartItems)
                        shoppingCartItemViewModel.refresh()
                    }
                }
            }
        }
    }

    private fun requestOrder() {
        val shoppingCartItems = shoppingCartViewModel.shoppingCartItems.value
        if (shoppingCartItems.isEmpty()) {
            return
        }
        orderViewModel.order(shoppingCartItems.toOrderInfo())
    }

    private fun resolveRecommendedCategory(shoppingCartItems: List<ShoppingCartItem>): String? =
        shoppingCartItems
            .map { shoppingCartItem -> shoppingCartItem.product.category }
            .filter { category -> category.isNotBlank() && category != UNKNOWN_CATEGORY }
            .groupingBy { category -> category }
            .eachCount()
            .maxByOrNull { (_, count) -> count }
            ?.key

}
