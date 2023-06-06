package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.RepositoryInjector
import woowacourse.shopping.ui.RetrofitInjector
import woowacourse.shopping.ui.model.OrderHistoryModel
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.orderdetail.OrderDetailPurpose

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var presenter: OrderHistoryContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)

        initPresenter()

        setupToolbar()

        setupView()
    }

    private fun initBinding() {
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
    }

    private fun initPresenter() {
        val retrofit = RetrofitInjector.inject(this)
        val memberRepository = RepositoryInjector.injectMemberRepository(retrofit)

        presenter = OrderHistoryPresenter(this, memberRepository)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.orderHistoryToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupView() {
        presenter.loadHistories()
    }

    override fun showHistories(histories: List<OrderHistoryModel>) {
        binding.rvOrderHistory.adapter = OrderHistoryAdapter(
            histories,
            onShowDetailListener = { presenter.openDetail(it) }
        )
    }

    override fun notifyFailure(message: String) {
        runOnUiThread {
            Toaster.showToast(this, message)
        }
    }

    override fun showDetail(id: Int) {
        val intent = OrderDetailActivity.createIntent(this, id, OrderDetailPurpose.SHOW_ORDER_DETAIL.name)
        startActivity(intent)
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}