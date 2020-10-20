package com.enping.transformers.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.enping.transformers.R
import com.enping.transformers.data.model.Transformer
import kotlinx.android.synthetic.main.layout_rating_view.view.*
import kotlinx.android.synthetic.main.layout_transformer_item.view.*

class TransformerAdapter : RecyclerView.Adapter<TransformerAdapter.TransformerVH>() {

    var transformers = listOf<Transformer>()
    var removeClickListener : (id: String) -> Unit = {}
    var editClickListener: (id:String) -> Unit = {}

    class TransformerVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.view_name_item
        val strength: TextView = itemView.view_strength_item.tv_rating_value_rating_view
        val intelligence: TextView = itemView.view_intelligence_item.tv_rating_value_rating_view
        val speed: TextView = itemView.view_speed_item.tv_rating_value_rating_view
        val endurance: TextView = itemView.view_endurance_item.tv_rating_value_rating_view
        val rank: TextView = itemView.view_rank_item.tv_rating_value_rating_view
        val courage: TextView = itemView.view_courage_item.tv_rating_value_rating_view
        val firepower: TextView = itemView.view_firepower_item.tv_rating_value_rating_view
        val skill: TextView = itemView.view_skill_item.tv_rating_value_rating_view
        val team: TextView = itemView.view_team_item.tv_rating_value_rating_view
        val btnEdit: Button = itemView.btn_edit_transformer_list_fragment
        val btnRemove: Button = itemView.btn_remove_transformer_list_fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformerVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_transformer_item, parent, false)
        return TransformerVH(view)
    }

    override fun getItemCount(): Int {
        return transformers.size
    }

    override fun onBindViewHolder(holder: TransformerVH, position: Int) {
        bind(holder, transformers[position])
    }

    private fun bind(holder: TransformerVH, transformer: Transformer) {
        holder.name.text = transformer.name
        holder.strength.text = transformer.strength.toString()
        holder.intelligence.text = transformer.intelligence.toString()
        holder.speed.text = transformer.speed.toString()
        holder.endurance.text = transformer.endurance.toString()
        holder.rank.text = transformer.rank.toString()
        holder.courage.text = transformer.courage.toString()
        holder.firepower.text = transformer.firepower.toString()
        holder.skill.text = transformer.skill.toString()
        holder.team.text = transformer.enumTeam.name
        holder.btnEdit.setOnClickListener {
            editClickListener.invoke(transformers[holder.adapterPosition].id)
        }
        holder.btnRemove.setOnClickListener {
            removeClickListener.invoke(transformers[holder.adapterPosition].id)
        }
        Glide.with(holder.itemView.context)
            .load(transformer.teamIcon)
            .into(holder.itemView.iv_team_icon_item_list_fragment)
    }
}