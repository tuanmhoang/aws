package com.tuanmhoang.order.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.tuanmhoang.dtos.response.OrderResponseDto;

@Component
public class OrderAssembler implements RepresentationModelAssembler<OrderResponseDto, EntityModel<OrderResponseDto>>{

	@Override
	public EntityModel<OrderResponseDto> toModel(OrderResponseDto entity) {
		return null;
	}

}
