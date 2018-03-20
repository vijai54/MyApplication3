package com.example.vijaya2.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    Button cameraButton = null;
    ImageView imageView = null;

    Button uploadButton = null;

    TextView messageText = null;

    ProgressDialog progressDialog = null;


    String upLoadServerUri = "http://107.109.107.100:8080/uploads/";

    String uploadFileName = "test";

    Bitmap imageBitmap = null;


    static final int REQUEST_IMAGE_CAPTURE = 2;

    int serverResponseCode = 0;

    static final int REQUEST_TAKE_PHOTO = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = (Button) findViewById(R.id.button_camera);
        imageView = (ImageView) findViewById(R.id.imageView);
        cameraButton.setOnClickListener(this);


        uploadButton = (Button)findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(this);
        messageText  = (TextView)findViewById(R.id.progress);

        messageText.setText("Uploading file path :- '/mnt/sdcard/"+uploadFileName+"'");

      /*  *//************* Php script path ****************//*
        upLoadServerUri = "<a class="vglnk" href="http://www.androidexample.com/media/UploadToServer.php" rel="nofollow"><span>http</span><span>://</span><span>www</span><span>.</span><span>androidexample</span><span>.</span><span>com</span><span>/</span><span>media</span><span>/</span><span>UploadToServer</span><span>.</span><span>php</span></a>";
*/

    }

    @Override
    public void onClick(View view) {
        if (view == cameraButton) {
           // takePhoto(view);
            dispatchTakePictureIntent();
        }

        if(view == uploadButton) {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Uploading, please wait...");
            progressDialog.show();

            //converting image to base64 string
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            //sending image to server
            StringRequest request = new StringRequest(Request.Method.POST, upLoadServerUri, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    progressDialog.dismiss();
                    if(s.equals("true")){
                        Toast.makeText(MainActivity.this, "Uploaded Successful", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Some error occurred!", Toast.LENGTH_LONG).show();
                    }
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this, "Some error occurred -> "+volleyError, Toast.LENGTH_LONG).show();;
                }
            }) {
                //adding parameters to send
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("image", imageString);
                    return parameters;
                }
            };

            RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
            rQueue.add(request);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {



            case REQUEST_IMAGE_CAPTURE:
                    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);
                    }
                    break;
        }

    }
}