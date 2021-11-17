package com.tuanhm.aws.lambda.product.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String id;

    private String name;

    private BigDecimal price;

    private String imgUrl;

}
