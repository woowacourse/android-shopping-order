package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.remote.OrderRetrofitDataSource
import woowacourse.shopping.data.repository.OrderDefaultRepository
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.order.orderProductAdapter.OrderProductAdapter
import woowacourse.shopping.ui.shopping.ShoppingActivity

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var binding: ActivityOrderBinding

    private val adapter = OrderProductAdapter()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        initBinding()
        initToolbar()
        initView()
    }

    private fun initPresenter() {
        presenter = OrderPresenter(
            view = this,
            intent.getIntegerArrayListExtra(KEY_CART_IDS) ?: listOf(),
            orderRepository = OrderDefaultRepository(
                orderRemoteDataSource = OrderRetrofitDataSource()
            )
        )
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrders.adapter = adapter
        binding.confirmOrder = {
            presenter.processToOrderConfirmation(
                binding.etUsedPoint.text.toString().toIntOrNull() ?: 0
            )
        }
        presenter.fetchOrder()
    }

    override fun setOrder(order: OrderUIModel) {
        runOnUiThread {
            binding.order = order
            binding.etUsedPoint.addTextChangedListener(
                textWatcher(order.availablePoints)
            )
            adapter.submitList(order.cartProducts)
        }
    }

    private fun textWatcher(maxPoint: Int) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable?) {
            val currentPoint = s.toString().toIntOrNull() ?: 0
            when {
                currentPoint < 0 -> binding.etUsedPoint.setText("0")
                currentPoint > maxPoint -> binding.etUsedPoint.setText(maxPoint.toString())
                else -> Unit
            }
            binding.discountPrice = currentPoint
        }
    }

    override fun navigateToOrderConfirmation() {
        startActivity(
            ShoppingActivity.getIntent(this).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        )
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"
        fun getIntent(context: Context, cartIds: List<Int>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putIntegerArrayListExtra(KEY_CART_IDS, cartIds as ArrayList<Int>)
            }
        }
    }
}
