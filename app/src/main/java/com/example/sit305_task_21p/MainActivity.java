package com.example.sit305_task_21p;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Spinner conversionTypeSpinner;
    private Spinner sourceUnitSpinner;
    private Spinner targetUnitSpinner;
    private EditText inputValue;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conversionTypeSpinner = findViewById(R.id.conversionType);
        sourceUnitSpinner = findViewById(R.id.Source_Spinner);
        targetUnitSpinner = findViewById(R.id.Target_Spinner);
        inputValue = findViewById(R.id.inputValue);
        resultView = findViewById(R.id.resultView);

        initializeConversionTypeSpinner();
        setupListeners();
    }

    private void initializeConversionTypeSpinner() {
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.Conversion_Type, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conversionTypeSpinner.setAdapter(typeAdapter);
    }

    private void setupListeners() {
        conversionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSubSpinners(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AdapterView.OnItemSelectedListener unitChangeListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateAndDisplayResult();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        sourceUnitSpinner.setOnItemSelectedListener(unitChangeListener);
        targetUnitSpinner.setOnItemSelectedListener(unitChangeListener);

        inputValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAndDisplayResult();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updateSubSpinners(String unitType) {
        int sourceArrayId = getSourceArrayIdByType(unitType);
        ArrayAdapter<CharSequence> sourceAdapter = ArrayAdapter.createFromResource(
                this, sourceArrayId, android.R.layout.simple_spinner_item);
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceUnitSpinner.setAdapter(sourceAdapter);
        targetUnitSpinner.setAdapter(sourceAdapter);  // Same adapter for simplicity in this example
    }

    private int getSourceArrayIdByType(String unitType) {
        switch (unitType) {
            case "Length":
                return R.array.Length;
            case "Weight":
                return R.array.Weight;
            case "Temperature":
                return R.array.Temperature;
            default:
                return R.array.Length; // Default case
        }
    }

    private void calculateAndDisplayResult() {
        try {
            double value = Double.parseDouble(inputValue.getText().toString());
            String sourceUnit = sourceUnitSpinner.getSelectedItem().toString();
            String targetUnit = targetUnitSpinner.getSelectedItem().toString();
            double result = convertUnits(sourceUnit, targetUnit, value);
            resultView.setText(String.format(Locale.getDefault(), "%.2f", result));
        } catch (NumberFormatException e) {
            resultView.setText("Invalid input");
        }
    }

    private double convertUnits(String sourceUnit, String targetUnit, double value) {
        // Length conversions to meters
        if (Arrays.asList("inch", "foot", "yard", "mile", "cm", "km").contains(sourceUnit)) {
            // Convert source unit to centimeters first
            double valueInCm = convertLengthToCentimeters(sourceUnit, value);
            return convertCentimetersToTargetLengthUnit(targetUnit, valueInCm);
        }

        // Weight conversions to kilograms
        if (Arrays.asList("Pound", "Ounce", "Ton").contains(sourceUnit)) {
            // Convert source unit to grams first
            double valueInGrams = convertWeightToGrams(sourceUnit, value);
            return convertGramsToTargetWeightUnit(targetUnit, valueInGrams);
        }

        // Temperature conversions
        if (Arrays.asList("C", "F", "K").contains(sourceUnit)) {
            return convertTemperature(sourceUnit, targetUnit, value);
        }

        // Return value if no conversion rule applies
        return value;
    }

    private double convertLengthToCentimeters(String unit, double value) {
        switch (unit) {
            case "inch": return value * 2.54;
            case "foot": return value * 30.48;
            case "yard": return value * 91.44;
            case "mile": return value * 160934;
            case "cm": return value;
            case "km": return value * 100000;
            default: return value;
        }
    }

    private double convertCentimetersToTargetLengthUnit(String unit, double valueInCm) {
        switch (unit) {
            case "inch": return valueInCm / 2.54;
            case "foot": return valueInCm / 30.48;
            case "yard": return valueInCm / 91.44;
            case "mile": return valueInCm / 160934;
            case "cm": return valueInCm;
            case "km": return valueInCm / 100000;
            default: return valueInCm;
        }
    }

    private double convertWeightToGrams(String unit, double value) {
        switch (unit) {
            case "Pound": return value * 453.592;
            case "Ounce": return value * 28.3495;
            case "Ton": return value * 907185;
            default: return value;
        }
    }

    private double convertGramsToTargetWeightUnit(String unit, double valueInGrams) {
        switch (unit) {
            case "Pound": return valueInGrams / 453.592;
            case "Ounce": return valueInGrams / 28.3495;
            case "Ton": return valueInGrams / 907185;
            default: return valueInGrams;
        }
    }

    private double convertTemperature(String sourceUnit, String targetUnit, double value) {
        double result = value;
        switch (sourceUnit) {
            case "C":
                if ("F".equals(targetUnit)) {
                    result = (value * 1.8) + 32;
                } else if ("K".equals(targetUnit)) {
                    result = value + 273.15;
                }
                break;
            case "F":
                if ("C".equals(targetUnit)) {
                    result = (value - 32) / 1.8;
                } else if ("K".equals(targetUnit)) {
                    result = (value - 32) / 1.8 + 273.15;
                }
                break;
            case "K":
                if ("C".equals(targetUnit)) {
                    result = value - 273.15;
                } else if ("F".equals(targetUnit)) {
                    result = (value - 273.15) * 1.8 + 32;
                }
                break;
        }
        return result;
    }


}
