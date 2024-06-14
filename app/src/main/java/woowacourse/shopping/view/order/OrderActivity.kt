package woowacourse.shopping.view.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.view.cart.CartItemInfo
import woowacourse.shopping.view.cart.toCartItemDomain
import woowacourse.shopping.view.cart.toCartItemInfo
import woowacourse.shopping.view.home.HomeActivity

class OrderActivity : AppCompatActivity() {
    private val binding: ActivityOrderBinding by lazy { ActivityOrderBinding.inflate(layoutInflater) }
    private val orders by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_CART_ITEMS, CartItemInfo::class.java)
        } else {
            intent.getParcelableArrayListExtra(EXTRA_CART_ITEMS)
        }
    }
    private val viewModel: OrderViewModel by viewModels<OrderViewModel> {
        (application as ShoppingApplication).orderViewModelFactory(
            orders?.map(CartItemInfo::toCartItemDomain) ?: emptyList(),
        )
    }
    private val adapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeBindingVariables()
        initializeToolbar()
        observeEvent()
        observeState()
    }

    private fun initializeBindingVariables() {
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.lifecycleOwner = this
    }

    private fun observeEvent() {
        viewModel.orderUiEvent.observe(this) { event ->
            when (event.getContentIfNotHandled() ?: return@observe) {
                is OrderUiEvent.NavigateBackToHome -> {
                    showToastMessage(getString(R.string.order_completion_message))
                    navigateBackToHome()
                }
                is OrderUiEvent.Error -> showToastMessage(getString(R.string.unknown_error))
            }
        }
    }

    private fun navigateBackToHome() {
        Intent(this, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigateBackToHome()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeToolbar() {
        setSupportActionBar(binding.toolbarOrder)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_vector)
    }

    private fun observeState() {
        viewModel.orderUiState.observe(this) { state ->
            adapter.submitList(state.coupons)
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_CART_ITEMS = "cart_items"

        fun createIntent(
            context: Context,
            cartItems: List<CartItemDomain>,
        ): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(
                    EXTRA_CART_ITEMS,
                    ArrayList(cartItems.map(CartItemDomain::toCartItemInfo)),
                )
        }
    }
}
