package kg.auca.example.converter;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

@SuppressWarnings("ALL")
public class CurrencyConverterActivity extends AppCompatActivity {

    private static final String FIXER_IO_API_KEY =
        "67e043a4d46e4fc44811a2d8f263ce92";
    private static final String CONVERSION_RATES_FILE_NAME =
        "custom_conversion_rates.json";

    private EditText firstValueEditText,
                     secondValueEditText,
                     currencyRateEditText;

    private Spinner firstValueSpinner,
                    secondValueSpinner;

    private RequestQueue networkRequestQueue;

    private JSONObject conversionRatios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        networkRequestQueue = Volley.newRequestQueue(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        firstValueEditText = findViewById(R.id.firstValueEditText);
        firstValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                convert();
            }
        });
        secondValueEditText = findViewById(R.id.secondValueEditText);
        currencyRateEditText = findViewById(R.id.currencyRateEditText);
        currencyRateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                updateConverstionRatio();
                convert();
            }
        });

        firstValueSpinner = findViewById(R.id.firstValueSpinner);
        secondValueSpinner = findViewById(R.id.secondValueSpinner);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadConversionRatio();
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        firstValueSpinner.setOnItemSelectedListener(onItemSelectedListener);
        secondValueSpinner.setOnItemSelectedListener(onItemSelectedListener);

        loadSpinnerValues();
        loadConversionData();
    }

    private void loadSpinnerValues() {
        String url =
            String.format(
                "http://data.fixer.io/api/latest?access_key=%s&base=USD&symbols=USD,EUR,RUB",
                FIXER_IO_API_KEY
            );

        final Context context = this;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        ArrayList<String> items = new ArrayList<>();

                        Iterator<String> keys = response.getJSONObject("rates").keys();
                        while (keys.hasNext()) {
                            items.add(keys.next());
                        }

                        ArrayAdapter adapter =
                            new ArrayAdapter(
                                context,
                                android.R.layout.simple_spinner_item,
                                items.toArray()
                            );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        firstValueSpinner.setAdapter(adapter);
                        secondValueSpinner.setAdapter(adapter);
                    } catch (JSONException e) {
                        reportError("Failed to load currency rates.");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    reportError("Failed to load currency rates.");
                }
            }
        );
        networkRequestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onStop() {
        saveConversionData();

        super.onStop();
    }

    public void updateCurrency(View view) {
        String from = firstValueSpinner.getSelectedItem().toString();
        final String to = secondValueSpinner.getSelectedItem().toString();
        if (from.equals(to)) {
            currencyRateEditText.setText(
                String.format(Locale.getDefault(), "%.2f", 1.0)
            );
        } else {
            String url =
                String.format(
                    "http://data.fixer.io/api/latest?access_key=%s&base=%s&symbols=USD,EUR,RUB",
                    FIXER_IO_API_KEY,
                    from
                );

            final Context context = this;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            currencyRateEditText.setText(
                                String.format(
                                    Locale.getDefault(),
                                    "%.2f",
                                    response.getJSONObject("rates").getDouble(to)
                                )
                            );
                        } catch (JSONException e) {
                            reportError("Failed to load currency rates.");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        reportError("Failed to load currency rates.");
                    }
                }
            );
            networkRequestQueue.add(jsonObjectRequest);
        }
    }

    private void loadConversionData() {
        File conversionRatiosFile;
        InputStream inputStream = null;

        if ((conversionRatiosFile = getFileStreamPath(CONVERSION_RATES_FILE_NAME)).exists()) {
            try {
                inputStream = new FileInputStream(conversionRatiosFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            inputStream = getResources().openRawResource(R.raw.initial_currency_rates);
        }

        if (inputStream != null) {
            String result = "";
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder(); String nextLine;
                while((nextLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(nextLine);
                }

                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                conversionRatios = new JSONObject(result);
            } catch (JSONException e) {
                reportError("Failed to load initial currency ratios.");
                e.printStackTrace();
            }
        }
    }

    private void saveConversionData() {
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(CONVERSION_RATES_FILE_NAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            reportError("Failed to save custom conversion ratios.");
            e.printStackTrace();
        }

        if (outputStream != null) {
            try {
                outputStream.write(conversionRatios.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadConversionRatio() {
        double currencyRatio = 1.0;

        String from = firstValueSpinner.getSelectedItem().toString();
        String to = secondValueSpinner.getSelectedItem().toString();
        if (!from.equals(to)) {
            String key = from + "-" + to;
            try {
                currencyRatio = conversionRatios.getDouble(key);
            } catch (JSONException e) {
                reportError("Invalid conversion ratio");
                return;
            }
        }

        currencyRateEditText.setText(
            String.format(Locale.getDefault(), "%.2f", currencyRatio)
        );
    }

    private void updateConverstionRatio() {
        String ratioText = currencyRateEditText.getText().toString();
        if (ratioText.trim().isEmpty()) {
            return;
        }
        ratioText = ratioText.replace(',', '.');

        float ratio;
        try {
            ratio = Float.parseFloat(ratioText);
        } catch (NumberFormatException e) {
            reportError("Invalid ratio");
            return;
        }

        String from = firstValueSpinner.getSelectedItem().toString();
        String to = secondValueSpinner.getSelectedItem().toString();
        if (!from.equals(to)) {
            String key = from + "-" + to;
            try {
                conversionRatios.put(key, ratio);
            } catch (JSONException e) {
                reportError("Failed to update the currency ratio");
            }
        }
    }

    private void convert() {
        String firstValueText = firstValueEditText.getText().toString();
        if (firstValueText.trim().isEmpty()) {
            return;
        }
        firstValueText = firstValueText.replace(',', '.');

        float fromValue;
        try {
            fromValue = Float.parseFloat(firstValueText);
        } catch (NumberFormatException e) {
            reportError("Invalid first value");
            return;
        }

        double currencyRatio = 1.0;

        String from = firstValueSpinner.getSelectedItem().toString();
        String to = secondValueSpinner.getSelectedItem().toString();
        if (!from.equals(to)) {
            String key = from + "-" + to;
            try {
                currencyRatio = conversionRatios.getDouble(key);
            } catch (JSONException e) {
                reportError("Invalid conversion ratio");
                return;
            }
        }

        double result = fromValue * currencyRatio;
        secondValueEditText.setText(
            String.format(Locale.getDefault(), "%.2f", result)
        );
    }

    private void reportError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
