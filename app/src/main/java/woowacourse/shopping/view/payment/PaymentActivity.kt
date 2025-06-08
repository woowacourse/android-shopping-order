package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.view.payment.adapter.PaymentAdapter

class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }

    private lateinit var paymentAdapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpView()
        initRecyclerView()
        supportActionBar?.title = getString(R.string.pay)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setUpView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initRecyclerView() {
        paymentAdapter = PaymentAdapter()
        binding.rvPayment.adapter = paymentAdapter
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, PaymentActivity::class.java)
    }
}
