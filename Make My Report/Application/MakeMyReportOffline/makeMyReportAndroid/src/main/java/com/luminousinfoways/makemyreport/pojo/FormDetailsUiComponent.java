package com.luminousinfoways.makemyreport.pojo;

import java.io.Serializable;
import java.util.List;

import android.view.View;

public class FormDetailsUiComponent implements Serializable {
	
	private View view;
	private String fieldID;
	private String viewType;
	private String fieldName;
	private String isCompulsory;
	private List<Options> options;
	private String fieldValue;
	private String fieldDefaultVal;
	private String is_multiple;
	private String min_range;
	private String max_range;
	private String validation_type;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    public String getFieldDefaultVal() {
		return fieldDefaultVal;
	}
	public void setFieldDefaultVal(String fieldDefaultVal) {
		this.fieldDefaultVal = fieldDefaultVal;
	}
	public String getIs_multiple() {
		return is_multiple;
	}
	public void setIs_multiple(String is_multiple) {
		this.is_multiple = is_multiple;
	}
	public String getMin_range() {
		return min_range;
	}
	public void setMin_range(String min_range) {
		this.min_range = min_range;
	}
	public String getMax_range() {
		return max_range;
	}
	public void setMax_range(String max_range) {
		this.max_range = max_range;
	}
	public String getValidation_type() {
		return validation_type;
	}
	public void setValidation_type(String validation_type) {
		this.validation_type = validation_type;
	}
	public List<Options> getOptions() {
		return options;
	}
	public void setOptions(List<Options> options) {
		this.options = options;
	}
	public String getIsCompulsory() {
		return isCompulsory;
	}
	public void setIsCompulsory(String isCompulsory) {
		this.isCompulsory = isCompulsory;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldID() {
		return fieldID;
	}
	
	public void setFieldID(String fieldID) {
		this.fieldID = fieldID;
	}
	
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public String getViewType() {
		return viewType;
	}
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
