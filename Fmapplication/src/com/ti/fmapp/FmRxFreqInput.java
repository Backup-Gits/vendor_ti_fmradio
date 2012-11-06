/*
 * TI's FM
 *
 * Copyright 2001-2010 Texas Instruments, Inc. - http://www.ti.com/
 * Copyright (C) 2010 Sony Ericsson Mobile Communications AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*******************************************************************************\
 *
 *   FILE NAME:      FmRxFreqInput.java
 *
 *   BRIEF:          This file defines the API of the FM Rx stack.
 *
 *   DESCRIPTION:    General
 *
 *
 *
 *   AUTHOR:
 *
 \*******************************************************************************/
package com.ti.fmapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ti.fm.IFmConstants;
import com.ti.fmapp.utils.Utils;

/**
 * FmRxFreqInput asks the user to enter a valid frequency for tuning the radio.
 * It is an activity that appears as a dialog.
 */

public class FmRxFreqInput extends Activity implements OnKeyListener,
        View.OnClickListener, IFmConstants, FmRxAppConstants {

    private EditText mUserText;
    private boolean mPrintDebugInfo = true;
    private static final boolean DBG = true;

    /**
     * Called when the activity is first created.
     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fmrxfreq);
        mPrintDebugInfo = Preferences.getPrintDebugInfo(this);
        initControls();
    }

    private void initControls() {        /* listening to key click or a key input into the exit text box */
        mUserText = (EditText) findViewById(R.id.txtFrequency);        /* wait for key inputs or mouse clicks in the edit box */
        mUserText.setOnKeyListener(this);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        TextView mBandRange = (TextView) findViewById(R.id.freqRange);
        if (FmRxApp.sBand == FM_BAND_EUROPE_US) {
            mBandRange.setText(getText(R.string.freqRangeEurope));
        } else {
            mBandRange.setText(getText(R.string.freqRangeJapan));

        }

    }

    private void writeFrequency() {
        try {
            Float iFreq = Float.parseFloat(mUserText.getText().toString());
            Float validFreq = UpdateFrequency(iFreq);
            if (validFreq != 0) {
                // reset the text in edit box for the next entry
                mUserText.setText(null);

                Bundle bundle = new Bundle();
                bundle.putFloat(FREQ_VALUE, validFreq);
                Intent result = new Intent();
                result.putExtras(bundle);
                setResult(RESULT_OK, result);
                finish();

            } else {
                new AlertDialog.Builder(this).setIcon(
                        android.R.drawable.ic_dialog_alert).setMessage(
                        R.string.valid_frequency).setNegativeButton(
                        android.R.string.ok, null).show();
                mUserText.setText(null);
            }
        } catch (NumberFormatException nfe) {
            if (DBG) {
                Utils.debugFunc("NumberFormatException:" + nfe.getMessage(), Log.DEBUG, mPrintDebugInfo);
            }
            new AlertDialog.Builder(this).setIcon(
                    android.R.drawable.ic_dialog_alert).setMessage(
                    R.string.number).setNegativeButton(
                    android.R.string.ok, null).show();
            mUserText.setText(null);
        }

    }

    /* This is a method implementation of OnKeyListener */
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    writeFrequency();
                    return true;
            }
        }
        return false;
    }

    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnOk:
                writeFrequency();
                break;
            case R.id.btnCancel:
                finish();
                break;

        }
    }

    static float BaseFreq() {
        return FmRxApp.sBand == FM_BAND_JAPAN ? FM_FIRST_FREQ_JAPAN_KHZ_FLOAT
                : FM_FIRST_FREQ_US_EUROPE_KHZ_FLOAT;
    }

    static float LastFreq() {
        return FmRxApp.sBand == FM_BAND_JAPAN ? FM_LAST_FREQ_JAPAN_KHZ_FLOAT
                : FM_LAST_FREQ_US_EUROPE_KHZ_FLOAT;
    }

    // Update the Frequency label with the given value
    float UpdateFrequency(float freq) {
        if (DBG) {
            Utils.debugFunc("FM App: UpdateFrequency %d." + freq, Log.DEBUG, mPrintDebugInfo);
        }
        if (freq < BaseFreq() || freq > LastFreq()) {
            freq = 0;
        }
        return freq;
    }

}
