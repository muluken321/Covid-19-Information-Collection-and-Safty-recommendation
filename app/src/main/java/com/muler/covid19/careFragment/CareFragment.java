package com.muler.covid19.careFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.muler.covid19.R;
import com.muler.covid19.StringsList;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CareFragment extends Fragment {
   private TextView textView, txthistorydet,txtdate1,txtdate2,txtLocation3ta;
    private CareVModel homeViewModel;
    public static SharedPreferences sharedpreferences;
    ProgressBar progressBar1,progressBar3,progressBar3state;
    private LayoutInflater inflater;
    private View layout;

    public static final String MyPREFERENCESdb = "MY_L_DB" ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(CareVModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
        textView   = root.findViewById(R.id.txtmyinfo);
        txthistorydet   = root.findViewById(R.id.txthistorydet);
        txtdate1   = root.findViewById(R.id.txtdate1);
       // txttravel2detail   = root.findViewById(R.id.txttravel2detail);
      //  txtdate2   = root.findViewById(R.id.txtdate2);
        txtLocation3ta = root.findViewById(R.id.txtLocation3ta);

        progressBar1 = root.findViewById(R.id.progressBar1);
        progressBar3 = root.findViewById(R.id.progressBar3);
       // progressBar4 = root.findViewById(R.id.progressBar4);
        progressBar3state = root.findViewById(R.id.progressBar3state);


         inflater = getLayoutInflater();
         layout = inflater.inflate(R.layout.network_error,
                (ViewGroup) root.findViewById(R.id.toast_layout_root));


        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCESdb, Context.MODE_PRIVATE);


        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                String My_prof = sharedpreferences.getString("profile","");
                String imei = sharedpreferences.getString("myid","");
                String My_firstT = sharedpreferences.getString("My_sT","");
                String My_SecondT = sharedpreferences.getString("My_SecondT","");
                textView.setText(My_prof);
                txthistorydet.setText(My_firstT);
              //  txttravel2detail.setText(My_SecondT);
                String mystatus = sharedpreferences.getString("yourstatus","");
                txtLocation3ta.setText(mystatus);
                getPinfo(StringsList.getPinfo+imei);
                getTravelHistory(StringsList.getTravelHistory+imei);
              //  getTravelHistory2(StringsList.getTravelHistory2);
              Warnings(StringsList.Warnings+imei);

            }
        });
        return root;
    }

    private void Warnings(final String urlWebService) {

        class GetWarningsDetail extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject reader = new JSONObject(s);
                    JSONObject sys  = reader.getJSONObject("datas");
                    String casewarnings = sys.getString("result");
                    String longjorney = sys.getString("longjorney");
                    String MessageofWarn = "";
                    if(casewarnings.equals("true")){
                        if(longjorney.equals("true")){
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "እስካሁን ያለዎት እንቅስቃሴ እንደሚያሳየው  ለቫይረሱ ያለዎት ተጋላጭነት ከፍ ያለ  ነዉ ፡፡ እባክዎትን ሁልጊዜ ግን ጥንቃቄ ያድርጉ";
                        }
                        else{
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "እስካሁን ያለዎት እንቅስቃሴ እንደሚያሳየው  ለቫይረሱ ያለዎት ተጋላጭነት \n" +
                                    "‹‹መካከለኛ››፡፡ \n"+"እባክዎትን ሁልጊዜ ግን ጥንቃቄ ያድርጉ\n";
                        }
                    }
                    else{
                        if(longjorney.equals("true")){
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "እስካሁን ያለዎት እንቅስቃሴ እንደሚያሳየው  ለቫይረሱ ያለዎት ተጋላጭነት \n" +
                                    "‹‹መካከለኛ››፡፡ \n"+"እባክዎትን ሁልጊዜ ግን ጥንቃቄ ያድርጉ\n";
                        }
                        else{
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "እስካሁን ባለዎት እንቅስቃሴ መሰረት ለቫይረሱ ያለዎት ተጋላጭነት ዝቅተኛ ነዉ ፡፡ ሁልጊዜ ግን ጥንቃቄ ያድርጉ" +
                                    "\n";
                        }
                    }

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("yourstatus", MessageofWarn);
                    editor.apply();
                    txtLocation3ta.setText(MessageofWarn);
                    progressBar3state.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progressBar3state.setVisibility(View.GONE);
                    //  textView.setText("Error in your internet Connection");
                }
            }
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }
        GetWarningsDetail getWarningsDetail = new GetWarningsDetail();
        getWarningsDetail.execute();
    }

    private void getPinfo(final String urlWebService) {

        class GetPinfo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject reader = new JSONObject(s);
                    JSONObject sys  = reader.getJSONObject("user_data");
                    String full_name = sys.getString("full_name");
                    String Nik_name = sys.getString("nikname");
                    String address = sys.getString("address");
                    String History_before = sys.getString("historybefor");
                  String profile =   "ሙሉ ስም: "+full_name+"\n"+
                            "ቅፅል ስም: "+Nik_name+"\n"+
                            "አድራሻ: "+address+"\n"+
                            "የበፊት የጉዞ ታሪክ: "+History_before;
                   // SharedPreferences sharedpreferences = getActivity().getSharedPreferences("mylocationDB", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("profile", profile);

                    editor.commit();
                    textView.setText(profile);

                        progressBar1.setVisibility(View.GONE);


                }
                catch (Exception ex){
                 //   Toast.makeText(getActivity().getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG).show();
                    progressBar1.setVisibility(View.GONE);

                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetPinfo getPinfo = new GetPinfo();
        getPinfo.execute();
    }
    /*********TRAVEL HISTORY FUNCTION
     *
     */
    private void getTravelHistory(final String urlWebService) {

        class GetJson extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject reader = new JSONObject(s);
                    JSONObject user_travel  = reader.getJSONObject("user_travel");
                    String date = user_travel.getString("date");
                    String country = user_travel.getString("country");
                    String address_line = user_travel.getString("address_line");
                    String admin_area = user_travel.getString("admin_area");
                    String postal_code = user_travel.getString("postal_code");
                    String sub_admin_area = user_travel.getString("sub_admin_area");
                    String locality = user_travel.getString("locality");
                    String sub_locality = user_travel.getString("sub_locality");
                  String My_sT =  "በዚህ ቀን የተጓዙበት ቦታ :"+"\n"+
                            "Country: "+country+"\n"+
                            "Street Name: "+address_line+"\n"+
                            "Admin Area: "+admin_area+"\n"+
                            "Postal Code:"+postal_code+"\n"+
                            "Sub Admin Area:"+sub_admin_area+"\n"+
                            "Locality:"+locality+"\n"+
                            "Sub Locality:"+sub_locality;

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("My_sT", My_sT);
                    editor.commit();


                    txthistorydet.setText(My_sT);

                    txtdate1.setText("Date: "+date);
                    progressBar3.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progressBar3.setVisibility(View.GONE);
                   // txthistorydet.setText("Connection Error!");
                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJson getTHistory = new GetJson();
        getTHistory.execute();
    }

   /* private void getTravelHistory2(final String urlWebService) {

        class GetThistory extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject reader = new JSONObject(s);
                    JSONObject user_travel  = reader.getJSONObject("user_travel");
                    String date = user_travel.getString("date");
                    String country = user_travel.getString("country");
                    String address_line = user_travel.getString("address_line");
                    String admin_area = user_travel.getString("admin_area");
                    String postal_code = user_travel.getString("postal_code");
                    String sub_admin_area = user_travel.getString("sub_admin_area");
                    String locality = user_travel.getString("locality");
                    String sub_locality = user_travel.getString("sub_locality");
                  String My_SecondT = "በዚህ ቀን የተጓዙበት ቦታ :"+"\n"+
                            "Country: "+country+"\n"+
                            "Street Name: "+address_line+"\n"+
                            "Admin Area: "+admin_area+"\n"+
                            "Postal Code:"+postal_code+"\n"+
                            "Sub Admin Area:"+sub_admin_area+"\n"+
                            "Locality:"+locality+"\n"+
                            "Sub Locality:"+sub_locality;

                  //  SharedPreferences sharedpreferences = getActivity().getSharedPreferences("mylocationDB", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("My_SecondT", My_SecondT);
                    editor.commit();
                    txttravel2detail.setText(My_SecondT);
                    txtdate2.setText("Date: "+date);
                    progressBar4.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progressBar4.setVisibility(View.GONE);
                    //txthistorydet.setText("Connection Error!");
                }
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {



                try {
                    //creating a URL
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetThistory getThistory = new GetThistory();
        getThistory.execute();
    }

*/
}