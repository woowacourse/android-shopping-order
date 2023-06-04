package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.data.member.MemberRemoteDataSourceRetrofit
import woowacourse.shopping.data.member.MemberRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.model.OrderHistoryModel

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
        val memberRepository = MemberRepositoryImpl(MemberRemoteDataSourceRetrofit())
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
        binding.rvOrderHistory.adapter = OrderHistoryAdapter(histories)
    }

    override fun notifyLoadFailed() {
        runOnUiThread {
            Toaster.showToast(this, "주문 목록을 불러오는데 실패했습니다!")
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}