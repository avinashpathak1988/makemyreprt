package com.luminousinfoways.makemyreport.pojo;

import java.io.Serializable;
import java.util.HashMap;

public class FbField implements Serializable {
	
	//private String fb_form_id_namevalue;
	//private String fb_assign_user_id_namevalue;
	private HashMap<String, Object> field_id_namevale;
	
	/*public String getFb_form_id_namevalue() {
		return fb_form_id_namevalue;
	}
	public void setFb_form_id_namevalue(String fb_form_id_namevalue) {
		this.fb_form_id_namevalue = fb_form_id_namevalue;
	}
	public String getFb_assign_user_id_namevalue() {
		return fb_assign_user_id_namevalue;
	}
	public void setFb_assign_user_id_namevalue(
			String fb_assign_user_id_namevalue) {
		this.fb_assign_user_id_namevalue = fb_assign_user_id_namevalue;
	}*/
	/*
	 * HashMap<String, Object>
	 * String : fieldID
	 * Object : String / String[]
	 */
	public HashMap<String, Object> getField_id_namevale_list() {
		return field_id_namevale;
	}
	public void setField_id_namevale_list(
			HashMap<String, Object> field_id_namevale) {
		this.field_id_namevale = field_id_namevale;
	}
}
