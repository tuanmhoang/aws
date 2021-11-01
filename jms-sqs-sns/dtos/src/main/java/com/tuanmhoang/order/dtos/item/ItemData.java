package com.tuanmhoang.order.dtos.item;

import com.tuanmhoang.order.dtos.type.ItemType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemData {
	
	private String name;
	private ItemType type;
	private String description;
	private String imgUrl;
	private int maxAllowed;

}
