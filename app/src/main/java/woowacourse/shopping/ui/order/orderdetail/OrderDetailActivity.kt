package woowacourse.shopping.ui.order.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.databinding.ItemOrderDiscountBinding
import woowacourse.shopping.ui.order.adapter.OrderListAdapter
import woowacourse.shopping.ui.order.uistate.DiscountPolicyUIState
import woowacourse.shopping.ui.order.uistate.OrderUIState

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private val binding: ActivityOrderDetailBinding by lazy {
        ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    private val presenter: OrderDetailContract.Presenter by lazy {
        OrderDetailPresenter(this)
    }

    private val orderListAdapter: OrderListAdapter by lazy {
        OrderListAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initRecycler()
        initOrder()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showOrder(order: OrderUIState) {
        binding.order = order
        orderListAdapter.setOrders(listOf(order))
    }

    private fun initToolbar() {
        setSupportActionBar(binding.orderDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecycler() {
        binding.orderDetailRecycler.adapter = orderListAdapter
    }

    private fun initOrder() {
        presenter.loadOrder()
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, this::class.java).run {
                context.startActivity(this)
            }
        }

        @BindingAdapter("app:discountPolicies")
        @JvmStatic
        fun discountPolicies(layout: LinearLayout, discountPolicies: List<DiscountPolicyUIState>?) {
            discountPolicies ?: return
            for (discountPolicy in discountPolicies) {
                val discountPolicyBinding = ItemOrderDiscountBinding.inflate(
                    LayoutInflater.from(layout.context),
                    layout,
                    true
                )
                discountPolicyBinding.discountPolicy = discountPolicy
            }
        }
    }
}
