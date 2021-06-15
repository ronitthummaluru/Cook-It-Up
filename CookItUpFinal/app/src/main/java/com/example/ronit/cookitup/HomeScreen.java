package com.example.ronit.cookitup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity {

    Button button;
    ImageView recImg;
    EditText editText;

    ProgressBar progressBar;
    TextView responseView;
    Button launchButton;
    TextView number;
    String ingredients = "";
    JSONObject food;
    int counter = 1;
    int recipeCount;
    boolean changeIng;
    String sameIng = "";
    String title = "";
    String imgUrl = "";

    ArrayList<JSONObject> jsonArr=new ArrayList<>();


    static final int NUMBER_CODE = 11234;
    static final String TAG_NUMBER = "Number TAG";
    int i;
    char comma = ',';
    char space = ' ';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.id_button);
        editText = findViewById(R.id.id_editText);

        progressBar = findViewById(R.id.id_progressBar);
        responseView = findViewById(R.id.id_textView);
        launchButton = findViewById(R.id.id_launch);
        number = findViewById(R.id.id_number);
        recImg = findViewById(R.id.id_recipeImage);

        /**  API_URL + "ingredients=" + ingredients + "&apiKey=" + API_KEY);
         */

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeIng = false;

               /* if(sameIng == editText.getText().toString()) {
                    changeIng = false;
                }*/
               /* if(sameIng != editText.getText().toString()){
                    changeIng = true;
                    counter = 1;
                }*/
                ingredients = editText.getText().toString();
                sameIng = ingredients;


                AsyncThread thread = new AsyncThread();
                try {
                    thread.execute();
                    responseView.setText(title);
                    // recImg.setImageDrawable(recipeImage(imgUrl));

                } catch (Exception e) {

                }

            }
        });

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToLoadActivity = new Intent(HomeScreen.this, MainActivity.class);
                intentToLoadActivity.putExtra("TEST", "This is a test!");
                //startActivity(intentToLoadActivity);
                startActivityForResult(intentToLoadActivity, NUMBER_CODE);
            }
        });

    }

    public String readAllLines(BufferedReader buffIn) throws IOException {
        StringBuilder everything = new StringBuilder();
        String line;
        while ((line = buffIn.readLine()) != null) {
            everything.append(line);
        }
        return everything.toString();
    }


    public class AsyncThread extends AsyncTask<String, Void, Void> {

        private Exception exception;


        protected Void doInBackground(String... response) {
            JSONArray array=new JSONArray();
            JSONObject recipe = new JSONObject();

            jsonArr=new ArrayList<>();
            try {

                URL url = new URL("https://www.food2fork.com/api/search?key=41d107b1102a56306993522fc134aaf7&q=" + ingredients);
                Log.e("HEYO", url + "");
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String jsonString = readAllLines(bufferedReader);
                Log.e("HEYO", "Food Results" + jsonString);

                food = new JSONObject(jsonString);
                array = food.getJSONArray("recipes");
                recipeCount = food.getInt("count");
                Log.e("HEYO","Counter = "+recipeCount+"");
                for(int x = 0; x < counter ; x++){
                    Log.e("COUNT","x for recipes" + x);
                    Log.e("COUNT", "counter for recipes"+ counter);
                    Log.e("COUNT", "changeIng "+changeIng);
                    recipe = array.getJSONObject(x);
                    Log.e("COOK",recipe.toString());
                    title = recipe.getString("title");
                    //    imgUrl = recipe.getString("image_url");
                    Log.e("TITLE",title);
                    jsonArr.add(recipe);
                    if(changeIng == false) {
                        counter++;
                        changeIng = true;
                    }

                }


            } catch (Exception e) {
                Log.d("JSON", "exception while fetching movie data: " + e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }


    public void displayResults() {
        while (food == null) {
            Log.d("TAG","NO DATA");
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String title = food.getString("recipes");

            responseView.setText(title);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*  public String findRecipe(int c){
          try {

                  if (c == 0)
                      title = jsonArr.get(c).getJSONArray("recipes").getJSONObject(0).getString("title");
                  Log.d("REC", "Title of movie: " + title);

          }catch (JSONException e) {
              e.printStackTrace();
          }
              return title;
          }*/
    public static Drawable recipeImage(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NUMBER_CODE && resultCode == RESULT_OK) {
            number.setText(data.getStringExtra(TAG_NUMBER));

        }
    }

}
