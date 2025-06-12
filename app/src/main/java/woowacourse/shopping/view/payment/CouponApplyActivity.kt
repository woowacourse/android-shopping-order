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
import woowacourse.shopping.data.payment.CouponApplyEvent
import woowacourse.shopping.databinding.ActivityCouponApplyBinding
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.view.product.ProductsActivity
import woowacourse.shopping.view.showToast

class CouponApplyActivity : AppCompatActivity() {
    private val binding: ActivityCouponApplyBinding by lazy {
        ActivityCouponApplyBinding.inflate(layoutInflater)
    }
    private val viewModel: CouponApplyViewModel by viewModels()
    private val adapter by lazy { CouponsAdapter(viewModel::selectCoupon) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.couponApplyRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViewModel()
        initViews()
        handleEvents()
    }

    private fun initViewModel() {
        viewModel.cartItems = intent.getSelectedCartItemsExtra() ?: return finish()
        viewModel.loadCouponsRepository()
    }

    private fun initViews() {
        binding.couponApplyCoupons.adapter = adapter

        viewModel.state.observe(this) { state: CouponApplyState ->
            adapter.submitList(state.coupons)
            binding.couponApplyOrderAmount.text =
                getString(R.string.price_format, state.orderAmount)
            binding.couponApplyCouponDiscountAmount.text =
                getString(R.string.price_format, state.discountAmount)
            binding.couponApplyDeliveryFee.text =
                getString(R.string.price_format, state.deliveryFee)
            binding.couponApplyTotalPaymentAmount.text =
                getString(R.string.price_format, state.totalPaymentAmount)
        }
    }

    private fun handleEvents() {
        viewModel.event.observe(this) { event ->
            when (event) {
                CouponApplyEvent.LOAD_COUPONS_FAILURE -> showToast(R.string.load_coupons_error_message)

                CouponApplyEvent.ORDER_SUCCESS -> {
                    showToast(R.string.order_success_message)

                    val intent =
                        Intent(this, ProductsActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    startActivity(intent)
                }

                CouponApplyEvent.ORDER_FAILURE -> showToast(R.string.order_failure_message)
            }
        }

        binding.couponApplyBackButton.setOnClickListener {
            finish()
        }

        binding.couponApplyPayButton.setOnClickListener {
            viewModel.order()
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
