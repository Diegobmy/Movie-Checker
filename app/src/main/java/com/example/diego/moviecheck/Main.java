package com.example.diego.moviecheck;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import android.view.View.OnClickListener;
import org.json.JSONException;
import org.json.JSONObject;

public class Main extends AppCompatActivity {

    String API_URL = "https://api.themoviedb.org/3/search/movie?api_key=019e8de384166a7055b2be392945c7ba&query=";
    ProgressDialog progD;
    TextView title, description;
    EditText mquery;
    ImageView poster;
    JSONObject jsonResults;
    int jsonTotal;
    Button clickButton;
    String backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (TextView) findViewById(R.id.MTitle);
        description = (TextView) findViewById(R.id.MDesc);
        poster = (ImageView) findViewById(R.id.MPoster);
        mquery = (EditText) findViewById(R.id.MQuery);

        progD = new ProgressDialog(this);
        progD.setMessage("Loading movie details");
        progD.setCancelable(false);

        clickButton = (Button) findViewById(R.id.MSearch);
        clickButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //Se vazio
                if(mquery.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Please, enter a search term.", Toast.LENGTH_LONG).show();
                    return;
                }

                    progD.show();

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            API_URL+mquery.getText().toString(), null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                //Se nenhum resultado
                                jsonTotal = response.getInt("total_results");
                                if (jsonTotal < 1){
                                    progD.dismiss();
                                    Toast.makeText(getApplicationContext(), "No results.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                jsonResults = (JSONObject) response.getJSONArray("results").get(0);

                                //Apresentação
                                description.setText(jsonResults.getString("overview"));
                                title.setText(jsonResults.getString("title"));
                                backgroundImage = "https://image.tmdb.org/t/p/w500/" + jsonResults.getString("poster_path");

                                Glide
                                        .with(getApplicationContext())
                                        .load(backgroundImage)
                                        .centerCrop()
                                        .crossFade()
                                        .listener(new RequestListener<String, GlideDrawable>() {
                                            @Override
                                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                                System.out.println(e.toString());
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                                return false;
                                            }
                                        })
                                        .into(poster);

                                progD.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error , try again ! ", Toast.LENGTH_LONG).show();
                                progD.dismiss();
                            }

                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
                            progD.dismiss();
                        }
                    });

                    AppController.getInstance(Main.this).addToRequestQueue(jsonObjReq);
            }
        });

    }
}
