package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.RetrofitBuilder
import woowacourse.shopping.data.respository.card.CardDAO
import woowacourse.shopping.data.respository.card.CardRepositoryImpl
import woowacourse.shopping.data.respository.cart.CartRepositoryImpl
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.respository.point.PointRepositoryImpl
import woowacourse.shopping.data.respository.point.source.remote.PointRemoteDataSourceImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.CardModel
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.model.PointModel
import woowacourse.shopping.presentation.view.order.adapter.CardListAdapter
import woowacourse.shopping.presentation.view.order.adapter.OrderProductListAdapter
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailActivity
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    private lateinit var retrofitBuilder: RetrofitBuilder

    private lateinit var presenter: OrderContract.Presenter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        val cartIds = intent.getSerializableCompat<ArrayList<Long>>(KEY_CART_IDS) ?: return finish()
        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()
        retrofitBuilder = RetrofitBuilder.getInstance(url, token)

        setToolbar()
        setPresenter()
        setOrderPayButtonClick()
        presenter.loadOrderProducts(cartIds)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }

    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.toolbar_title_order)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setPresenter() {
        val cartLocalDataSource = CartLocalDataSourceImpl(this, url)
        val cartRemoteDataSource = CartRemoteDataSourceImpl(retrofitBuilder.createCartService())
        val pointRemoteDataSource = PointRemoteDataSourceImpl(retrofitBuilder.createPointService())
        val orderRemoteDataSource = OrderRemoteDataSourceImpl(retrofitBuilder.createOrderService())

        presenter = OrderPresenter(
            this,
            cardRepository = CardRepositoryImpl(CardDAO),
            cartRepository = CartRepositoryImpl(cartLocalDataSource, cartRemoteDataSource),
            pointRepository = PointRepositoryImpl(pointRemoteDataSource),
            orderRepository = OrderRepositoryImpl(orderRemoteDataSource),
        )
    }

    override fun setPointTextChangeListener(orderPrice: Int, userPoint: PointModel) {
        binding.etOrderUsePoint.addTextChangedListener(
            PointTextWatcher(
                orderPrice,
                userPoint,
                binding.etOrderUsePoint,
                presenter::setUsePoint
            )
        )
    }

    override fun setLayoutVisibility() {
        binding.layoutSkeletonOrder.post {
            binding.layoutSkeletonOrder.visibility = View.GONE
        }

        binding.layoutOrder.post {
            binding.layoutOrder.visibility = View.VISIBLE
        }
    }

    override fun showProductItemsView(products: List<CartProductModel>) {
        binding.rvOrderProductList.post {
            binding.rvOrderProductList.adapter = OrderProductListAdapter(products)
        }
    }

    override fun showCardItemsView(cards: List<CardModel>) {
        binding.rvOrderCardList.post {
            binding.rvOrderCardList.adapter = CardListAdapter(cards)
        }
    }

    override fun showUserPointView(userPoint: PointModel) {
        binding.availablePoint = userPoint
    }

    override fun showUsePointView(usePoint: PointModel) {
        binding.usePoint = usePoint
    }

    override fun showSavePredictionPointView(savePredictionPoint: PointModel) {
        binding.savingPoint = savePredictionPoint
    }

    override fun showOrderPriceView(orderPrice: Int) {
        binding.orderPrice = orderPrice
    }

    private fun setOrderPayButtonClick() {
        binding.btOrderPay.setOnClickListener {
            presenter.postOrder()
        }
    }

    override fun showOrderDetailView(orderId: Long) {
        val intent = OrderDetailActivity.createIntent(this, orderId, url, token)
        startActivity(intent)
        finish()
    }

    override fun handleErrorView(message: String) {
        binding.root.post {
            showToast(message)
            finish()
        }
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"

        fun createIntent(
            context: Context,
            cartIds: ArrayList<Long>,
            url: Server.Url,
            token: Server.Token
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(KEY_CART_IDS, cartIds)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)
            return intent
        }
    }
}
