package mx.tecnm.cdhidalgo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class MainActivity3 : AppCompatActivity() {
    lateinit var etSensorEdit: EditText
    lateinit var etValueEdit: EditText
    lateinit var bSaveEdit: Button
    lateinit var bCancelEdit: Button
    lateinit var sesion: SharedPreferences
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        etSensorEdit = findViewById(R.id.etSensorEdit)
        etValueEdit = findViewById(R.id.etValueEdit)
        bSaveEdit = findViewById(R.id.bSaveEdit)
        bCancelEdit = findViewById(R.id.bCancelEdit)
        sesion = getSharedPreferences("sesion",0)
        val datos = this.intent.extras
        etSensorEdit.setText(datos?.getString("sensor"))
        etValueEdit.setText(datos?.getString("valor"))
        bCancelEdit.setOnClickListener{ startActivity(Intent(this, MainActivity2::class.java))}
        bSaveEdit.setOnClickListener { guardar(datos?.getString("id"))}
    }
    private fun guardar(id: String?){
        var url = Uri.parse("http://172.18.7.251/sensores/"+id)
            .buildUpon()
            .build().toString()

        val datos: JSONObject = JSONObject()
        datos.put("name",etSensorEdit.text.toString())
        datos.put("value",etValueEdit.text.toString())

        val peticion = object: JsonObjectRequest(
            Request.Method.PUT, url, datos,
            {
                Toast.makeText(this, "Registro modificado", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity2::class.java))
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
