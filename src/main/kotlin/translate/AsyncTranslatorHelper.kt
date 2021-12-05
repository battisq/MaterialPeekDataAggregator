package translate

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.reactivestreams.Publisher
import parser.models.Page
import utils.FileUtils
import utils.JsonUtils.toJsonString
import utils.KotlinUtils
import utils.KotlinUtils.mapAsync
import java.util.*
import kotlin.collections.ArrayList

object AsyncTranslatorHelper {

    const val countAsyncThread = 6

    private val translateHelpers = hashMapOf<Int, TranslateHelper>().apply {
        repeat(countAsyncThread) { index ->
            this[index] = TranslateHelper()
        }
    }

    fun List<Page>.asyncTranslate(onComplete: (List<Page>) -> Unit, log: (String) -> Unit) {
        val tripleList = mapIndexed { index, page -> Triple(index, index % countAsyncThread, page) }
        val resultList = ArrayList<Pair<Int, Page>>(tripleList.size)

        Flowable.fromIterable(tripleList)
            .parallel(countAsyncThread)
            .runOn(Schedulers.computation())
            .concatMap {
                Flowable.just(it.also { log(it.first.toString()) })
            }
            .map { pair ->
                val translateHelper = translateHelpers[pair.second]!!

                val page = pair.third
                val header = translateHelper.translate(page.header)

                log(pair.third.text)

                val text = translateHelper.translate(
                    separator = "\n\n",
                    translatableBlocks = (page.text
                        .split("\n\n")
                        .toTypedArray())
                )

                val mappedPair = pair.first to Page(
                    header,
                    text,
                    page.nextLink,
                    page.previousLink
                )

                mappedPair.also { Completable.complete() }
            }
            .sequential()
            .blockingIterable()
            .let(resultList::addAll)

            resultList
                .apply { sortBy { it.first } }
                .map { it.second }
                .let { onComplete(it) }
    }
}