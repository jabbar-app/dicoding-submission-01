package id.jabbar.githubusers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.jabbar.githubusers.UserAdapter.RecyclerViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class UserAdapter (private val users: ArrayList<User>) : RecyclerView.Adapter<RecyclerViewHolder>(), Filterable {

    private lateinit var onItemClickDetail: OnItemClickCallBack
    private lateinit var onItemClickShare: OnItemClickCallBack
    private var filterUsers: ArrayList<User> = users

    fun setOnItemClickCallback(onItemClickCallBack: OnItemClickCallBack) {
        this.onItemClickDetail = onItemClickCallBack
        this.onItemClickShare = onItemClickCallBack
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val itemSearch = constraint.toString()
                filterUsers = if (itemSearch.isEmpty()) users else {
                    val itemList = ArrayList<User>()
                    for (item in users) {
                        val name = item.name?.lowercase(Locale.ROOT)?.contains(itemSearch.lowercase(
                            Locale.ROOT))
                        val userName = item.username?.lowercase(Locale.ROOT)?.contains(itemSearch.lowercase(
                            Locale.ROOT))
                        if (name!! || userName!!) {
                            itemList.add(item)
                        }
                    }
                    itemList
                }
                val filterResults = FilterResults()
                filterResults.values = filterUsers
                return filterResults
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterUsers = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecyclerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = filterUsers.size

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val user = filterUsers[position]
        Glide.with(holder.itemView)
            .load(user.avatar)
            .apply(RequestOptions().override(300, 300))
            .into(holder.ivAvatar)
        with(holder) {
            tvName.text = user.name
            tvUsername.text = user.username
            itemView.setOnClickListener { onItemClickDetail.onItemClicked(filterUsers[holder.adapterPosition]) }
            btnShare.setOnClickListener { onItemClickShare.onItemShared(filterUsers[holder.adapterPosition]) }
        }
    }

    inner class RecyclerViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivAvatar: CircleImageView = itemView.findViewById(R.id.ivAvatar)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        var btnShare: Button = itemView.findViewById(R.id.btnShare)
    }

    interface OnItemClickCallBack {
        fun onItemClicked(data: User)
        fun onItemShared(data: User)
    }
}