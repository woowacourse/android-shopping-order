package woowacourse.shopping.presentation.ui.myPage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.defaultRepository.DefaultChargeRepository
import woowacourse.shopping.databinding.ActivityMyPageBinding
import woowacourse.shopping.presentation.ui.order.OrderActivity

class MyPageActivity : AppCompatActivity(), MyPageContract.View {
    private lateinit var binding: ActivityMyPageBinding
    private val presenter: MyPagePresenter by lazy {
        MyPagePresenter(this, DefaultChargeRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)

        setContentView(binding.root)
        presenter.fetchCharge()
        binding.buttonMyPageRecharge.setOnClickListener { requestRecharge() }
        binding.buttonMyPageOrders.setOnClickListener { showOrders() }
    }

    override fun showCharge(amount: Int) {
        binding.textMyPageChangeValue.text = getString(R.string.detailPriceFormat, amount)
    }

    override fun requestRecharge() {
        if (binding.editMyPageRechargeValue.text.isNullOrEmpty()) return
        presenter.recharge(binding.editMyPageRechargeValue.text.toString().toInt())
    }

    override fun showError(message: String) {
        Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_SHORT).show()
        Log.e(TAG, message)
    }

    private fun showOrders() {
        startActivity(Intent(this, OrderActivity::class.java))
    }

    companion object {
        const val TAG = "MyPageActivity"
    }
}
