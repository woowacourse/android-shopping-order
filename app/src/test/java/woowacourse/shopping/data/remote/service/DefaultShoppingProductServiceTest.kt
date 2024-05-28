package woowacourse.shopping.data.remote.service

//class DefaultShoppingProductServiceTest {
//    private lateinit var testExecutors: ExecutorService
//
//    @BeforeEach
//    fun setUp() {
//        testExecutors = Executors.newSingleThreadExecutor()
//    }
//
//    @Test
//    @DisplayName("상품이 20개 있을 때, page 1 에 해당하는 10개의 상품을 가져올 수 있다.")
//    fun `paging`() {
//        // given
//        val totalProducts = fakeProductResponses(20)
//        val expectSize = 10
//        val expect = fakeProductResponses(10)
//        // when
//        val actual =
//            DefaultShoppingProductService(testExecutors, totalProducts)
//                .fetchProducts(1, 10)
//                .content
//        // then
//        assertSoftly {
//            actual shouldBe expect
//            actual shouldHaveSize expectSize
//        }
//    }
//}
