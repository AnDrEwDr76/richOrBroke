package com.example.demotri.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Controller
public class JustController {

    @RequestMapping(value = "/richOrBroke")
    public String pretty(@RequestParam(
            // можно указать валюту (например: "http://localhost:8080/richOrBroke?name=BYN")
            // по умолчанию - "RUB"
            name = "name", required = false, defaultValue = "RUB") String name, Model model) {

        LocalDate today = LocalDate.now();          // сегодняшняя дата
        LocalDate yesterday = today.minusDays(1);   // вчерашняя дата
        Double todayRate = (getRateFromOpenexchangerates(today, name));         // сегодняшний рейтинг
        Double yesterdayRate = (getRateFromOpenexchangerates(yesterday, name)); // вчерашний рейтинг
        String wordToFindGif = "rich";
        if (todayRate > yesterdayRate) {
            wordToFindGif = "broke";
        }
        String figUrl = getFigureUrlFromGiphy(wordToFindGif); // ссылка на нужную картинку
        model.addAttribute("resultUrl", figUrl); // добавляем ссылку в отображение
        return "richOrBroke";
    }

    // получение рейтинга валюты
    private double getRateFromOpenexchangerates(LocalDate day, String exchangeCode) {
        double result = 0;
        LocalDate today = LocalDate.now();
        String uri = null;
        if (today.isEqual(day)) {
            uri = "https://openexchangerates.org/api/latest.json?app_id=fbfa7a517e2a47ccae1204c8bc253a3e";
        } else {
            uri = "https://openexchangerates.org/api/historical/" + day + ".json?app_id=fbfa7a517e2a47ccae1204c8bc253a3e";
        }
        RestTemplate restTemplate = new RestTemplate();
        String wholeJsonString = restTemplate.getForObject(uri,  String.class);

        try {
            JSONObject wholeJson = new JSONObject(wholeJsonString);
            result = wholeJson.getJSONObject("rates").getDouble(exchangeCode);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return result;
    }

    // получение гифки
    private String getFigureUrlFromGiphy(String wordToFindGif) {
        final String uri = "https://api.giphy.com/v1/gifs/random?api_key=OQhhAr2O3MdV0ehld9CrmxMo3K6m3IJy&tag=" + wordToFindGif + "&limit=1";
        RestTemplate restTemplate = new RestTemplate();
        String wholeJsonString = restTemplate.getForObject(uri,  String.class);
        String result = null;
        try {
            JSONObject wholeJson = new JSONObject(wholeJsonString);
            result = wholeJson
                    .getJSONObject("data")
                    .getJSONObject("images")
                    .getJSONObject("downsized")
                    .getString("url");
        } catch (JSONException e) {
            System.out.println(e);
        }
        return result;
    }
}