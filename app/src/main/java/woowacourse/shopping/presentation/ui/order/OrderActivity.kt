package woowacourse.shopping.presentation.ui.order

import android.os.Bundle
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.base.BindingActivity

class OrderActivity : BindingActivity<ActivityOrderBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    override fun initStartView(savedInstanceState: Bundle?) {
        title = getString(R.string.order_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
