@file:Suppress("FunctionName")

package woowacourse.shopping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import woowacourse.shopping.ui.ShoppingCartScreen
import woowacourse.shopping.ui.component.PageNavigation
import woowacourse.shopping.ui.pagination.ShoppingCartPageStateHolder
import woowacourse.shopping.ui.theme.AndroidShoppingTheme
import woowacourse.shopping.viewmodel.ShoppingCartItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
class ShoppingCartActivity : ComponentActivity() {
    private val shoppingCartItemViewModel: ShoppingCartItemViewModel by viewModels()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ShoppingCartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shoppingCartItemsState by shoppingCartItemViewModel.shoppingCartItems.collectAsState()
            val shoppingCartItems = shoppingCartItemsState.items
            var savedCurrentPage by rememberSaveable { mutableIntStateOf(0) }
            AndroidShoppingTheme {
                val shoppingCartPageStateHolder =
                    remember(shoppingCartItemsState, savedCurrentPage) {
                        ShoppingCartPageStateHolder(shoppingCartItems).apply {
                            restoreCurrentPage(savedCurrentPage)
                        }
                    }
                val pageItems = shoppingCartPageStateHolder.getItems()
                ShoppingCartScreen(
                    shoppingCartItems = pageItems,
                    getQuantityPrice = shoppingCartItemViewModel::getQuantityPrice,
                    onBackClick = this::finish,
                    onRemoveShoppingItemClick = { shoppingCartItem ->
                        shoppingCartItemViewModel.removeShoppingItem(shoppingCartItem)
                        savedCurrentPage = shoppingCartPageStateHolder.currentPage
                    },
                    onIncreaseShoppingItemQuantityClick = { shoppingCartItem ->
                        shoppingCartItemViewModel.increaseShoppingItemQuantity(shoppingCartItem)
                        savedCurrentPage = shoppingCartPageStateHolder.currentPage
                    },
                    onDecreaseShoppingItemQuantityClick = { shoppingCartItem ->
                        shoppingCartItemViewModel.decreaseShoppingItemQuantity(shoppingCartItem)
                        savedCurrentPage = shoppingCartPageStateHolder.currentPage
                    },
                ) {
                    PageNavigation(
                        currentPage = shoppingCartPageStateHolder.currentPage,
                        canMoveToPreviousPage = shoppingCartPageStateHolder.canMoveToPreviousPage(),
                        canMoveToNextPage = shoppingCartPageStateHolder.canMoveToNextPage(),
                        onBeforePageClick = {
                            shoppingCartPageStateHolder.beforePage()
                            savedCurrentPage = shoppingCartPageStateHolder.currentPage
                        },
                        onNextPageClick = {
                            shoppingCartPageStateHolder.nextPage()
                            savedCurrentPage = shoppingCartPageStateHolder.currentPage
                        },
                    )
                }
            }
        }
    }
}
