package com.tuanhm.dtos;

import java.util.List;

import com.tuanhm.dtos.item.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	private Long id;
	private List<Item> orderedItems;

}
