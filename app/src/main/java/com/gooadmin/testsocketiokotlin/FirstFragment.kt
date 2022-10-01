package com.gooadmin.testsocketiokotlin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gooadmin.testsocketiokotlin.databinding.FragmentFirstBinding
import com.gooadmin.testsocketiokotlin.socket.io.SocketManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject

class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The following lines connects the Android app to the server.
        SocketManager.setSocket()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserDetails()
    }

    private fun getUserDetails() {
        val socketManager = SocketManager.getSocket()

        val arrayList = ArrayList<Int>()
        arrayList.add(1)
        arrayList.add(2)
        val jsonObject = JsonObject()
        jsonObject.addProperty("clientId", 1)
        val jsonArray = JsonArray()
        jsonArray.add(1)
        jsonArray.add(2)
        jsonObject.add("buses", jsonArray)
        Log.e("jsonObject", "jsonObject $jsonObject")

        socketManager.emit("join", jsonObject) // {"clientId: 1", "buses": [1,2}}

        socketManager.on(Socket.EVENT_CONNECT) {
            Log.e("EVENT_CONNECT", "args $it")
        }

        socketManager.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e("EVENT_CONNECT_ERROR", "args $args")
        }
        socketManager.on(Socket.EVENT_DISCONNECT) { args ->
            Log.e("EVENT_DISCONNECT", "args $args")
        }

        socketManager.on("bus-location-to-client") { args ->
            val data = args[0] as JSONObject
            Log.d("TAG", "Handling data $data" )
            try {
                val callFrom = data.getString("from")
                Log.d("TAG", "Call from : $callFrom")
            } catch (e: JSONException) {
                Log.d("TAG", "friend call object cannot be parsed")
            }
        }

        SocketManager.establishConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        // The following line disconnects the Android app to the server.
        SocketManager.closeConnection()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // The following line disconnects the Android app to the server.
        SocketManager.closeConnection()
    }
}