package com.example.z.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseModel implements Serializable {

	protected int Errcode = 0;

	public int getErrcode() {
		return Errcode;
	}

	public void setErrcode(int errcode) {
		Errcode = errcode;
	}

}
