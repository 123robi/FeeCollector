package eu.rkosir.feecollector.helper;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;


public class MyYAxisValueFormatter implements IValueFormatter {
	private String sign;
	private DecimalFormat mFormat;

	public MyYAxisValueFormatter(String sign) {
		this.sign = sign;
		mFormat = new DecimalFormat("###,###,##0.0");
	}

	@Override
	public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
		return (int)value + " " + sign;
	}
}
