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

        initializeView()
    }

    private fun initializeView() {
        initializeCouponList()
        observeData()
    }

    private fun initializeCouponList() {
        val adapter = CouponAdapter()
        binding.rvCoupon.itemAnimator = null
        binding.rvCoupon.adapter = adapter
        viewModel.couponUiModels.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun observeData() {
        viewModel.couponErrorEvent.observe(this) {
            showToastCouponError()
        }
    }

    private fun showToastCouponError() {
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
//
// viewModel.isSuccessCreateOrder.observe(this) {
//    val isSuccessCreateOrder = it.getContentIfNotHandled() ?: return@observe
//    if (isSuccessCreateOrder) {
//        showDialogSuccessCreateOrder()
//    } else {
//        showToastFailureCreateOrder()
//    }
// }

//        orderRepository.createOrder(cartItemIds)
//            .onSuccess {
//                _isSuccessCreateOrder.value = Event(true)
//                deleteCartItems(cartItemIds)
//            }.onFailure {
//                _isSuccessCreateOrder.value = Event(false)
//            }

// private fun deleteCartItems(cartItemIds: List<Int>) =
//    viewModelScope.launch {
//        _changedCartEvent.value = Event(Unit)
//        cartItemIds.forEach { cartItemId ->
//            cartRepository.delete(cartItemId)
//                .onSuccess { deleteCartItem(cartItemId) }
//                .onFailure { setError() }
//        }
//    }
