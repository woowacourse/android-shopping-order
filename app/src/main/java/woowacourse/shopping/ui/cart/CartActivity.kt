package woowacourse.shopping.ui.cart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import woowacourse.shopping.IntentKeys
import woowacourse.shopping.ui.recommendation.RecommendationActivity
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.ui.cart.uimodel.toUiModel

class CartActivity : ComponentActivity() {
    private lateinit var viewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            viewModel =
                viewModel<CartViewModel>(
                    factory =
                        CartViewModelFactory(
                            (application as ShoppingApplication).cartRepository
                        ),
                )

            val pagedCart by viewModel.pagedCart.collectAsStateWithLifecycle()
            val currentPage by viewModel.currentPage.collectAsStateWithLifecycle()
            val isPageable by viewModel.isPageable.collectAsStateWithLifecycle()
            val nextEnable by viewModel.nextEnable.collectAsStateWithLifecycle()
            val prevEnable by viewModel.prevEnable.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val checkedItemIds by viewModel.checkedItemIds.collectAsStateWithLifecycle()
            val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
            val totalCount by viewModel.selectedItemCount.collectAsStateWithLifecycle()
            val uiState =
                CartUiState(
                    cartItems = pagedCart.toUiModel(),
                    currentPage = currentPage,
                    isPageable = isPageable,
                    previousEnable = prevEnable,
                    nextEnable = nextEnable,
                    isLoading = isLoading,
                    totalPrice = totalPrice,
                    totalCount = totalCount,
                    checkedItemIds = checkedItemIds,
                )

            Scaffold(modifier = Modifier.Companion.fillMaxSize()) { paddingValues ->
                CartScreen(
                    uiState = uiState,
                    onClose = {
                        finish()
                    },
                    onAdd = { id, updateAmount ->
                        viewModel.updateCountWithID(id, updateAmount)
                    },
                    onMinus = { id, updateAmount ->
                        viewModel.updateCountWithID(id, updateAmount)
                    },
                    onDelete = { id ->
                        viewModel.removeWithID(id)
                    },
                    onPrevious = {
                        viewModel.prev()
                    },
                    onNext = {
                        viewModel.next()
                    },
                    onCheckedChanged = { viewModel.onItemChecked(it) },
                    onSelectAllClick = { viewModel.onSelectAllClick() },
                    onOrderClick = {
                        val intent = Intent(this, RecommendationActivity::class.java).apply {
                            putExtra(
                                IntentKeys.SELECTED_CART_ID_KEY,
                                checkedItemIds.toLongArray()
                            )
                        }
                        startActivity(intent)
                    },
                    modifier =
                        Modifier.Companion
                            .fillMaxSize()
                            .padding(paddingValues),
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::viewModel.isInitialized) {
            viewModel.fetchCart()
        }
    }
}
