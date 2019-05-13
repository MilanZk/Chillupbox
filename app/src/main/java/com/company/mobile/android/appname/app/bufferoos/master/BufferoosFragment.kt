package com.company.mobile.android.appname.app.bufferoos.master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.company.mobile.android.appname.model.bufferoo.Bufferoo
import com.company.mobile.android.appname.app.R
import com.company.mobile.android.appname.app.bufferoos.viewmodel.BufferoosState
import com.company.mobile.android.appname.app.bufferoos.viewmodel.BufferoosState.Error
import com.company.mobile.android.appname.app.bufferoos.viewmodel.BufferoosState.Loading
import com.company.mobile.android.appname.app.bufferoos.viewmodel.BufferoosState.Success
import com.company.mobile.android.appname.app.bufferoos.viewmodel.BufferoosViewModel
import com.company.mobile.android.appname.app.common.widget.empty.EmptyListener
import com.company.mobile.android.appname.app.common.widget.error.ErrorListener
import kotlinx.android.synthetic.main.fragment_bufferoos.ev_bufferoos_empty_view
import kotlinx.android.synthetic.main.fragment_bufferoos.ev_bufferoos_error_view
import kotlinx.android.synthetic.main.fragment_bufferoos.pb_bufferoos_progress
import kotlinx.android.synthetic.main.fragment_bufferoos.rv_bufferoos
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class BufferoosFragment : Fragment() {

    private val bufferoosAdapter: BufferoosAdapter by inject()
    val bufferoosViewModel: BufferoosViewModel by viewModel()

    companion object {
        fun newInstance() = BufferoosFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_bufferoos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // At this point, Kotlin extensions are available
        earlyInitializeViews()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initializeState(savedInstanceState)
        initializeViews()
        initializeContents()
    }

    /**
     * View initialization that does not depend on view models.
     */
    private fun earlyInitializeViews() {
        setupBrowseRecycler()
        setupViewListeners()
    }

    /**
     * Initializes fragment state with [androidx.lifecycle.ViewModel]s and parameters passed through [Bundle].
     */
    private fun initializeState(savedInstanceState: Bundle?) {
    }

    /**
     * View initialization that depends on view models.
     */
    private fun initializeViews() {
        bufferoosViewModel.getBufferoos().observe(this,
            Observer<BufferoosState> {
                if (it != null) this.handleDataState(it)
            }
        )
    }

    /**
     * Initializes view contents.
     */
    private fun initializeContents() {
        bufferoosViewModel.fetchBufferoos()
    }

    private fun setupBrowseRecycler() {
        rv_bufferoos.layoutManager = LinearLayoutManager(this.context)
        rv_bufferoos.adapter = bufferoosAdapter
    }

    private fun handleDataState(bufferoosState: BufferoosState) {
        when (bufferoosState) {
            is Loading -> setupScreenForLoadingState()
            is Success -> setupScreenForSuccess(bufferoosState.data)
            is Error -> setupScreenForError(bufferoosState.errorMessage)
        }
    }

    private fun setupScreenForLoadingState() {
        pb_bufferoos_progress.visibility = View.VISIBLE
        rv_bufferoos.visibility = View.GONE
        ev_bufferoos_empty_view.visibility = View.GONE
        ev_bufferoos_error_view.visibility = View.GONE
    }

    private fun setupScreenForSuccess(data: List<Bufferoo>?) {
        ev_bufferoos_error_view.visibility = View.GONE
        pb_bufferoos_progress.visibility = View.GONE
        if (data != null && data.isNotEmpty()) {
            updateListView(data)
            rv_bufferoos.visibility = View.VISIBLE
        } else {
            ev_bufferoos_empty_view.visibility = View.VISIBLE
        }
    }

    private fun updateListView(bufferoos: List<Bufferoo>) {
        bufferoosAdapter.bufferoos = bufferoos
        bufferoosAdapter.notifyDataSetChanged()
    }

    private fun setupScreenForError(message: String?) {
        pb_bufferoos_progress.visibility = View.GONE
        rv_bufferoos.visibility = View.GONE
        ev_bufferoos_empty_view.visibility = View.GONE
        ev_bufferoos_error_view.visibility = View.VISIBLE
    }

    private fun setupViewListeners() {
        ev_bufferoos_empty_view.emptyListener = emptyListener
        ev_bufferoos_error_view.errorListener = errorListener
    }

    private val emptyListener = object : EmptyListener {
        override fun onCheckAgainClicked() {
            bufferoosViewModel.fetchBufferoos()
        }
    }

    private val errorListener = object : ErrorListener {
        override fun onTryAgainClicked() {
            bufferoosViewModel.fetchBufferoos()
        }
    }
}
