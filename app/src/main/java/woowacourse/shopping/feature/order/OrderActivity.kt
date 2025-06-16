package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.cart.adapter.CartGoodsItem

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            carts: List<CartGoodsItem>,
        ): Intent =
            Intent(context, CartActivity::class.java).apply {
                putExtra(EXTRA_CARTS, ArrayList(carts))
            }

        const val EXTRA_CARTS = "carts"
    }
}
