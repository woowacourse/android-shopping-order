package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.data.payment.CouponEvent
import woowacourse.shopping.databinding.ActivityCouponApplyBinding
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.view.showToast

class CouponApplyActivity : AppCompatActivity() {
    private val binding: ActivityCouponApplyBinding by lazy {
        ActivityCouponApplyBinding.inflate(layoutInflater)
    }
    private val viewModel: CouponApplyViewModel by viewModels()
    private val adapter = CouponsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.couponApplyRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.cartItems = intent.getSelectedCartItemsExtra() ?: return finish()
        viewModel.loadCouponsRepository()

        binding.onClickBackButton = { finish() }

        binding.couponApplyCoupons.adapter = adapter

        viewModel.event.observe(this) { event ->
            when (event) {
                CouponEvent.LOAD_COUPONS_FAILURE -> showToast(getString(R.string.load_coupons_error_message))
            }
        }

        viewModel.state.observe(this) { state: CouponApplyState ->
            adapter.submitList(state.coupons)
            binding.couponApplyOrderAmount.text = state.orderAmount.toString()
            binding.couponApplyCouponDiscountAmount.text = state.discountAmount.toString()
            binding.couponApplyDeliveryFee.text = state.deliveryFee.toString()
            binding.couponApplyTotalPaymentAmount.text = state.totalPaymentAmount.toString()
        }
    }

    private fun Intent.getSelectedCartItemsExtra(): List<CartItem>? =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            getSerializableExtra(
                EXTRA_SELECTED_CART_ITEMS,
                ArrayList::class.java,
            ) as? List<CartItem>
        } else {
            getSerializableExtra(EXTRA_SELECTED_CART_ITEMS) as? List<CartItem>
        }

    companion object {
        fun newIntent(
            context: Context,
            selectedCartItems: ArrayList<CartItem>,
        ): Intent =
            Intent(context, CouponApplyActivity::class.java)
                .putExtra(EXTRA_SELECTED_CART_ITEMS, selectedCartItems)

        private const val EXTRA_SELECTED_CART_ITEMS =
            "woowacourse.shopping.EXTRA_SELECTED_CART_ITEMS"
    }
}
