package woowacourse.shopping.ui.coupon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.coupon.remote.RemoteCouponRepository
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.ui.coupon.adapter.CouponAdapter
import woowacourse.shopping.ui.products.ProductsActivity

class CouponActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCouponBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<CouponViewModel> {
        CouponViewModelFactory(
            selectedCartItemIds(),
            RemoteCartRepository,
            RemoteCouponRepository,
            RemoteOrderRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initializeView()
    }

    private fun initializeView() {
        initializeCouponList()
        initializeToolbar()
        observeData()
    }

    private fun initializeCouponList() {
        val adapter = CouponAdapter(viewModel)
        binding.rvCoupon.itemAnimator = null
        binding.rvCoupon.adapter = adapter
        viewModel.couponUiModels.observe(this) {
            adapter.submitList(it.uiModels)
        }
    }

    private fun initializeToolbar() {
        binding.toolbarCart.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeData() {
        viewModel.couponErrorEvent.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            showToastCouponFailure()
        }
        viewModel.isSuccessCreateOrder.observe(this) {
            val isSuccessCreateOrder = it.getContentIfNotHandled() ?: return@observe
            if (isSuccessCreateOrder) {
                showToastOrderSuccess()
                navigateProductsView()
            } else {
                showToastCouponFailure()
            }
        }
    }

    private fun showToastOrderSuccess() {
        Toast.makeText(this, R.string.create_order_success, Toast.LENGTH_SHORT).show()
    }

    private fun navigateProductsView() {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showToastCouponFailure() {
        Toast.makeText(this, R.string.common_error_retry, Toast.LENGTH_SHORT).show()
    }

    private fun selectedCartItemIds(): List<Int> {
        return intent.getIntArrayExtra(SELECTED_CART_ITEM_IDS_KEY)?.toList() ?: emptyList()
    }

    companion object {
        private const val SELECTED_CART_ITEM_IDS_KEY = "selected_cart_item_ids"

        fun newIntent(
            context: Context,
            selectedCartItemIds: List<Int>,
        ): Intent {
            return Intent(context, CouponActivity::class.java)
                .putExtra(SELECTED_CART_ITEM_IDS_KEY, selectedCartItemIds.toIntArray())
        }
    }
}
