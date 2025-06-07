package woowacourse.shopping.feature.goodsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import woowacourse.shopping.R
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityGoodsDetailsBinding
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.feature.CustomCartQuantity
import woowacourse.shopping.feature.CustomLastViewed
import woowacourse.shopping.feature.model.ResultCode
import woowacourse.shopping.feature.model.State
import kotlin.getValue

class GoodsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsDetailsBinding
    private var id: Long = 0
    private val viewModel: GoodsDetailsViewModel by viewModels {
        (application as ShoppingApplication).goodsDetailsFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getLongExtra(GOODS_KEY, 0)
        lifecycleScope.launch {
            viewModel.setInitialCart(id)
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        sendHistoryResult()
        setOnClickListener()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_close -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendHistoryResult() {
        setResult(
            ResultCode.GOODS_DETAIL_INSERT.code,
            Intent().apply {
                putExtra(HISTORY_ID, id)
            },
        )
    }

    private fun setupObservers() {
        observeCartInsertResult()
        observeNavigateToLastViewedCart()
    }

    private fun observeNavigateToLastViewedCart() {
        viewModel.navigateToLastViewedCart.observe(this) { lastViewedCart ->
            navigateToLastViewed(lastViewedCart)
        }
    }

    private fun observeCartInsertResult() {
        viewModel.insertState.observe(this) { event ->
            event.getContentIfNotHandled()?.let { state ->
                when (state) {
                    is State.Success -> {
                        Toast.makeText(this, R.string.goods_detail_cart_insert_success_toast_message, Toast.LENGTH_SHORT).show()
                        setResult(
                            ResultCode.GOODS_DETAIL_INSERT.code,
                            Intent().apply {
                                putExtra(CART_ID, viewModel.cartProduct.value?.id)
                                putExtra(GOODS_ID, id)
                                putExtra(GOODS_QUANTITY, viewModel.cartProduct.value?.quantity)
                            },
                        )
                    }
                    is State.Failure ->
                        Toast.makeText(this, R.string.goods_detail_cart_insert_fail_toast_message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToLastViewed(lastViewedCart: History) {
        lastViewedCart.let {
            val intent = newIntent(this@GoodsDetailsActivity, it.id)
            startActivity(intent)
            finish()
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
        binding.customLastViewed.setClickListener(
            object : CustomLastViewed.LastViewedClickListener {
                override fun navigate() {
                    viewModel.emitLastViewedCart()
                }
            },
        )
    }

    companion object {
        private const val GOODS_KEY = "GOODS_KEY"
        private const val CART_ID = "CART_ID"
        private const val GOODS_ID = "GOODS_ID"
        private const val GOODS_QUANTITY = "GOODS_QUANTITY"
        private const val HISTORY_ID = "HISTORY_ID"

        fun newIntent(
            context: Context,
            id: Long,
        ): Intent =
            Intent(context, GoodsDetailsActivity::class.java).apply {
                putExtra(GOODS_KEY, id)
            }
    }
}
