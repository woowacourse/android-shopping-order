package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderDetailBinding

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderDetailActivity::class.java)
        }
    }
}
