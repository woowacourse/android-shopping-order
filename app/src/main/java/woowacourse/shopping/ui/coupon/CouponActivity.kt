package woowacourse.shopping.ui.coupon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.remote.RemoteCartRepository
import woowacourse.shopping.data.coupon.remote.RemoteCouponRepository
import woowacourse.shopping.data.order.remote.RemoteOrderRepository
import woowacourse.shopping.data.remote.ApiError
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
        observeCouponErrorEvent()
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
        viewModel.isSuccessCreateOrder.observe(this) {
            val isSuccessCreateOrder = it.getContentIfNotHandled() ?: return@observe
            if (isSuccessCreateOrder) {
                showToast(R.string.create_order_success)
                navigateProductsView()
            } else {
                showToast(R.string.create_order_failure)
            }
        }
    }

    private fun navigateProductsView() {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun observeCouponErrorEvent() {
        viewModel.couponLoadError.observe(this) {
            val throwable = it.getContentIfNotHandled() ?: return@observe
            showCouponLoadErrorToast(throwable, R.string.coupon_load_error)
        }
        viewModel.orderPossibleError.observe(this) {
            it.getContentIfNotHandled() ?: return@observe
            showToast(R.string.create_order_possible_error)
        }
    }

    private fun showCouponLoadErrorToast(
        throwable: Throwable,
        @StringRes errorMessageResId: Int,
    ) {
        if (throwable is ApiError) {
            showToast(errorMessageResId)
        }
        when (throwable) {
            is ApiError.BadRequest -> showToast(errorMessageResId)
            is ApiError.Unauthorized -> showToast(R.string.unauthorized_error)
            is ApiError.Forbidden -> showToast(R.string.unauthorized_error)
            is ApiError.NotFound -> showToast(R.string.product_not_found_error)
            is ApiError.InternalServerError -> showToast(R.string.server_error)
            is ApiError.Exception -> showToast(errorMessageResId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
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
