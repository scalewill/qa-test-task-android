package com.flatstack.qatesttask.feature.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import by.kirich1409.viewbindingdelegate.viewBinding
import com.flatstack.qatesttask.R
import com.flatstack.qatesttask.databinding.FragmentNewsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NewsFragment : Fragment(R.layout.fragment_news) {

    private val binding: FragmentNewsBinding by viewBinding()

    private val viewModel: NewsFragmentViewModel by viewModel()
    private val httpExceptionHandler: (IOException) -> Unit = { exception ->
        when (exception) {
            is UnknownHostException ->
                Snackbar.make(binding.root, getString(R.string.bad_gateway_error), Snackbar.LENGTH_LONG).show()
            is SocketTimeoutException ->
                Snackbar.make(binding.root, getString(R.string.timeout_error), Snackbar.LENGTH_LONG).show()
        }
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerViewNewsFragmentNews
        val dividerItemDecoration = DividerItemDecoration(
            activity,
            VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
        with(viewModel) {
            setNewsLanguage(get())
            val newsAdapter = NewsAdapter(onClickListener = {
                val nextPage: String? = currentNewsList.value?.run {
                    this.toList()[indexOf(it) + 1].url
                }
                NewsFragmentDirections.actionNewsFragmentToBrowserFragment(it.url, nextPage).let { directions ->
                    findNavController().navigate(directions)
                }
            }, onBottomReachedListener = {
                Timber.d("the bottom had been reached")
            })
            recyclerView.adapter = newsAdapter
            currentNewsList.observe(viewLifecycleOwner) { list ->
                if (list.isNotEmpty())
                    newsAdapter.submitList(list.toList())
            }
            currentPageInfo.observe(viewLifecycleOwner) {
                if (it.currentPage == it.pages) {
                    binding.floatingActionButtonGetMoreNews.hide()
                }
            }
            requestIsLoading.observe(viewLifecycleOwner) {
                binding.floatingActionButtonGetMoreNews.isEnabled = it == false
            }
            getInitialSection(httpExceptionHandler)
            binding.floatingActionButtonGetMoreNews.setOnClickListener {
                getNextSection(httpExceptionHandler)
            }
        }
    }
}
