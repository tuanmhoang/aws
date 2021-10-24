package com.tuanmhoang.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.OrderedItem;

public class OrderItemTestUtils {

	public OrderedItem createMockOrderedItems() throws IOException, URISyntaxException {
//		OrderedItems orderedItems = new OrderedItems();
//		List<Item> items = new ArrayList<>();
//		items.add(new Item(1L, "Horilka", ItemType.LIQUID, 330, ""));

		ClassLoader loader = ClassLoader.getSystemClassLoader();

		String json = Files.lines(Paths.get(loader.getResource("data.json").toURI())).parallel()
				.collect(Collectors.joining());

		Gson gson = new Gson();
		OrderedItem orderedItems = gson.fromJson(json, OrderedItem.class);

//		items.add(new Item(1L, "Horilka", ItemType.LIQUID, 330));
//		items.add(new Item(1L, "Horilka", ItemType.LIQUID, 330));
//		items.add(new Item(1L, "Horilka", ItemType.LIQUID, 330));

		// Varenyky
		// Holubtsi
		// Chicken Kyiv
		// Horilka -> ruou
		// Kvass -> ruou
		// Uzvar -> tra
		// SUGAR CANE JUICE
		// Hue’s Royal Tea
		// Coffee
		return orderedItems;
	}

}
