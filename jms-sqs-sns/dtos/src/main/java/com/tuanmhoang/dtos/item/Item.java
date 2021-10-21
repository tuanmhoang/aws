package com.tuanmhoang.dtos.item;

import com.tuanmhoang.dtos.type.ItemType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
	private long id;
	private String name;
	private ItemType type;
	private int quantity;
	private String description;

}
