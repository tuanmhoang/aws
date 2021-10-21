package com.tuanmhoang.dtos.response;

import java.util.List;

import com.tuanmhoang.dtos.item.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {
	
	private Long id;
	private List<Item> orderedItems;
	private String message;
}
