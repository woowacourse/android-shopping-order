package woowacourse.shopping

import android.content.Context
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
import woowacourse.shopping.ui.component.screen.CartScreen
import woowacourse.shopping.ui.viewmodel.CartViewModel
import woowacourse.shopping.ui.viewmodel.CartViewModelFactory

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
                            (application as ShoppingApplication).cartRepository,
                        ),
                )

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                CartScreen(
                    cart = uiState.items,
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
                    currentPage = uiState.currentPage,
                    onPrevious = {
                        viewModel.prev()
                    },
                    onNext = {
                        viewModel.next()
                    },
                    previousEnable = uiState.isPrevEnable,
                    nextEnable = uiState.isNextEnable,
                    isPageable = uiState.isPageable,
                    isLoading = uiState.isLoading,
                    onCheckedChanged = { viewModel.onItemChecked(it) },
                    totalPrice = uiState.totalPrice,
                    totalCount = uiState.checkedItemIds.size,
                    isChecked = { id -> uiState.checkedItemIds.contains(id) },
                    onSelectAllClick = { viewModel.onSelectAllClick() },
                    onOrderClick = {
                        if (uiState.checkedItemIds.isNotEmpty()) {
                            RecommendationActivity.startActivity(
                                context = this,
                                totalPrice = uiState.totalPrice,
                                checkedIds =  uiState.checkedItemIds,
                            )
                        }
                    },
                    isAllChecked = viewModel.isAllChecked(),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (::viewModel.isInitialized) viewModel.fetchCart()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, CartActivity::class.java)
            context.startActivity(intent)
        }
    }
}
