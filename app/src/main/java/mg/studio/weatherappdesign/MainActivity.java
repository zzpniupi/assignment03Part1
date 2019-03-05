package mg.studio.weatherappdesign;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity {

    public String api="https://free-api.heweather.com/s6/weather/now?key=ca42a16767dc404eabebfe8eef168b14&location=chongqing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager connManager = (ConnectivityManager) this
                .getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo==null)
            Toast.makeText(this, "The current network connection is unavailable", Toast.LENGTH_SHORT).show();
        else
            new DownloadUpdate().execute();
    }

    public void btnClick(View view) {
        ConnectivityManager connManager = (ConnectivityManager) this
                .getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo==null)
            Toast.makeText(this, "The current network connection is unavailable", Toast.LENGTH_SHORT).show();
        else{
            new DownloadUpdate().execute();
            Toast.makeText(MainActivity.this, "Weather data has been updated！", Toast.LENGTH_SHORT).show();
        }
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://free-api.heweather.net/s6/weather/forecast?key=ca42a16767dc404eabebfe8eef168b14&location=chongqing";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    //检测网络异常
                } else {
                    return "11";
                }

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("weatherinfo", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                //返回天气json数据
                Log.d("jsoninfo",buffer.toString());
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("11")) {
                Toast.makeText(MainActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            }
            //Update the temperature displayed
            //((TextView) findViewById(R.id.temperature_of_the_day)).setText(s);
            String wendu="0";   //当前温度
            String addr="0";   //当前地址
            String date="0";   //当前日期
            String cond_now="0";    //当前天气状态
            String week="0";        //周几
            String cond_01="0";    //01天气状态
            String cond_02="0";    //02天气状态
            String cond_03="0";    //03天气状态
            String cond_04="0";    //04天气状态
            String date_01="0";    //01日期
            String date_02="0";    //02日期
            String date_03="0";    //03日期
            String date_04="0";    //04日期
            String week_01="0";    //01周几
            String week_02="0";    //02周几
            String week_03="0";    //03周几
            String week_04="0";    //04周几

            //解析json数据
            try{
                JSONObject object=new JSONObject(s);
                JSONObject object1 = object.getJSONArray("HeWeather6").getJSONObject(0);
                JSONObject basic=object1.getJSONObject("basic");
                JSONObject now = object1.getJSONArray("daily_forecast").getJSONObject(0);
                JSONObject day01 = object1.getJSONArray("daily_forecast").getJSONObject(1);
                JSONObject day02 = object1.getJSONArray("daily_forecast").getJSONObject(2);
                JSONObject day03 = object1.getJSONArray("daily_forecast").getJSONObject(3);
                JSONObject day04 = object1.getJSONArray("daily_forecast").getJSONObject(4);


                wendu = now.getString("tmp_max");
                addr=basic.getString("location");
                date=now.getString("date");
                cond_now=now.getString("cond_txt_d");
                week=dateToWeek(date);
                cond_01=day01.getString("cond_txt_d");
                cond_02=day02.getString("cond_txt_d");
                cond_03=day03.getString("cond_txt_d");
                cond_04=day04.getString("cond_txt_d");
                date_01=day01.getString("date");
                date_02=day02.getString("date");
                date_03=day03.getString("date");
                date_04=day04.getString("date");
                week_01=dateToWeek(date_01);
                week_02=dateToWeek(date_02);
                week_03=dateToWeek(date_03);
                week_04=dateToWeek(date_04);

            }catch(JSONException e){
                e.printStackTrace();
            }
            Log.d("temp", cond_now);
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(wendu);      //更新当前温度
            ((TextView) findViewById(R.id.tv_location)).setText(addr);                 //更新地址
            ((TextView) findViewById(R.id.tv_date)).setText(date);                     //更新时间
            ((TextView) findViewById(R.id.week)).setText(week);                     //更新周几
            ((TextView) findViewById(R.id.week01)).setText(week_01);                     //更新周几
            ((TextView) findViewById(R.id.week02)).setText(week_02);                     //更新周几
            ((TextView) findViewById(R.id.week03)).setText(week_03);                     //更新周几
            ((TextView) findViewById(R.id.week04)).setText(week_04);                     //更新周几

            if(cond_now.matches("晴"))
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));     //更新天气图标
            else if(cond_now.matches("多云"))
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));     //更新天气图标
            else if(cond_now.matches("阴"))
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if(cond_now.matches("小雨"))
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
            else if(cond_now.matches("大雨"))
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else
                ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));

            if(cond_01.matches("晴"))
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));     //更新天气图标
            else if(cond_01.matches("多云"))
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));     //更新天气图标
            else if(cond_01.matches("阴"))
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if(cond_01.matches("小雨"))
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
            else if(cond_01.matches("大雨"))
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else
                ((ImageView)findViewById(R.id.img_weather_condition01)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));

            if(cond_02.matches("晴"))
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));     //更新天气图标
            else if(cond_02.matches("多云"))
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));     //更新天气图标
            else if(cond_02.matches("阴"))
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if(cond_02.matches("小雨"))
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
            else if(cond_02.matches("大雨"))
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else
                ((ImageView)findViewById(R.id.img_weather_condition02)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));

            if(cond_03.matches("晴"))
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));     //更新天气图标
            else if(cond_03.matches("多云"))
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));     //更新天气图标
            else if(cond_03.matches("阴"))
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if(cond_03.matches("小雨"))
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
            else if(cond_03.matches("大雨"))
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else
                ((ImageView)findViewById(R.id.img_weather_condition03)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));

            if(cond_04.matches("晴"))
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));     //更新天气图标
            else if(cond_04.matches("多云"))
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.partly_sunny_small));     //更新天气图标
            else if(cond_04.matches("阴"))
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.windy_small));
            else if(cond_04.matches("小雨"))
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_small));
            else if(cond_04.matches("大雨"))
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.rainy_up));
            else
                ((ImageView)findViewById(R.id.img_weather_condition04)).setImageDrawable(getResources().getDrawable(R.drawable.sunny_small));
        }
    }

    public String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = { "Sun", "Mon", "Tue", "Wed", "Tur", "Fri", "Sat" };
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}
