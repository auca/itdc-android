package kg.auca.example.converter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onMenuClick(View view) {
        switch (view.getId()) {
            case R.id.calculatorButton: {
                Intent intent = new Intent(this, CalculatorActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.lengthUnitConverterButton: {
                Intent intent = new Intent(this, UnitConverterActivity.class);
                intent.putExtra("UNIT_CONVERSION_NAMES", R.array.lengthConversionUnitNames);
                intent.putExtra("UNIT_CONVERSION_VALUES", R.array.lengthConversionValues);
                startActivity(intent);
                break;
            }
            case R.id.massUnitConverterButton: {
                Intent intent = new Intent(this, UnitConverterActivity.class);
                intent.putExtra("UNIT_CONVERSION_NAMES", R.array.massConversionUnitNames);
                intent.putExtra("UNIT_CONVERSION_VALUES", R.array.massConversionValues);
                startActivity(intent);
                break;
            }
            case R.id.currencyConverterButton: {
                Intent intent = new Intent(this, CurrencyConverterActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

}
