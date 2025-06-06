package woowacourse.shopping.feature.goodsdetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import woowacourse.shopping.R
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.account.AccountLocalDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.databinding.ActivityGoodsDetailsBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.GoodsUiModel
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.goodsdetails.GoodsDetailsViewModel.Companion.NULL_CART_ID
import woowacourse.shopping.util.toUi

class GoodsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoodsDetailsBinding
    private val goodsUiModel by lazy {
        IntentCompat.getParcelableExtra(intent, GOODS_KEY, GoodsUiModel::class.java)
            ?: throw IllegalArgumentException("GoodsUiModel is required")
    }

    private val cartId by lazy { intent.getIntExtra(SELECTED_ITEM_CART_ID_KEY, NULL_CART_ID) }

    private val viewModel: GoodsDetailsViewModel by viewModels {
        GoodsDetailsViewModelFactory(
            goodsUiModel,
            CartRepositoryImpl(CartRemoteDataSourceImpl(), AccountLocalDataSourceImpl(this)),
            GoodsRepositoryImpl(
                GoodsRemoteDataSourceImpl(),
                GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(this)),
            ),
            cartId,
        )
    }

    private val mostRecentItemCartId: Int by lazy {
        intent.getIntExtra(MOST_RECENT_ITEM_CART_ID_KEY, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        val source = intent.getStringExtra(EXTRA_SOURCE)
        if (source != SOURCE_RECENTLY_VIEWED) {
            viewModel.initMostRecentlyViewedGoods()
        }

        binding.viewModel = viewModel
        binding.quantityChangeListener =
            object : QuantityChangeListener {
                override fun onIncrease(cartItem: CartItem) {
                    viewModel.increaseSelectorQuantity()
                }

                override fun onDecrease(cartItem: CartItem) {
                    viewModel.decreaseSelectorQuantity()
                }
            }

        viewModel.alertEvent.observe(this) { goodsDetailsAlertMessage ->
            showMessage(
                getString(
                    goodsDetailsAlertMessage.resourceId,
                    goodsDetailsAlertMessage.quantity,
                ),
            )
        }

        viewModel.clickMostRecentlyGoodsEvent.observe(this) { mostRecentGoods ->
            val intent = newIntent(this, mostRecentGoods.toUi())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(EXTRA_SOURCE, SOURCE_RECENTLY_VIEWED)
            intent.putExtra(SELECTED_ITEM_CART_ID_KEY, mostRecentItemCartId)
            startActivity(intent)
        }
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

    private fun showMessage(message: String) {
        Toast
            .makeText(
                this,
                message,
                Toast.LENGTH_SHORT,
            ).show()
    }

    companion object {
        private const val GOODS_KEY = "GOODS"

        fun newIntent(
            context: Context,
            goods: GoodsUiModel,
        ): Intent =
            Intent(context, GoodsDetailsActivity::class.java).apply {
                putExtra(GOODS_KEY, goods)
            }

        const val EXTRA_SOURCE = "extra_source"
        const val SELECTED_ITEM_CART_ID_KEY = "cart_id"
        const val MOST_RECENT_ITEM_CART_ID_KEY = "most_cart_ids"
        const val SOURCE_RECENTLY_VIEWED = "recently_viewed"
        const val SOURCE_GOODS_LIST = "goods_list"
    }
}
