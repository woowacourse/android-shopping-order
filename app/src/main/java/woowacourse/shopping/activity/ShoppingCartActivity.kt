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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.backend.retrofit.viewmodel.BackendViewModelFactory
import woowacourse.shopping.backend.retrofit.viewmodel.ShoppingCartViewModel
import woowacourse.shopping.ui.ShoppingCartScreen
import woowacourse.shopping.ui.ShoppingCartState
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.theme.AndroidShoppingTheme

@OptIn(ExperimentalMaterial3Api::class)
class ShoppingCartActivity : ComponentActivity() {
    private val backendViewModelFactory: BackendViewModelFactory by lazy {
        val app = application as ShoppingApplication
        BackendViewModelFactory(app.retrofitService)
    }
    private val shoppingCartViewModel: ShoppingCartViewModel by viewModels { backendViewModelFactory }

    companion object {
        private const val INITIAL_PAGE = 0
        private const val PAGE_ITEM_SIZE = 5

        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        shoppingCartViewModel.requestCartItems()
        setContent {
            val shoppingCartItems by shoppingCartViewModel.shoppingCartItems.collectAsStateWithLifecycle()
            val isLoading by shoppingCartViewModel.isLoading.collectAsStateWithLifecycle()
            val errorMessage by shoppingCartViewModel.errorMessage.collectAsStateWithLifecycle()
            var currentPage by rememberSaveable { mutableIntStateOf(INITIAL_PAGE) }
            val pageStartIndex = currentPage * PAGE_ITEM_SIZE
            val pagedItems = shoppingCartItems.drop(pageStartIndex).take(PAGE_ITEM_SIZE)
            val canMoveToPreviousPage = currentPage > INITIAL_PAGE
            val canMoveToNextPage = (currentPage + 1) * PAGE_ITEM_SIZE < shoppingCartItems.size
            val state =
                ShoppingCartState(
                    items = shoppingCartItems,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    currentPage = currentPage,
                    canMoveToPreviousPage = canMoveToPreviousPage,
                    canMoveToNextPage = canMoveToNextPage,
                )
            LaunchedEffect(shoppingCartItems.size, currentPage) {
                val lastPage =
                    if (shoppingCartItems.isEmpty()) {
                        INITIAL_PAGE
                    } else {
                        (shoppingCartItems.size - 1) / PAGE_ITEM_SIZE
                    }
                if (currentPage > lastPage) {
                    currentPage = lastPage
                }
            }

            AndroidShoppingTheme {
                ShoppingCartScreen(
                    shoppingCartItems = pagedItems,
                    getQuantityPrice = shoppingCartViewModel::getQuantityPrice,
                    state = state,
                    onBackClick = this::finish,
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
                        currentPage = currentPage,
                        canMoveToPreviousPage = canMoveToPreviousPage,
                        canMoveToNextPage = canMoveToNextPage,
                        onBeforePageClick = {
                            if (currentPage > INITIAL_PAGE) {
                                currentPage -= 1
                            }
                        },
                        onNextPageClick = {
                            if ((currentPage + 1) * PAGE_ITEM_SIZE < shoppingCartItems.size) {
                                currentPage += 1
                            }
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
}
