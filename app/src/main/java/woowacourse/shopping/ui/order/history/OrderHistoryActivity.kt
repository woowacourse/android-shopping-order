package woowacourse.shopping.ui.order.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.UiOrderResponse
import woowacourse.shopping.ui.order.detail.OrderDetailActivity
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View
import woowacourse.shopping.ui.order.history.recyclerview.adapter.history.HistoryAdapter
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.inject.injectOrderHistoryPresenter
import woowacourse.shopping.util.toast.Toaster

class OrderHistoryActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private val presenter: Presenter by lazy {
        injectOrderHistoryPresenter(view = this, baseUrl = pref.getBaseUrl().toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater).setContentView(this)
        setActionBar()
        initRecyclerView()
        presenter.loadOrderedProducts()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.orderHistoryToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showOrderedProducts(orderedProducts: List<UiOrderResponse>) {
        adapter.submitList(orderedProducts)
    }

    override fun showLoadFailed(error: String) {
        Toaster.showToast(this, LOAD_ERROR_MESSAGE.format(error))
    }

    private fun navigateToOrderDetail(orderId: Int) {
        val intent = OrderDetailActivity.getIntent(this, orderId)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.navigateToHome(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToHome() {
        finish()
    }

    private fun initRecyclerView() {
        binding.orderHistoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = HistoryAdapter(::navigateToOrderDetail)
        binding.orderHistoryRecyclerView.adapter = adapter
        binding.orderHistoryRecyclerView.itemAnimator = null

        binding.orderHistoryRecyclerView.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!binding.orderHistoryRecyclerView.canScrollVertically(1)) {
                    presenter.loadOrderedProducts()
                }
            }
        })
    }

    companion object {
        private const val LOAD_ERROR_MESSAGE = "[ERROR] 데이터를 불러오는 데에 실패했습니다. : %s"

        fun getIntent(context: Context): Intent = Intent(context, OrderHistoryActivity::class.java)
    }
}