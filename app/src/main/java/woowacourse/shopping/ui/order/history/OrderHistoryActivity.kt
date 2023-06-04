package woowacourse.shopping.ui.order.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View
import woowacourse.shopping.ui.order.main.recyclerview.adapter.OrderAdapter
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.inject.injectOrderHistoryPresenter

class OrderHistoryActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var adapter: OrderAdapter
    private val presenter: Presenter by lazy {
        injectOrderHistoryPresenter(view = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater).setContentView(this)
        setActionBar()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.orderHistoryToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.navigateToHome(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToHome() {
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, OrderHistoryActivity::class.java)
    }
}
