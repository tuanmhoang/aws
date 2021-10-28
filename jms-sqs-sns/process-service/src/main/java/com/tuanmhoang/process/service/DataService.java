package com.tuanmhoang.process.service;

import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.item.Item;
import com.tuanmhoang.order.dtos.item.ItemData;
import com.tuanmhoang.order.dtos.item.ItemsDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataService {

	private Map<Integer, ItemData> appData;
	
	public DataService() throws IOException {
		String jsonData = loadData();
		Gson gson = new Gson();
		ItemsDto data = gson.fromJson(jsonData,ItemsDto.class);
		List<Item> items = data.getItems();
		appData = items.stream()
						.collect(Collectors.toMap(Item::getId, Item::getData));
	}
	
	public String loadData() throws IOException {
		File resource = new ClassPathResource("data/data.json").getFile();
		return new String(Files.readAllBytes(resource.toPath()));
	}

	public Map<Integer, ItemData> getAppData() {
		return appData;
	}
}
