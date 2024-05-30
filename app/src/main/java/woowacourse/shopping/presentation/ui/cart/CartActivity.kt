package woowacourse.shopping.presentation.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory
import woowacourse.shopping.presentation.ui.cart.adapter.CartAdapter
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver

class CartActivity : BindingActivity<ActivityCartBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_cart

    private val cartAdapter: CartAdapter by lazy { CartAdapter(viewModel) }

    private val viewModel: CartViewModel by viewModels { ViewModelFactory() }

    override fun initStartView(savedInstanceState: Bundle?) {
        val initialItemQuantity = intent.getIntExtra(EXTRA_CART_ITEM_QUANTITY, 0)
        if (savedInstanceState == null) {
            viewModel.loadProductByPage(initialItemQuantity)
        }
        binding.cartHandler = viewModel
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        initActionBarTitle()
        initCartAdapter()
        observeErrorEventUpdates()
        observeCartUpdates()
        observeChangedCartProducts()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.cart_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initCartAdapter() {
        binding.rvCarts.adapter = cartAdapter
    }

    private fun observeErrorEventUpdates() {
        viewModel.error.observe(
            this,
            EventObserver {
                showToast(it.message)
            },
        )
    }

    private fun observeCartUpdates() {
        viewModel.shoppingProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    cartAdapter.updateList(it.data)
                }
            }
        }
    }

    private fun observeChangedCartProducts() {
        viewModel.changedCartProducts.observe(this) { carts ->
            carts?.let {
                Intent(applicationContext, ShoppingActivity::class.java).apply {
                    putExtra(EXTRA_CHANGED_PRODUCT_IDS, it.map { it.product.id }.toLongArray())
                    putExtra(EXTRA_NEW_PRODUCT_QUANTITIES, it.map { it.quantity }.toIntArray())
                    setResult(RESULT_OK, this)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    companion object {
        private const val EXTRA_CART_ITEM_QUANTITY = "cartItemQuantity"
        const val EXTRA_CHANGED_PRODUCT_IDS = "changedProductIds"
        const val EXTRA_NEW_PRODUCT_QUANTITIES = "newProductQuantities"

        fun startWithResult(
            context: Context,
            cartItemQuantity: Int,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, CartActivity::class.java).apply {
                putExtra(EXTRA_CART_ITEM_QUANTITY, cartItemQuantity)
                activityLauncher.launch(this)
            }
        }
    }
}
