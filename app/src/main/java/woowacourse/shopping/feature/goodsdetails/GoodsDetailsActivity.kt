package woowacourse.shopping.feature.goodsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityGoodsDetailsBinding
import woowacourse.shopping.feature.CustomCartQuantity
import woowacourse.shopping.feature.cart.CartActivity
import kotlin.getValue

class GoodsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsDetailsBinding
    private var id: Long = 0
    private val viewModel: GoodsDetailsViewModel by viewModels {
        val app = (application as ShoppingApplication)
        GoodsDetailsViewModelFactory(
            app.cartRepository,
            app.historyRepository,
            app.productRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getLongExtra(GOODS_KEY, 0)
        viewModel.loadProductDetails(id)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.lastViewed.observe(this) {
            viewModel.updateLastViewedVisibility()
        }

        observeCartInsertResult()
        setOnClickListener()
        navigateToLastViewed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_close -> {
                val resultIntent = Intent()
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToLastViewed() {
        viewModel.navigateToLastViewed.observe(this) { value ->
            if (value) {
                val id = viewModel.lastViewed.value?.id ?: 0L
                val intent =
                    newIntent(this@GoodsDetailsActivity, id.toLong()).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun observeCartInsertResult() {
        viewModel.isSuccess.observe(this) {
            Toast
                .makeText(
                    this,
                    R.string.goods_detail_cart_insert_success_toast_message,
                    Toast.LENGTH_SHORT,
                ).show()
            startActivity(CartActivity.newIntent(this))
        }
        viewModel.isFail.observe(this) {
            Toast
                .makeText(
                    this,
                    R.string.goods_detail_cart_insert_fail_toast_message,
                    Toast.LENGTH_SHORT,
                ).show()
        }
    }

    private fun setOnClickListener() {
        binding.customCartQuantity.setClickListener(
            object : CustomCartQuantity.CartQuantityClickListener {
                override fun onAddClick() {
                    viewModel.increaseQuantity()
                }

                override fun onRemoveClick() {
                    viewModel.decreaseQuantity()
                }
            },
        )
    }

    companion object {
        private const val GOODS_KEY = "GOODS_KEY"

        fun newIntent(
            context: Context,
            id: Long,
        ): Intent =
            Intent(context, GoodsDetailsActivity::class.java).apply {
                putExtra(GOODS_KEY, id)
            }
    }
}
