package com.mds.mapschallenge.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mds.mapschallenge.R
import com.mds.mapschallenge.model.Routes
import com.mds.mapschallenge.ui.activity.DetailsActivity
import com.mds.mapschallenge.viewmodel.RecordListViewModel
import kotlinx.android.synthetic.main.item_routes.view.*

class RecordListAdapter(val routes: MutableLiveData<List<Routes>>) : RecyclerView.Adapter<RecordListAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routes, parent, false))

    override fun getItemCount(): Int {
        return if (routes!=null) routes.value!!.size else 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bindView(routes.value!!.get(position))

    class Holder(itemView : View): RecyclerView.ViewHolder(itemView){
        fun bindView(items : Routes){
            with(items){

                var vNombre = "Ruta: ${RouteName}"
                var vKm     = "Kilometros Recorridos: ${(KmTraveled/1000)}"
                var vTime   = "Tiempo: ${TimeTraveled}"

                itemView.tvRouteName.text   = vNombre
                itemView.tvKmTraveled.text  = vKm
                itemView.clItemsRoutes.setOnClickListener {
                    if (itemView!=null){


                    var intent = Intent(itemView.context,DetailsActivity::class.java)
                    intent.putExtra("Id",Id)
                    intent.putExtra("RouteName",vNombre)
                    intent.putExtra("KmTraveled",vKm)
                    intent.putExtra("TimeTraveled",vTime)
                    intent.putExtra("TrackPoints",TrackPoints)
                    var p1: Pair<View, String> = Pair.create(itemView.tvRouteName,"transitionName")
                    var p2: Pair<View, String> = Pair.create(itemView.tvKmTraveled,"transitionKm")


                    val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,p1,p2)

                    itemView.context.startActivity(intent,options.toBundle())
                    }
                }
            }
        }
    }

}