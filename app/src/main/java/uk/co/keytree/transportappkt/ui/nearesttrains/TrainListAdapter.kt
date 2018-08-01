package uk.co.keytree.transportappkt.ui.nearesttrains

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.databinding.ItemStationBinding
import uk.co.keytree.transportappkt.model.Member

class TrainListAdapter(val clickListener: (Member) -> Unit): RecyclerView.Adapter<TrainListAdapter.ViewHolder>() {
    var stationList:List<Member> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainListAdapter.ViewHolder {
        val binding: ItemStationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_station, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainListAdapter.ViewHolder, position: Int) {
        holder.bind(stationList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return stationList.size
    }

    fun updatePostList(stationList:List<Member>){
        this.stationList = stationList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemStationBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = NearestTrainViewModel()
        fun bind(member:Member, clickListener: (Member) -> Unit){
            viewModel.bind(member)
            binding.root.setOnClickListener{clickListener(member)}
            binding.viewModel = viewModel
        }
    }
}