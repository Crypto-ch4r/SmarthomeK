package mx.tecnm.cdhidalgo

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {
    lateinit var etSensor: EditText
    lateinit var etValor: EditText
    lateinit var bAdd: Button
    lateinit var bRefresh: Button
    lateinit var rvList: RecyclerView

    lateinit var sesion: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        sesion = getSharedPreferences("sesion", 0)
        supportActionBar!!.title = "Sensores:" + sesion.getString("user", "")

        etSensor = findViewById(R.id.etSensor)
        etValor = findViewById(R.id.etValor)
        bAdd = findViewById(R.id.bAdd)
        bRefresh = findViewById(R.id.bRefresh)
        rvList = findViewById(R.id.rvList)

        rvList.setHasFixedSize(true)
        rvList.itemAnimator = DefaultItemAnimator()
        rvList.layoutManager = LinearLayoutManager(this)

        llenar()

        bRefresh.setOnClickListener {llenar() }
        bAdd.setOnClickListener{ agregar() }
    }

    private fun agregar() {
        var url = Uri.parse("http://172.18.7.251/sensores")
            .buildUpon()
            .build().toString()

        val dato: JSONObject = JSONObject()
        dato.put("name",etSensor?.text.toString())
        dato.put("value",etValor?.text.toString())

        val peticion = object:JsonObjectRequest(
            Request.Method.POST, url, dato,
            {
                Toast.makeText(this, "Registro agregado", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(this, "Error en la peticion", Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer " + sesion.getString("token", "")
                return header
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun llenar() {
        val url = Uri.parse("http://172.18.7.251/sensores")
            .buildUpon()
            .build().toString()
        val peticion = object: JsonArrayRequest(Method.GET, url, null,
            {
                    res -> llenarRespuesta(res)
            },
            {
                Toast.makeText(this, "Error en la peticion", Toast.LENGTH_SHORT).show()
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer " + sesion.getString("token", "")
                return header
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }

    private fun llenarRespuesta(res: JSONArray) {
        val lista = Array(res.length()){ arrayOfNulls<String>(4)}
        for (i in 0 until res.length()){
            Log.d("ARRAY", res.getJSONObject(i).getString("id"))
            lista[i][0] = res.getJSONObject(i).getString("id")
            lista[i][1] = res.getJSONObject(i).getString("name")
            lista[i][2] = res.getJSONObject(i).getString("value")
            lista[i][3] = res.getJSONObject(i).getString("date")
        }
        rvList.adapter = MyAdapter(lista, object:MyListener{
            override fun onClickEdit(posicion: Int) {
                edit(lista[posicion][0], lista[posicion][1], lista[posicion][2])
                Toast.makeText(this@MainActivity2, "posicion:"+posicion+" id:"+lista[posicion][0],
                Toast.LENGTH_LONG).show()
            }

            override fun onClickDel(posicion: Int) {
                eliminar(lista[posicion][0]);
            }
        })
    }

    private fun edit(id: String?, sensor: String?, valor: String?){
        val extras = Bundle()
        extras.putString("id", id)
        extras.putString("sensor", sensor)
        extras.putString("valor", valor)
        val i = Intent(this, MainActivity3::class.java)
        i.putExtras(extras)
        startActivity(i)
    }

    private fun eliminar(id: String?) {
        var url = Uri.parse("http://172.18.7.251/sensores/"+id)
            .buildUpon()
            .build().toString()

        val peticion = object:JsonObjectRequest(
            Request.Method.DELETE, url, null,
            {
                Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show()
            },
            {
                Toast.makeText(this, "Error en la peticion", Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer " + sesion.getString("token", "")
                return header
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }
}

