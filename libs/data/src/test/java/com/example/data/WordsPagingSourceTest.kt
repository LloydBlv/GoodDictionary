
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.testing.TestPager
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.example.data.AppDatabase
import com.example.data.TestData
import com.example.data.WordEntity
import com.example.data.WordsDao
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.TimeUnit


@RunWith(RobolectricTestRunner::class)
class WordsPagingSourceTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: WordsDao

    @JvmField
    @Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
        // At the end of all tests, query executor should be idle (transaction thread released).
        countingTaskExecutorRule.drainTasks(500, TimeUnit.MILLISECONDS)
        assertThat(countingTaskExecutorRule.isIdle).isTrue()
    }

    @Test
    fun test() = runTest {
        val source = dao.allWordsPaged()
        val pager = TestPager(PagingConfig(pageSize = 1000), source)
        val result = pager.refresh()
        assertThat(result is Page).isTrue()
        val page = pager.getLastLoadedPage()
        assertThat(page).isNotNull()
    }

    @Test
    fun test1() = runTest {
        val words = TestData.generateTestWords(dataSize = 5)
        dao.insertAll(words)
        assertThat(dao.getAllWords().all { it.sequence == 1L })
        val source = dao.allWordsPaged()
        val pager = TestPager(PagingConfig(pageSize = 1000), source)

        val result = pager.refresh() as Page

        assertThat(result.data)
            .isEqualTo(words)

        assertThat(
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 500,
                    placeholdersEnabled = false
                )
            )
        ).isEqualTo(
            Page(
                data = words,
                prevKey = null,
                nextKey = null,
                itemsAfter = 0,
                itemsBefore = 0
            )
        )

    }

    @Test
    fun test2() = runTest {
        val words = TestData.generateTestWords(dataSize = 100)
        dao.insertAll(words)
        assertThat(dao.getAllWords().all { it.sequence == 1L })
        val source = dao.allWordsPaged()
        val pager = TestPager(
            PagingConfig(
                pageSize = 2,
                prefetchDistance = 1,
                enablePlaceholders = false,
                initialLoadSize = 1
            ), source
        )

        val result: Page<Int, WordEntity> = pager.refresh() as Page

        assertThat(result.data)
            .isEqualTo(words.take(1))

        assertThat(
            source.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        ).isEqualTo(
            Page(
                data = words.take(2),
                prevKey = null,
                nextKey = 2,
                itemsAfter = 98,
                itemsBefore = 0
            )
        )

        val page = with(pager) {
            append()
            append()
            append()
        } as Page

        assertThat(page.data)
            .hasSize(2)

    }

}