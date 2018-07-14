package kg.auca.example.converter;

import android.content.Intent;
import android.content.res.TypedArray;
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

import java.util.Locale;

public class UnitConverterActivity extends AppCompatActivity {

    private EditText firstValueEditText,
                     secondValueEditText;

    private Spinner firstValueSpinner,
                    secondValueSpinner;

    private TypedArray conversionUnitValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_converter);

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

        firstValueSpinner = findViewById(R.id.firstValueSpinner);
        secondValueSpinner = findViewById(R.id.secondValueSpinner);

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                convert();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        firstValueSpinner.setOnItemSelectedListener(onItemSelectedListener);
        secondValueSpinner.setOnItemSelectedListener(onItemSelectedListener);

        Intent intent = getIntent();
        int unit_names = intent.getIntExtra("UNIT_CONVERSION_NAMES", -1);
        int unit_values = intent.getIntExtra("UNIT_CONVERSION_VALUES", -1);

        ArrayAdapter<CharSequence> adapter =
            ArrayAdapter.createFromResource(
                this,
                unit_names,
                android.R.layout.simple_spinner_item
            );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstValueSpinner.setAdapter(adapter);
        secondValueSpinner.setAdapter(adapter);

        conversionUnitValues = getResources().obtainTypedArray(unit_values);
    }

    private void convert() {
        String firstValueText = firstValueEditText.getText().toString();
        if (firstValueText.trim().isEmpty()) {
            return;
        }

        float fromValue;
        try {
            fromValue = Float.parseFloat(firstValueText);
        } catch (NumberFormatException e) {
            reportError("Invalid first value");
            return;
        }

        int fromIndex = firstValueSpinner.getSelectedItemPosition();
        int toIndex = secondValueSpinner.getSelectedItemPosition();

        float fromUnitValue = conversionUnitValues.getFloat(fromIndex, 1.0f);
        float toUnitValue = conversionUnitValues.getFloat(toIndex, 1.0f);

        float result = fromValue * fromUnitValue / toUnitValue;
        secondValueEditText.setText(
            String.format(Locale.getDefault(), "%.2f", result)
        );
    }

    private void reportError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
