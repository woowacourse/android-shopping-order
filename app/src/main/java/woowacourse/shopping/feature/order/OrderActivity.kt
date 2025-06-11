package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.coupons.repository.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.coupons.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.BaseActivity
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.goods.GoodsActivity

class OrderActivity : BaseActivity<ActivityOrderBinding>() {

    private lateinit var viewModel : OrderViewModel
    private lateinit var orderAdapter: OrderAdapter

    override fun inflateBinding(): ActivityOrderBinding =
        ActivityOrderBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = OrderViewModelFactory(
            CartRepositoryImpl(CartRemoteDataSourceImpl()),
            OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
        viewModel = ViewModelProvider(this, factory)[OrderViewModel::class.java]
        orderAdapter = OrderAdapter { selectedCoupon ->
            viewModel.selectCoupon(selectedCoupon)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.order_action_bar_name)

        binding.couponRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = orderAdapter
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        observeViewModel()
        viewModel.loadCoupons()
        val selectedItems =
            intent.getIntegerArrayListExtra(CartActivity.SELECTED_CART_ITEM_KEY) ?: arrayListOf()
        viewModel.setCartItems(selectedItems)
        binding.btnPay.setOnClickListener {
            viewModel.onPayClicked()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) { coupons ->
            orderAdapter.submitList(coupons, viewModel.selectedCoupon.value)
        }

        viewModel.uiEvent.observe(this) { orderUiEvent ->
            when(orderUiEvent){
                OrderUiEvent.OrderSuccess -> orderFinish()
                is OrderUiEvent.ShowToast -> showToast(orderUiEvent.messageKey.toMessage())
            }

        }
    }

    private fun orderFinish(){
        showToast(getString(R.string.order_success))

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
            ToastMessageKey.FAIL_ORDER -> getString(R.string.order_fail)
            ToastMessageKey.FAIL_LOAD_COUPON -> getString(R.string.coupon_load_fail)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, OrderActivity::class.java)
    }
}