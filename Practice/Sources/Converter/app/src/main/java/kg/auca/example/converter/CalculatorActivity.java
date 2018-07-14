package kg.auca.example.converter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CalculatorActivity extends AppCompatActivity {
    private Calculator calculator;

    private EditText inputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        calculator = new Calculator();

        inputEditText = findViewById(R.id.inputEditText);

        updateInputField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lengthUnitConverterMenuItem: {
                Intent intent = new Intent(this, UnitConverterActivity.class);
                intent.putExtra("UNIT_CONVERSION_NAMES", R.array.lengthConversionUnitNames);
                intent.putExtra("UNIT_CONVERSION_VALUES", R.array.lengthConversionValues);
                startActivity(intent);
                return true;
            } case R.id.massUnitConverterMenuItem: {
                Intent intent = new Intent(this, UnitConverterActivity.class);
                intent.putExtra("UNIT_CONVERSION_NAMES", R.array.massConversionUnitNames);
                intent.putExtra("UNIT_CONVERSION_VALUES", R.array.massConversionValues);
                startActivity(intent);
                return true;
            } case R.id.currencyConverterMenuItem: {
                Intent intent = new Intent(this, CurrencyConverterActivity.class);
                startActivity(intent);
                return true;
            } default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClearButtonClick(View view) {
        calculator.init();
        updateInputField();
    }

    public void onChangeSignButtonClick(View view) {
        calculator.negate();
        updateInputField();
    }

    public void onNumericButtonClick(View view) {
        String text = ((Button) view).getText().toString();
        calculator.addDigit(Integer.parseInt(text));
        updateInputField();
    }

    public void onDecimalPointButtonClick(View view) {
        calculator.addDecimalPoint();
        updateInputField();
    }

    public void onBinaryOperationButtonClick(View view) {
        String text = view.getTag().toString().toUpperCase();

        try {
            calculator.performBinaryOperation(Calculator.Operation.valueOf(text));
        } catch (ArithmeticException exception) {
            reportError(getString(R.string.DivisionByZeroErrorText));
        }

        updateInputField();
    }

    public void onCalculateResultButtonClick(View view) {
        try {
            calculator.calculate();
        } catch (ArithmeticException exception) {
            reportError(getString(R.string.DivisionByZeroErrorText));
        }

        updateInputField();
    }

    private void updateInputField() {
        inputEditText.setText(calculator.getCurrentValue().toPlainString());
    }

    private void reportError(CharSequence errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
