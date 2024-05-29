package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.presentation.state.UIState
import woowacourse.shopping.presentation.ui.detail.DetailActivity

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(
            repository = CartRepositoryImpl(this),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        observeViewModel()
        showSkeletonUI()
    }

    private fun setUpViews() {
        setUpUIState()
    }

    private fun setUpRecyclerViewAdapter(): CartAdapter {
        val adapter = CartAdapter(viewModel, viewModel)
        binding.recyclerView.adapter = adapter
        return adapter
    }

    private fun setUpUIState() {
        val adapter = setUpRecyclerViewAdapter()
        viewModel.cartItemsState.observe(this) { state ->
            when (state) {
                is UIState.Success -> showData(state.data, adapter)
                is UIState.Empty -> {} // emptyCart()
                is UIState.Error ->
                    showError(
                        state.exception.message ?: getString(R.string.unknown_error),
                    )
            }
        }
    }

    private fun showData(
        data: List<CartItem>,
        adapter: CartAdapter,
    ) {
        adapter.loadData(data)
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun emptyCart() {
        viewModel.isCartEmpty()
        Toast.makeText(this, getString(R.string.empty_cart_message), Toast.LENGTH_LONG).show()
    }

    private fun observeViewModel() {
        viewModel.deleteCartItem.observe(this) {
            it.getContentIfNotHandled()?.let { itemId ->
                viewModel.deleteItem(itemId)
            }
        }

        viewModel.navigateToDetail.observe(this) {
            it.getContentIfNotHandled()?.let { productId ->
                navigateToDetail(productId)
            }
        }

        viewModel.navigateToShopping.observe(this) {
            it.getContentIfNotHandled()?.let {
                finish()
            }
        }
    }

    private fun navigateToDetail(productId: Long) {
        startActivity(DetailActivity.createIntent(this, productId))
    }

    private fun showSkeletonUI() {
        lifecycleScope.launch {
            showCartData(isLoading = true)
            delay(3000)
            showCartData(isLoading = false)
            setUpViews()
        }
    }

    private fun showCartData(isLoading: Boolean) {
        if (isLoading) {
            binding.shimmerCartList.startShimmer()
            binding.shimmerCartList.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.shimmerCartList.stopShimmer()
            binding.shimmerCartList.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
