package ru.psuti.apache1337.homeowners.presentation.requests.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import ru.psuti.apache1337.homeowners.R
import ru.psuti.apache1337.homeowners.databinding.ListRequestsItemBinding
import ru.psuti.apache1337.homeowners.domain.requests.model.RequestModel
import ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestsFragmentDirections
import java.time.format.DateTimeFormatter


class RequestItemsAdapter(
    private val navController: NavController
) : RecyclerView.Adapter<RequestItemsAdapter.MyViewHolder>() {

    private var objects: List<RequestModel> = listOf()

    fun addElement(requests: List<RequestModel>) {
        objects = requests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(objects[position], navController)
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ListRequestsItemBinding.bind(itemView)

        fun bind(request: RequestModel, navController: NavController) {
            binding.apply {
                theme.text = request.theme
                status.text = request.status.text
                status.setTextColor(status.context.resources.getColor(request.status.color, null))
                date.text = request.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                itemView.setOnClickListener {
                    val action =
                        RequestsFragmentDirections.actionRequestFragmentToOpenedRequestFragment(
                            request
                        )
                    navController.navigate(action)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): MyViewHolder {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_requests_item, parent, false)
                return MyViewHolder(itemView)
            }
        }
    }

}