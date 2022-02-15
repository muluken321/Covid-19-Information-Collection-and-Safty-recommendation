package com.muler.covid19.moreInfoFragment;

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

import com.muler.covid19.MainActivity;
import com.muler.covid19.R;
import com.muler.covid19.StringsList;
import com.muler.covid19.careFragment.CareFragment;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoreFragment extends Fragment {
    TextView textView, my_c_loc_txt,txtLocation3t,txtLocation5t;
    SharedPreferences sharedpreferences, sharedpreferencescidimei;
    static SharedPreferences sharedpreferencethis;
    ProgressBar progressBar8,progressBarwarn,progress14;
private  String Data_this = "Data_This";
    private MoreVModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(MoreVModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        textView   = root.findViewById(R.id.txtconfirmedet);
        txtLocation3t = root.findViewById(R.id.txtLocation3t);
        my_c_loc_txt  = root.findViewById(R.id.messagenews1address);
        txtLocation5t = root.findViewById(R.id.txtLocation5t);

        progressBar8 = root.findViewById(R.id.progressBar8);
        progressBarwarn = root.findViewById(R.id.progressBarwarn);
        progress14 = root.findViewById(R.id.progress14);

        sharedpreferences = getActivity().getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
        sharedpreferencethis = getActivity().getSharedPreferences(Data_this, Context.MODE_PRIVATE);
         sharedpreferencescidimei = getActivity().getSharedPreferences(CareFragment.MyPREFERENCESdb, Context.MODE_PRIVATE);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                String MY_loc_data = MainActivity.sharedpreferences.getString("MY_loc_data","");
                String Report_et = MoreFragment.sharedpreferencethis.getString("Report_et","");
                String Re_warn = MoreFragment.sharedpreferencethis.getString("Reportetwarn","");
                String last14 = MoreFragment.sharedpreferencethis.getString("last14","");
                String myImEi = sharedpreferencescidimei.getString("myid","");

                txtLocation3t.setText(Re_warn);
               // my_c_loc_txt.setText(""+MY_loc_data);
                my_c_loc_txt.setText(""+MY_loc_data);
                textView.setText(Report_et);
                txtLocation5t.setText(last14);

                getData(StringsList.getData);

                Warnings(StringsList.Warnings+myImEi);

                Sammaryforteen(StringsList.Sammaryforteen+myImEi);
            }
        });
        return root;
    }

    private void getData(final String urlWebService) {

        class GetDat extends AsyncTask<Void, Void, String> {

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
                    String totalcases = sys.getString("total_cases");
                    String death = sys.getString("death");
                    String recovered = sys.getString("recovered");
                    String intensive_care = sys.getString("intensive_care");
                    String total_in_quarantine = sys.getString("total_in_quarantine");
                 String Report_et=   "ጠቅላላ በቫይረሱ የተያዙ "+totalcases+"\n"+
                            "የሞቱ "+death+"\n"+
                            "ያገገሙ "+recovered+"\n"+
                            "ፅኑ ሀሙማን "+intensive_care +"\n"+
                            "አሁን በለይቶ ማቆያ ያሉ "+total_in_quarantine;

                    SharedPreferences.Editor editor = sharedpreferencethis.edit();
                    editor.putString("Report_et", Report_et);
                    editor.apply();
                    textView.setText(Report_et);
                    progressBar8.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progressBar8.setVisibility(View.GONE);
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
        GetDat getDat = new GetDat();
        getDat.execute();
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
                                    "ብዙ ጊዜ መንቀሳቀስ ያበዛሉ: ኬዝ ወደተመዘገበባቸው ቦታዎችም ሄደው ነበር! እባክዎትን እንቅስቃሴዎትን ይግቱ! " +
                                    "ካሁን በፊት የተንቀሳቀሱባቸውን ቦታዎች በማሰስ እባክዎትን ራስዎን ይገምግሙ\n";
                        }
                        else{
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "ብዙም  እንቅስቀሰሴ ባያበዙም ኬዝ ወደተመዘገበባቸው ቦታዎች ግን ሄደው ነበር! " +
                                    "እባክዎትን እንቅስቃሴዎትን ይግቱ! ካሁን በፊት የተንቀሳቀሱባቸውን ቦታዎች በማሰስ እባክዎትን ራስዎን ይገምግሙ\n";
                        }
                    }
                    else{
                        if(longjorney.equals("true")){
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "ብዙ ጊዜ  መንቀሳቀስ ያበዛሉ! ምንም እንኳን እስካሁን ኬዝ ወደተመዘገበባቸው ቦታዎች ባሂዱም እባክዎትን እንቅስቃሴዎትን ይግቱ\n";
                        }
                        else{
                            MessageofWarn = "ጤናዎን ለመጠበቅ በቤትዎ ይቆዩ\n" +
                                    "\n" +
                                    "ብዙም  እንቅስቀሰሴ ባያበዙም ፤ ኬዝ ወደተመዘገበባቸው ቦታዎችም  ሄደው ባያውቁም  እባክዎትን ሁጊዜ ጥንቃቄ አይለይዎት\n" +
                                    "\n";
                        }
                    }

                    SharedPreferences.Editor editor = sharedpreferencethis.edit();
                    editor.putString("Reportetwarn", MessageofWarn);
                    editor.apply();
                    txtLocation3t.setText(MessageofWarn);
                    progressBarwarn.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progressBarwarn.setVisibility(View.GONE);
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

    private void Sammaryforteen(final String urlWebService) {

        class GetJSummaryDetail extends AsyncTask<Void, Void, String> {

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
                    String locality = sys.getString("locality");
                    String streetname = sys.getString("streetname");
                    String warn = sys.getString("warn");
                    String  Last14 = "";
                    if(warn.equals("true")) {
                         Last14 = "ባለፉት 14 ቀናት የነበረዎት እንቅስቃሴ :" + streetname + "\t\t" + locality + "\n" +
                                "በዚህ አካባቢ ብዙ የሰዎች እንቅስቃሴ አለ";
                    }
                    else{
                        Last14 = "ባለፉት 14 ቀናት የነበረዎት እንቅስቃሴ :" + streetname + "\t\t" + locality + "\n" +
                                "በዚህ አካባቢ ብዙ የሰዎች እንቅስቃሴ የለም";
                    }
                    SharedPreferences.Editor editor = sharedpreferencethis.edit();
                    editor.putString("last14", Last14);
                    editor.apply();
                    txtLocation5t.setText(Last14);

                    progress14.setVisibility(View.GONE);
                }
                catch (Exception ex){
                    progress14.setVisibility(View.GONE);
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

        GetJSummaryDetail getJSummaryDetail = new GetJSummaryDetail();
        getJSummaryDetail.execute();
    }

}
