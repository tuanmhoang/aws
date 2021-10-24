package com.tuanmhoang.process.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.tuanmhoang.order.dtos.item.Item;
import com.tuanmhoang.order.dtos.item.ItemData;
import com.tuanmhoang.order.dtos.item.ItemsDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DataService {
	
	private Gson gson = new Gson();
	private Map<Long, ItemData> appData = new HashMap<>();
	
	public DataService() throws IOException {
		String jsonData = loadData();
		ItemsDto data = gson.fromJson(jsonData,ItemsDto.class);
		List<Item> items = data.getItems();
		appData = items.stream()
						.collect(Collectors.toMap(Item::getId, Item::getData));
	}
	
	public String loadData() throws IOException {
		File resource = new ClassPathResource("data/data.json").getFile();
		String text = new String(Files.readAllBytes(resource.toPath()));
		return text;
	}

	public Map<Long, ItemData> getAppData() {
		return appData;
	}

	public void setAppData(Map<Long, ItemData> appData) {
		this.appData = appData;
	}

}
