package com.mds.mapschallenge.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.mds.mapschallenge.R
import com.mds.mapschallenge.adapter.RecordListAdapter
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.viewmodel.RecordListViewModel
import kotlinx.android.synthetic.main.record_list_fragment.*
import java.lang.Override as Override1

class RecordListFragment : Fragment() {

    companion object {
        fun newInstance() = RecordListFragment()
    }

    private lateinit var viewModel: RecordListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.record_list_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            viewModel = ViewModelProvider(this@RecordListFragment).get(RecordListViewModel::class.java)
            var observer : Observer<List<Routes>> =
                Observer {
                        routes ->
                    var manager = LinearLayoutManager(activity!!,LinearLayoutManager.VERTICAL,false) as RecyclerView.LayoutManager
                    rvRouteList.layoutManager = manager
                    rvRouteList.adapter = RecordListAdapter(MutableLiveData(routes))
                }

        viewModel.getRoutes(requireActivity().application)?.observe(viewLifecycleOwner, observer)

        swipeRoutes.setOnRefreshListener {
            viewModel.getRoutes(requireActivity().application)?.observe(viewLifecycleOwner, observer)
            swipeRoutes.isRefreshing = false
        }

    }
}
