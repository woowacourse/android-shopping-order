package woowacourse.shopping.view.order

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.view.order.adapter.OrderAdapter
import woowacourse.shopping.view.order.vm.OrderViewModel
import woowacourse.shopping.view.order.vm.OrderViewModelFactory

class OrderActivity : AppCompatActivity() {
    private val viewModel: OrderViewModel by viewModels {
        val container = (application as App).container
        OrderViewModelFactory(
            couponRepository = container.couponRepository,
        )
    }

    private lateinit var binding: ActivityOrderBinding

    private val orderAdapter by lazy {
        OrderAdapter(viewModel.couponHandler)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        setUpBinding(binding)
        setUpSystemBar()

        viewModel.uiState.observe(this) { value ->
            orderAdapter.submitItems(value)
        }
    }

    private fun setUpBinding(binding: ActivityOrderBinding) {
        with(binding) {
            lifecycleOwner = this@OrderActivity
            adapter = orderAdapter
        }
    }

    private fun setUpSystemBar() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.text_payment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
