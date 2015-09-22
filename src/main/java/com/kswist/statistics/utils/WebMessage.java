package com.kswist.statistics.utils;

import javax.inject.Named;

@Named
public class WebMessage extends WebLog {

	private static final long serialVersionUID = -8704594425015804768L;

	public WebMessage() {
		super(Type.MESSAGE);
	}

}
