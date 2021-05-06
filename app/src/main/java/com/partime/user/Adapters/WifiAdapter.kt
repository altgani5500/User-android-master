package com.partime.user.Adapters

import android.content.Context
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.partime.user.Listeners.WifiListener
import com.partime.user.R
import kotlinx.android.synthetic.main.add_network_layout.view.*
import kotlin.collections.ArrayList

class WifiAdapter(var networks: ArrayList<String>, internal var context: Context?,listener : WifiListener) :
    RecyclerView.Adapter<WifiAdapter.MyViewHolder>() {

    var listener:WifiListener

    init {
        this.listener=listener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view: View

        init {
            this.view = view
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.add_network_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        val network = networks[position]

        viewHolder.view.txtNetworkName.setText(network)

        viewHolder.view.cardViewWifiNetwork.setOnClickListener{

            listener.onWifiNetworkSelect(network)

        }

    }

    override fun getItemCount(): Int {
        return networks.size
    }

}

