package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.OrderProductsModel

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setToolBar()
    }

    private fun setUpBinding() {
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarOrder.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        private const val ORDER_PRODUCTS_KEY = "ORDER_PRODUCTS_KEY"
        fun getIntent(
            context: Context,
            orderProductsModel: OrderProductsModel
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(ORDER_PRODUCTS_KEY, orderProductsModel)
            return intent
        }
    }
}
