package com.tuanmhoang.dtos.response;

import com.tuanmhoang.dtos.OrderedItem;

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
	private OrderedItem orderedItem;
	private String message;
}
