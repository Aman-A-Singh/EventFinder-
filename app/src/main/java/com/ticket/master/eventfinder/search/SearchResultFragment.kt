package com.ticket.master.eventfinder.search

import android.app.appsearch.SearchResult
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ticket.master.eventfinder.R
import com.ticket.master.eventfinder.adapter.SearchResultRecyclerViewAdapter
import com.ticket.master.eventfinder.databinding.FragmentSearchResultBinding
import com.ticket.master.eventfinder.models.EventData

class SearchResultFragment : Fragment() {

    private lateinit var viewModel: SearchResultFragmentViewModel
    private lateinit var _binding: FragmentSearchResultBinding
    private val binding get() = _binding!!

    private lateinit var eventListAdapter: SearchResultRecyclerViewAdapter

    private val args: SearchResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SearchResultFragmentViewModel::class.java]
        binding.searchResultToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        //Set up the recycler view
        configureRecyclerView()
    }

    private fun configureRecyclerView() {
        eventListAdapter =
            SearchResultRecyclerViewAdapter(requireActivity().findNavController(R.id.fragmentContainerView))
        binding.eventListRecyclerView.adapter = eventListAdapter
        binding.eventListRecyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.getEvents(args.distance, "Default", args.keyword, args.geoHash)
        viewModel.eventList.observe(viewLifecycleOwner) { eventList ->
            eventListAdapter.submitList(eventList)
        }
    }

}