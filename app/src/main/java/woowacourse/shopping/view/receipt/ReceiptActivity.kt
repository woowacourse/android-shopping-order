package woowacourse.shopping.view.receipt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityReceiptBinding
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.util.getSerializableExtraCompat
import woowacourse.shopping.view.showToast

class ReceiptActivity : AppCompatActivity(), ReceiptActions {
    private val binding by lazy { ActivityReceiptBinding.inflate(layoutInflater) }
    private val receiptAdapter by lazy { ReceiptAdapter(this) }
    private val viewModel: ReceiptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initBinding()

        val cartItems: List<CartItem> =
            intent.getSerializableExtraCompat<ArrayList<CartItem>>(EXTRA_CART_ITEMS_ID)?.toList()
                ?: emptyList()

        viewModel.showAvailableCoupons(cartItems)
        viewModel.couponItem.observe(this) { couponItems: List<CouponItem> ->
            receiptAdapter.submitList(couponItems)
        }
        viewModel.event.observe(this) { event: ReceiptEvent ->
            when (event) {
                ReceiptEvent.LOAD_MORE_COUPON_FAILURE -> {
                    showToast(getString(R.string.receipt_load_coupon_items_error_message))
                    finish()
                }
            }
        }
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.rvCoupon.adapter = receiptAdapter
        binding.vm = viewModel
    }

    companion object {
        private const val EXTRA_CART_ITEMS_ID = "woowacourse.shopping.EXTRA_CART_ITEMS_ID"

        fun newIntent(
            context: Context,
            cartItems: List<CartItem>
        ): Intent {
            val intent =
                Intent(context, ReceiptActivity::class.java)
                    .putExtra(EXTRA_CART_ITEMS_ID, ArrayList(cartItems))

            return intent
        }
    }

    override fun select(couponItem: CouponItem) {
        viewModel.select(couponItem)
    }

    override fun unselect() {
        viewModel.unselect()
    }
}