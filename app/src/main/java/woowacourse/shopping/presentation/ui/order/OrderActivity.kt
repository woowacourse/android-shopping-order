package woowacourse.shopping.presentation.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.database.OrderDatabase
import woowacourse.shopping.data.repository.RemoteCartRepositoryImpl
import woowacourse.shopping.data.repository.RemoteCouponRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.ui.payment.adapter.CouponsAdapter

class OrderActivity : AppCompatActivity(), OrderClickListener {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var couponsAdapter: CouponsAdapter
    private val viewModel: OrderViewModel by viewModels {
        OrderViewModelFactory(
            cartRepository = RemoteCartRepositoryImpl(),
            couponRepository = RemoteCouponRepositoryImpl(),
            orderDatabase = OrderDatabase,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView() {
        initDataBinding()
        initObserve()
        initAdapter()
    }

    private fun initDataBinding() {
        binding.viewModel = viewModel
        binding.eventHandler = viewModel
        binding.orderClickListener = this
        binding.uiState = viewModel.uiState.value
    }

    private fun initAdapter() {
        couponsAdapter = CouponsAdapter(viewModel)
        couponsAdapter.loadData(viewModel.uiState.value?.couponsState ?: emptyList())
        binding.recyclerviewAvailableCouponList.adapter = couponsAdapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(this) { state ->
            couponsAdapter.submitList(state.couponsState)
            binding.uiState = state
        }
    }

    override fun onBackClick() {
        finish()
    }

    override fun order() {
        viewModel.payment()
        Toast.makeText(this, getString(R.string.message_order_complete), Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderActivity::class.java)
        }
    }
}
