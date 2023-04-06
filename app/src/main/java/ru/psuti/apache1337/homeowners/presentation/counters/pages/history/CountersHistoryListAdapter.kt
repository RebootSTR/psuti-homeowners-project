package ru.psuti.apache1337.homeowners.presentation.counters.pages.history

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.CountersHistoryItemBinding
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterType
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CounterHistoryListAdapter(val data: List<CounterHistoryEntry>) :
    RecyclerView.Adapter<CounterHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CounterHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CountersHistoryItemBinding.inflate(inflater, parent, false)
        return CounterHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CounterHistoryViewHolder, position: Int) {
        val item = data[position]

        holder.date.text = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(
            ZoneId.systemDefault()
        ).format(item.date)
        holder.value.text = if (item.counter.type == CounterType.ELECTRICITY) "${item.counter.prev} кВт/ч" else "${item.counter.prev} м. куб."
        holder.type.text = item.counter.name

        val image = when (item.counter.type) {
            CounterType.WATER_HOT -> R.drawable.ic_water_hot
            CounterType.GAS -> R.drawable.ic_gas
            CounterType.WATER_COLD -> R.drawable.ic_water_cold
            CounterType.ELECTRICITY -> R.drawable.ic_electro
            else -> R.drawable.ic_gas
        }

        holder.icon.setImageResource(image)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}

class CounterHistoryViewHolder(binding: CountersHistoryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    val date: TextView = binding.date
    val value: TextView = binding.value
    val type: TextView = binding.type
    val icon: ImageView = binding.icon
}