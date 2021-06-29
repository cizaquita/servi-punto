/*******************************************************************************
 *  @author 
 *  Ybrant Digital
 *  Copyright (C) Ybrant Digital
 *  http://www.ybrantdigital.com
 *******************************************************************************/
package com.micaja.servipunto.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter  implements InputFilter {

Pattern mPattern;
private int digitsBeforeZero = 15;
private int digitsAfterZero  = 2;

public DecimalInputFilter() {
    mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero) + "})?)||(\\.)?");
}

public DecimalInputFilter(int digitsBeforeZero, int digitsAfterZero) {
    mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero) + "}+((\\.[0-9]{0," + (digitsAfterZero) + "})?)||(\\.)?");
}

@Override
public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

    String formatedSource = source.subSequence(start, end).toString();

    String destPrefix = dest.subSequence(0, dstart).toString();

    String destSuffix = dest.subSequence(dend, dest.length()).toString();

    String result = destPrefix + formatedSource + destSuffix;

    result = result.replace(",", ".");

    Matcher matcher = mPattern.matcher(result);

    if (matcher.matches()) {
        return null;
    }

    return "";
}

 }