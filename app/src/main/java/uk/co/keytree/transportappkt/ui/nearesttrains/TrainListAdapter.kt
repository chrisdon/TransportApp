package uk.co.keytree.transportappkt.ui.nearesttrains

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.databinding.ItemStationBinding
import uk.co.keytree.transportappkt.model.Station
import uk.co.keytree.transportappkt.utils.extension.getParentActivity

class TrainListAdapter: RecyclerView.Adapter<TrainListAdapter.ViewHolder>() {
    private lateinit var stationList:List<Station>
    val tapped = MutableLiveData<Station>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainListAdapter.ViewHolder {
        val binding: ItemStationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_station, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainListAdapter.ViewHolder, position: Int) {
        holder.bind(stationList[position], tapped)
    }

    override fun getItemCount(): Int {
        return if(::stationList.isInitialized) stationList.size else 0
    }

    fun updatePostList(stationList:List<Station>){
        this.stationList = stationList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemStationBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = NearestTrainViewModel()
        fun bind(station:Station, tapped: MutableLiveData<Station>){
            viewModel.bind(station)
            val parentActivity: AppCompatActivity? = binding.root.getParentActivity()
            if(parentActivity != null) {
                viewModel.tapped.observe(parentActivity, Observer { value -> tapped.value = value})
            }
            binding.viewModel = viewModel
        }
    }
}