package com.parang.datepicker;

import org.joda.time.LocalDate;
import net.simonvt.numberpicker.NumberPicker;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class ParangDatePicker extends RelativeLayout {
    public static final String G_DAY = "gDay";
    public static final String G_MONTH = "gMonth";
    public static final String G_YEAR = "gYear";
    public static final String J_DAY = "jDay";
    public static final String J_MONTH = "jMonth";
    public static final String J_YEAR = "jYear";
    private String[][] monthNames = {
    		{ "�?روردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن", "اس�?ند" },
    		{ "Janurary", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }};
    private int[][] monthDays = {
    		{ 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29 },
    		{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }};
    
    private NumberPicker npDay;
    private NumberPicker npMonth;
    private NumberPicker npYear;
    int calendarType = 0;
    private LocalDate date;
    private int minYear = 1, maxYear = 9999;
    private boolean maxNow = false, minNow = false;
    private int yearRange;
    
    public boolean isMaxNow() {
		return maxNow;
	}

	public void setMaxNow(boolean maxNow) {
		this.maxNow = maxNow;
	}
	
	public boolean isMinNow() {
		return minNow;
	}

	public void setMinNow(boolean minNow) {
		this.minNow = minNow;
	}

	public LocalDate getDate() {
    	buildResult();
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
		if(this.calendarType == 0){			
			JDF jdf = new JDF(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
			npYear.setValue(jdf.getIranianYear());
			npMonth.setValue(jdf.getIranianMonth());
			npDay.setValue(jdf.getIranianDay());
		}
		else if(this.calendarType == 1){
			npYear.setValue(date.getYear());
			npMonth.setValue(date.getMonthOfYear());
			npDay.setValue(date.getDayOfMonth());
		}
	}

	public int getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(String calendarTypeString) {
		if(calendarTypeString.equals("Solar"))
			setCalendarType(0);
		else if(calendarTypeString.equals("Gregorian"))
			setCalendarType(1);
	}
	
	public void setCalendarType(int calendarType) {
		this.calendarType = calendarType;
		initializeCalendar();
	}

	private void initializeCalendar(){
		for(int i = 0; i < monthNames.length; i++){
        	monthNames[calendarType][i] = DariGlyphUtils.reshapeText(monthNames[calendarType][i]);
        }
        
        JDF jdf = new JDF();
        int iranianYear = jdf.getIranianYear();
        int iranianMonth = jdf.getIranianMonth();
        int iranianDay = jdf.getIranianDay();

        if(maxNow == false && minNow == false){
        	npYear.setMinValue(getMinYear());
        	npYear.setMaxValue(getMaxYear());
        }
        else if(maxNow == true && minNow == false){
        	if(calendarType == 0)
        		npYear.setMaxValue(jdf.getIranianYear());
        	else if(calendarType ==1)
        		npYear.setMaxValue(jdf.getGregorianYear());
        	npYear.setMinValue(npYear.getMaxValue() - yearRange);
        }
        else if(maxNow == false && minNow == true){
        	if(calendarType == 0)
        		npYear.setMinValue(jdf.getIranianYear());
        	else if(calendarType == 1)
        		npYear.setMinValue(jdf.getGregorianYear());
        	npYear.setMaxValue(npYear.getMinValue() + yearRange);
        }
        else if(maxNow == true && minNow == true){
        	if(calendarType == 0){
        		npYear.setMinValue(jdf.getIranianYear());
        		npYear.setMaxValue(jdf.getIranianYear());
        	}
        	else if(calendarType == 1){
        		npYear.setMinValue(jdf.getGregorianYear());
        		npYear.setMaxValue(jdf.getGregorianYear());
        	}
        }
        
        npYear.setWrapSelectorWheel(true);
        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
        npMonth.setDisplayedValues(monthNames[calendarType]);

        npDay.setMinValue(1);
        npDay.setMaxValue(31);

        if(calendarType == 0){
        	npYear.setValue(iranianYear);
        	npMonth.setValue(iranianMonth);
        	npDay.setValue(iranianDay);
        }
        else if(calendarType == 1){
        	npYear.setValue(jdf.getGregorianYear());
        	npMonth.setValue(jdf.getGregorianMonth());
        	npDay.setValue(jdf.getGregorianDay());
        }
	}
	
	public ParangDatePicker(Context context, AttributeSet attr){
    	super(context, attr);
    	initialize(context);
    }
    
    public ParangDatePicker(Context context){
        super(context);
        initialize(context);
        setCalendarType(0);
    }

    private void initialize(Context context){
    	final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.full_date_picker, this);
        
        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (picker == npMonth) {
                	if(calendarType == 0){//Solar
	                    if (newVal != 12) {
	                        npDay.setMaxValue(monthDays[calendarType][newVal - 1]);
	                    }
	                    else{
	                    	if(isSolarLeap(npYear.getValue()))
	                    		npDay.setMaxValue(30);
	                    	else
	                    		npDay.setMaxValue(29);
	                    }
                	} else if(calendarType == 0){//Gregorian
                		if (newVal != 2) {
		                    npDay.setMaxValue(monthDays[calendarType][newVal - 1]);
		                }
		                else{
		                	if(isGregorianLeap(npYear.getValue()))
		                   		npDay.setMaxValue(29);
		                 	else
		                   		npDay.setMaxValue(28);
		                }
	                }
                	
                }

            }
        };
        npYear = (NumberPicker) findViewById(R.id.npYear);
        npMonth = (NumberPicker) findViewById(R.id.npMonth);
        npDay = (NumberPicker) findViewById(R.id.npDay);
        
        npMonth.setOnValueChangedListener(onChangeListener);
    }
    
	public void buildResult(){
    	int newIrYear = npYear.getValue();
        int newIrMonth = npMonth.getValue();
        int newIrDay = npDay.getValue();

        JDF jdf = new JDF();
        
        if(calendarType == 0)
        	jdf.setIranianDate(newIrYear, newIrMonth, newIrDay);
        else if(calendarType == 1)
        	jdf.setGregorianDate(newIrYear, newIrMonth, newIrDay);

		this.date = new LocalDate(jdf.getGregorianYear(), jdf.getGregorianMonth(), jdf.getGregorianDay());
    }
    
    boolean isSolarLeap(int year)
    {
      return ((((((year - ((year > 0) ? 474 : 473)) % 2820) + 474) + 38) * 682) % 2816) < 682;
    }
    
    boolean isGregorianLeap(int year){
    	if(year % 4 != 0)
    		return false;
    	
    	if(year % 100 != 0)
    		return true;
    	
    	if(year % 400 == 0)
    		return true;
    	
    	return false;
    }

	public int getMaxYear() {
		return maxYear;
	}

	public void setMaxYear(int maxYear) {
        npYear.setMaxValue(maxYear);
        npYear.setWrapSelectorWheel(true);
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMinYear(int minYear) {
		npYear.setMinValue(minYear);
        npYear.setWrapSelectorWheel(true);
	}

	public int getYearRange() {
		return yearRange;
	}

	public void setYearRange(int yearRange) {
		this.yearRange = yearRange;
	}
}