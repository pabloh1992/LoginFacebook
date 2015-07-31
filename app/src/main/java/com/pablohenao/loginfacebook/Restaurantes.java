package com.pablohenao.loginfacebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Restaurantes extends ActionBarActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    TextView txvtest;
    TextView txvtest2;
    private String foto;
    private static int TAKE_PICTURE = 1;
    int aleatorio = 0;
    Bitmap bitmap;
    String encodedString;
    String name;
    String rate = "0";
    private GoogleApiClient mGoogleApiClient;
    private Location currentLocation;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    String slatitud;
    String slongitud;
    String restaurante;
    String descrip;

    private EditText edname, eddesc;

 //   private EditText user, pass;
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    private static final String REGISTER_URL = "http://parchapp.esy.es/cas/guardar.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantes);


        Button btncamara = (Button) findViewById(R.id.btncam);  //REGISTER
        btncamara.setOnClickListener(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        EditText edname = (EditText) findViewById(R.id.edname);  // user
        EditText eddesc = (EditText) findViewById(R.id.eddesc);

        final Spinner listah = (Spinner) findViewById(R.id.spinrate);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.num_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listah.setAdapter(adapter);
        listah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String hotel = String.valueOf(listah.getSelectedItem());

                if(position == 0){
                    rate = "1";
                }
                if(position==1){
                    rate = "2";
                }
                if(position==2){
                    rate = "3";
                }
                if(position==3){
                    rate = "4";
                }
                if(position==4){
                    rate = "5";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buildGoogleApiClient();
        createLocationRequest();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurantes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidM
        // anifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.btncam){
            EditText edname = (EditText) findViewById(R.id.edname);  // user
            EditText eddesc = (EditText) findViewById(R.id.eddesc);  // pass

            restaurante = edname.getText().toString();
            descrip = eddesc.getText().toString();

            if(restaurante.isEmpty()){
                edname.setError("Vacío");
            }
            else if (descrip.isEmpty()){
                eddesc.setError("Vacío");
            }
            else {

                currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                double latitud = currentLocation.getLatitude();
                double longitud = currentLocation.getLongitude();
                slatitud = String.valueOf(latitud);
                slongitud = String.valueOf(longitud);
                aleatorio = new Double(Math.random() * 100).intValue();
                foto = Environment.getExternalStorageDirectory() + "/imagen" + aleatorio + ".png";
                name = "/imagen" + aleatorio + ".png";
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri output = Uri.fromFile(new File(foto));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
                startActivityForResult(intent, TAKE_PICTURE); // 1 para la camara, 2 para la galeria
            }
        }
    }

    class CreateUser extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Restaurantes.this);
            pDialog.setMessage("Agregando un nuevo restaurante...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
      //      EditText edname = (EditText) findViewById(R.id.edname);  // user
      //      EditText eddesc = (EditText) findViewById(R.id.eddesc);  // pass
       //     String username = edname.getText().toString();
       //     String password = eddesc.getText().toString();
            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("nombre", restaurante));
                params.add(new BasicNameValuePair("descripcion", descrip));
                params.add(new BasicNameValuePair("imagen", name));
                params.add(new BasicNameValuePair("latitud", slatitud));
                params.add(new BasicNameValuePair("longitud", slongitud));
                params.add(new BasicNameValuePair("calificacion", rate));
          //      params.add(new BasicNameValuePair("bytes_imagen", encodedString));
                params.add(new BasicNameValuePair("bytes_imagen", "bytes malvados"));


                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Restaurantes.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }



    public static String URL = "http://parchapp.esy.es/prueba.php";
    private void enviarnum(String numero) {
        Map<String, String> params3 = new HashMap<String, String>();
        params3.put("numero", numero);
        params3.put("nombre", "Alekoz");
        final JSONObject Jason=new JSONObject(params3);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,URL,Jason,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resp1 = response.getString("resp1");
                            String resp2 = response.getString("resp2");
                            Toast.makeText(getApplicationContext(), "Imagen subida", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {       @Override
                public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Imagen subida", Toast.LENGTH_SHORT).show();
            }
        }
        );

        MySingleton.getInstance(this).addToRequestQueue(getRequest);

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView iv = (ImageView) findViewById(R.id.imageView1);
        iv.setImageBitmap(BitmapFactory.decodeFile(foto));

        File file = new File(foto);
        if (file.exists()) {
            UploaderFoto nuevaTarea = new UploaderFoto();
            nuevaTarea.execute(foto);
        }
        else
            Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation!=null){
            currentLocation = mLastLocation;
        }
        else{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    class UploaderFoto extends AsyncTask  <String, Void, Void>{

        ProgressDialog pDialog;
        String miFoto = "";


        @Override
        protected Void doInBackground(String... params) {
            miFoto = params[0];
            try {
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                bitmap = BitmapFactory.decodeFile(foto);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, 0);

                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Restaurantes.this);
            pDialog.setMessage("Subiendo la imagen, espere." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Map<String, String> paramsfoto = new HashMap<String, String>();
            paramsfoto.put("image", encodedString);
            paramsfoto.put("filename",name);
            paramsfoto.put("latitud",slatitud);
            paramsfoto.put("longitud",slongitud);
            paramsfoto.put("nombrest",restaurante);
            paramsfoto.put("descrip",descrip);
            paramsfoto.put("nota",rate);
            paramsfoto.put("personas","1");
            // Trigger Image upload
            triggerImageUpload(paramsfoto);
      //      new CreateUser().execute();
            pDialog.dismiss();
        }
    }

    public void triggerImageUpload(Map<String, String> paramsfoto) {
        makeHTTPCall(paramsfoto);
    }
    public static String URLf = "http://parchapp.esy.es/intento.php";
    public void makeHTTPCall(Map<String, String> paramsfoto) {
        final JSONObject Jason=new JSONObject(paramsfoto);

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,URLf,Jason,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resp1 = response.getString("resp1");
                            String resp2 = response.getString("resp2");
                            Toast.makeText(getApplicationContext(), "Imagen subida", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()
        {       @Override
                public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Imagen subida", Toast.LENGTH_SHORT).show();
            }
        }
        );

        MySingleton.getInstance(this).addToRequestQueue(getRequest);

    }
    /**
     * A placeholder fragment containing a simple view.
     */
}