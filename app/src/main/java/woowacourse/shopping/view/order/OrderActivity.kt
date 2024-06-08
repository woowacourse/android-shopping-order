package woowacourse.shopping.view.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication.Companion.couponRepository
import woowacourse.shopping.ShoppingApplication.Companion.orderRepository
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.view.cart.CartItemInfo
import woowacourse.shopping.view.cart.toCartItemDomain
import woowacourse.shopping.view.cart.toCartItemInfo
import woowacourse.shopping.view.home.HomeActivity
import woowacourse.shopping.view.state.OrderUiEvent

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
        OrderViewModelFactory(
            couponRepository,
            orderRepository,
            orders?.map(CartItemInfo::toCartItemDomain) ?: emptyList()
        )
    }
    private val adapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        binding.adapter = adapter
        binding.lifecycleOwner = this

        viewModel.orderUiEvent.observe(this) { event ->
            when (event.getContentIfNotHandled() ?: return@observe) {
                is OrderUiEvent.NavigateBackToHome -> {
                    Intent(this, HomeActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(it)
                    }
                }

                is OrderUiEvent.Error -> showError()
            }
        }

        viewModel.orderUiState.observe(this) { state ->
            adapter.submitList(state.coupons)
        }
    }

    private fun showError() {
        Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_CART_ITEMS = "cart_items"

        fun createIntent(context: Context, cartItems: List<CartItemDomain>): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(
                    EXTRA_CART_ITEMS,
                    ArrayList(cartItems.map(CartItemDomain::toCartItemInfo)),
                )
        }
    }
}
