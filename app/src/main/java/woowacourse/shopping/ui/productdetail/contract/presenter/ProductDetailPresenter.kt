package woowacourse.shopping.ui.productdetail.contract.presenter

import com.example.domain.repository.CartRepository
import com.example.domain.repository.ProductDetailRepository
import com.example.domain.repository.RecentRepository
import woowacourse.shopping.mapper.toUIModel
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.ui.productdetail.contract.ProductDetailContract

class ProductDetailPresenter(
    private val view: ProductDetailContract.View,
    private val id: Long,
    private val repository: ProductDetailRepository,
    private val cartRepository: CartRepository,
    private val recentRepository: RecentRepository,
) : ProductDetailContract.Presenter {
    private var count = 1

    private var latestProduct: ProductUIModel? = null

    init {
        setUpProductDetail()
        setLatestProduct()
        addProductToRecent()
    }

    override fun setUpProductDetail() {
        repository.getById(
            id,
            onSuccess =
            { view.setProductDetail(it.toUIModel()) },
            onFailure = {},
        )
    }

    override fun addProductToCart() {
        cartRepository.findById(id, onSuccess = {
            if (it != null) {
                cartRepository.updateCount(it.id, count, {}, {})
            }
        })
    }
    override fun addProductToRecent() {
        recentRepository.findById(id)?.let {
            recentRepository.delete(it.id)
        }
        repository.getById(
            id,
            onSuccess =
            { recentRepository.insert(it) },
            onFailure = {},
        )
    }

    override fun setProductCountDialog() {
        repository.getById(
            id,
            onSuccess =
            { view.showProductCountDialog(it.toUIModel()) },
            onFailure = {},
        )
    }

    override fun setLatestProduct() {
        recentRepository.getRecent(1).firstOrNull()?.let { recent ->
            latestProduct = recent.toUIModel()
            view.showLatestProduct(latestProduct!!)
        }
    }

    override fun clickLatestProduct() {
        latestProduct?.let { view.navigateToDetail(it.id) }
    }

    override fun addProductCount(id: Long) {
        count++
        view.setProductCount(count)
    }

    override fun subtractProductCount(id: Long) {
        count--
        view.setProductCount(count)
    }
}
