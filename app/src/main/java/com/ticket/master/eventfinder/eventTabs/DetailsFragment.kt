package com.ticket.master.eventfinder.eventTabs

import android.content.Intent
import android.graphics.Paint
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ticket.master.eventfinder.R
import com.ticket.master.eventfinder.databinding.FragmentDetailsBinding
import com.ticket.master.eventfinder.eventDetails.EventDetailsViewModel
import com.ticket.master.eventfinder.models.event.EventDetails
import com.ticket.master.eventfinder.util.UIState
import java.text.SimpleDateFormat

class DetailsFragment : Fragment() {

    private lateinit var _binding: FragmentDetailsBinding
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EventDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.artistTeamsTxt.isSelected = true
        binding.buyTicketAtText.isSelected = true
        binding.buyTicketAtText.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                UIState.INPROGREES -> binding.eventDetailsCardView.visibility = View.GONE
                UIState.COMPLETED -> {
                    binding.eventDetailsCardView.visibility = View.VISIBLE
                    bind()
                }

                UIState.ERROR -> binding.artistTeamsTxt.text = "Errorrrrrrrrr"
            }
        }
    }

    private fun bind() {
        viewModel.eventData.let { event ->

            bindToolBar(event.name, event.url)

            binding.artistTeamsTxt.text = event.name
            binding.venueTxt.text = event._embedded.venues[0].name
            val dateFormat = SimpleDateFormat("MMM dd,yyyy")
            binding.dateTxt.text = dateFormat.format(event.dates.start.date)
            val timeFormat = SimpleDateFormat("h:mm a")
            binding.timeTxt.text = timeFormat.format(event.dates.start.time)

            bindGenre(event.classifications)

            bindBuyTicketURL(event.url)

            if (event.priceRanges != null) {
                binding.priceRangeTxt.text =
                    event.priceRanges[0].min.toString() + " - " + event.priceRanges[0].max.toString() + " (" + event.priceRanges[0].currency + ")"
            } else {
                binding.priceRangeTxt.visibility = View.GONE
                binding.priceRange.visibility = View.GONE
            }
            Glide.with(binding.seatImage.context)
                .load(event.seatmap.staticUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.seatImage)

            bindTicketStatus(event.dates.status.code)
        }
    }

    private fun bindGenre(classifications: List<EventDetails.Classification>) {
        var genre = ArrayList<String>()
        if (classifications[0].segment != null) {
            if (!classifications[0].segment!!.name.equals("Undefined")) {
                genre.add(classifications[0].segment!!.name)
            }
        }
        if (classifications[0].genre != null) {
            if (!classifications[0].genre!!.name.equals("Undefined")) {
                genre.add(classifications[0].genre!!.name)
            }
        }
        if (classifications[0].subGenre != null) {
            if (!classifications[0].subGenre!!.name.equals("Undefined")) {
                genre.add(classifications[0].subGenre!!.name)
            }
        }
        if (classifications[0].type != null) {
            if (!classifications[0].type!!.name.equals("Undefined")) {
                genre.add(classifications[0].type!!.name)
            }
        }
        if ((classifications[0].subType != null)) {
            if (!classifications[0].subType!!.name.equals("Undefined")) {
                genre.add(classifications[0].subType!!.name)
            }
        }
        binding.genresTxt.isSelected = true
        binding.genresTxt.text = genre.joinToString("|")
    }

    private fun bindToolBar(name: String, url: String?) {
        val toolbarTitle =
            requireParentFragment().view?.findViewById<TextView>(R.id.toolBar_title)
        toolbarTitle?.isSelected = true
        toolbarTitle?.text = name
        val toolbar =
            requireParentFragment().view?.findViewById<Toolbar>(R.id.eventDetailsToolBar)

        if (url != null) {
            toolbar?.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.faceBook -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("https://www.facebook.com/sharer/sharer.php?u=${url}")
                        startActivity(intent)
                        true
                    }

                    R.id.twitter -> {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data =
                            Uri.parse("http://twitter.com/share?text=Check ${name} Tour on Ticketmaster.&url=${url}")
                        intent.setPackage("com.android.chrome")
                        startActivity(intent)
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun bindTicketStatus(code: String) {
        val drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.ticket_status_background)
        var color = android.R.color.transparent
        when (code) {
            "onsale" -> {
                binding.ticketStatusTxt.text = "On Sale"
                color = resources.getColor(R.color.onsale)
            }

            "offsale" -> {
                binding.ticketStatusTxt.text = "Off Sale"
                color = resources.getColor(R.color.offsale)
            }

            "cancelled" -> {
                binding.ticketStatusTxt.text = "Cancelled"
                color = resources.getColor(R.color.cancelled)
            }

            "postponed" -> {
                binding.ticketStatusTxt.text = "Postponed"
                color = resources.getColor(R.color.postponed_rescheduled)
            }

            "rescheduled" -> {
                binding.ticketStatusTxt.text = "Rescheduled"
                color = resources.getColor(R.color.postponed_rescheduled)
            }
        }
        drawable?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        binding.ticketStatusTxt.background = drawable
    }

    private fun bindBuyTicketURL(url: String?) {
        if (url != null) {
            binding.buyTicketAtText.text = url
            binding.buyTicketAtText.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        } else {
            binding.buyTicketAtText.visibility = View.GONE
            binding.buyTicket.visibility = View.GONE
        }
    }

}