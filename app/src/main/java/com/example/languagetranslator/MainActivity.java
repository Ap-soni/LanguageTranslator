package com.example.languagetranslator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner fromspin,tospin;
    private Button btn;
    private TextView translatedtext;
    private ImageView micIV;
    private EditText sourceedt;
    private static final int REQUEST_PERMISSION_CODE = 1;
    String languageCode, fromLanguageCode, toLanguageCode;


    String[] fromlanguages={"from","English","Afrikaans","Arabic","Belarusian","Bengali","Catalan","Czech", "Welsh", "Hindi", "Urdu"};
    String[] toLanguages = {"from","English","Afrikaans","Arabic","Belarusian","Bengali","Catalan","Czech", "Welsh", "Hindi", "Urdu"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromspin=findViewById(R.id.idFromSpinner);
        tospin=findViewById(R.id.idToSpinner);
        sourceedt=findViewById(R.id.idEdtSource);
        btn=findViewById(R.id.idBtnTranslate);
        micIV = findViewById(R.id.idIVMic);
        translatedtext=findViewById(R.id.idTvTranslatedTV);
        fromspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromLanguageCode=getLanguageCode(fromlanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tospin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toLanguageCode=getLanguageCode(toLanguages[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromadapter=new ArrayAdapter(this,R.layout.spinner_item,fromlanguages);
        fromadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromspin.setAdapter(fromadapter);
        ArrayAdapter toadapter=new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tospin.setAdapter(toadapter);

        micIV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to convert into text");
                try{
                    startActivityForResult(i, REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }


            }


        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedtext.setText("");
                if(sourceedt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter your text", Toast.LENGTH_SHORT).show();
                } else if (fromLanguageCode.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Select Source Language", Toast.LENGTH_SHORT).show();
                } else if (toLanguageCode.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Select Target Language", Toast.LENGTH_SHORT).show();
                }else {
                    TranslateText(fromLanguageCode,toLanguageCode,sourceedt.getText().toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                if (resultCode==RESULT_OK&&data!=null){
                    ArrayList<String> arrayList=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    sourceedt.setText(arrayList.get(0));

                }break;
        }
    }

    private void TranslateText(String fromLanguageCode, String toLanguageCode, String src) {
        translatedtext.setText("Downloading Language Model");

           TranslatorOptions options=new TranslatorOptions.Builder().setSourceLanguage(fromLanguageCode).setTargetLanguage(toLanguageCode).build();
           Translator translator= Translation.getClient(options);
           DownloadConditions conditions=new DownloadConditions.Builder().build();
           translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void unused) {
                   translatedtext.setText("Translating...");
                   translator.translate(src).addOnSuccessListener(new OnSuccessListener<String>() {
                       @Override
                       public void onSuccess(String s) {
                           translatedtext.setText(s);
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(MainActivity.this, "Fail to translate", Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(MainActivity.this, "Fail to download the language", Toast.LENGTH_SHORT).show();
               }
           });


    }


    private String getLanguageCode(String languages) {
        String languageCode;
        switch(languages){
            case"English":
                languageCode= TranslateLanguage.ENGLISH;
                break;
            case"Afrikaans":
                languageCode= TranslateLanguage.AFRIKAANS;
                break;
            case"Arabic":
                languageCode= TranslateLanguage.ARABIC;
                break;
            case"Belarusian":
                languageCode= TranslateLanguage.BELARUSIAN;
                break;
            case"Bengali":
                languageCode= TranslateLanguage.BENGALI;
                break;
            case"Catalan":
                languageCode= TranslateLanguage.CATALAN;
                break;
            case"Czech":
                languageCode= TranslateLanguage.CZECH;
                break;
            case"Welsh":
                languageCode= TranslateLanguage.WELSH;
                break;
            case"Hindi":
                languageCode= TranslateLanguage.HINDI;
                break;
            case"Urdu":
                languageCode= TranslateLanguage.URDU;
                break;
            default:
                languageCode="";
        }
return languageCode;
    }
}