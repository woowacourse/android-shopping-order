package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.coupons.repository.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.coupons.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityCouponBinding
import woowacourse.shopping.feature.BaseActivity
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.goods.GoodsActivity

class CouponActivity : BaseActivity<ActivityCouponBinding>() {

    private lateinit var viewModel : CouponViewModel
    private lateinit var couponAdapter: CouponAdapter

    override fun inflateBinding(): ActivityCouponBinding =
        ActivityCouponBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = CouponViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
        viewModel = ViewModelProvider(this, factory)[CouponViewModel::class.java]
        couponAdapter = CouponAdapter { selectedCoupon ->
            viewModel.selectCoupon(selectedCoupon)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "결제하기"

        binding.couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CouponActivity)
            adapter = couponAdapter
        }
        binding.tvCouponFee.text = getString(R.string.amount, 0)
        observeViewModel()
        viewModel.loadCoupons()
        val selectedItems =
            intent.getIntegerArrayListExtra(CartActivity.SELECTED_CART_ITEM_KEY) ?: arrayListOf()
        viewModel.setCartItems(selectedItems)

        binding.btnPay.setOnClickListener {
            viewModel.onPayClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons,null)
        }

        viewModel.originalAmount.observe(this) { amount ->
            binding.summeryTexts.text = getString(R.string.amount, amount)
        }

        viewModel.totalAmount.observe(this) { amount ->
            binding.tvTotalPayAmount.text = getString(R.string.amount, amount)
        }

        viewModel.shippingFee.observe(this) { amount ->
            binding.tvShppingFee.text = getString(R.string.amount, amount)
        }

        viewModel.selectedCoupon.observe(this) { selectedCoupon ->
            val discount = selectedCoupon?.calculateDiscount(
                viewModel.cartItems,
                viewModel.originalAmount.value ?: 0
            ) ?: 0
            binding.tvCouponFee.text = getString(R.string.amount, discount*-1)
        }
        viewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons, viewModel.selectedCoupon.value)
        }

        viewModel.selectedCoupon.observe(this) { selectedCoupon ->
            couponAdapter.submitList(viewModel.coupons.value ?: emptyList(), selectedCoupon)
        }
        viewModel.uiEvent.observe(this) { orderUiEvent ->
            when(orderUiEvent){
                OrderUiEvent.OrderSuccess -> orderFinish()
                is OrderUiEvent.ShowToast -> showToast(orderUiEvent.messageKey.toMessage())
            }

        }
    }

    private fun orderFinish(){
        showToast("주문에 성공했습니다!")

        val intent = Intent(this, GoodsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    private fun showToast(message : String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun ToastMessageKey.toMessage():String{
        return when(this){
            ToastMessageKey.FAIL_ORDER -> "주문에 실패했습니다."
            ToastMessageKey.FAIL_LOAD_COUPON -> "쿠폰 불러오기에 실패했습니다."
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CouponActivity::class.java)
    }
}