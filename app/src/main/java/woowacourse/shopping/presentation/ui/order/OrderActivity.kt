package woowacourse.shopping.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCouponRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.ui.payment.adapter.CouponsAdapter

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var couponsAdapter: CouponsAdapter
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(
            cartRepository = RemoteCartRepositoryImpl(),
            couponRepository = RemoteCouponRepositoryImpl(),
            order = OrderDatabase.getOrder(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_order)
        initView()
    }

    fun initView() {
        //    initActionBar()
        initDataBinding()
        initObserve()
        initAdapter()
    }

    /*private fun initActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.payment_title)
        }
    }*/

    private fun initDataBinding() {
        binding.viewModel = viewModel
    }

    private fun initAdapter() {
        couponsAdapter = CouponsAdapter(viewModel)
        couponsAdapter.loadData(viewModel.uiState.value?.couponsState ?: emptyList())
        binding.recyclerviewAvailableCouponList.adapter = couponsAdapter
    }

    private fun initObserve() {
        /*viewModel.navigateAction.observeEvent(this) { navigateAction ->
            when (navigateAction) {
                is PaymentNavigateAction.NavigateToProductList -> {
                    val intent =
                        ProductListActivity.getIntent(this).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                    startActivity(intent)
                }
            }
        }

        viewModel.message.observeEvent(this) { message ->
            when (message) {
                is PaymentMessage.PaymentSuccessMessage -> showToastMessage(message.getMessage(this))
            }
        }*/

        viewModel.uiState.observe(this) { state ->
            couponsAdapter.submitList(state.couponsState)
            Log.d("crong", "initObserve: ${state.couponsState}")
        }
    }

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }*/

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
