package com.ga.wyc.domain.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Area implements Serializable {
	private String adcode;
	private String citycode;
	private String pdcode;
	private String name;
	private String center;
	private String level;
	private String label;
	private String value;
}
