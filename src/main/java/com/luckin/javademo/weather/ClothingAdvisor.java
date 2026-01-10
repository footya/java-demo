package com.luckin.javademo.weather;

import org.springframework.stereotype.Component;

@Component
public class ClothingAdvisor {
    public String advise(Integer temperature, String weather) {
        int t = temperature == null ? 0 : temperature;
        String base;
        if (t <= 0) {
            base = "建议穿厚羽绒服，注意防寒保暖";
        } else if (t <= 10) {
            base = "建议穿棉服或厚外套，注意保暖";
        } else if (t <= 20) {
            base = "建议穿外套或卫衣，早晚可加一件";
        } else if (t <= 28) {
            base = "建议穿薄长袖或短袖，注意防晒";
        } else {
            base = "建议穿短袖短裤，注意防暑补水";
        }

        String w = weather == null ? "" : weather;
        if (w.contains("雨") || w.contains("雷")) {
            return base + "，建议携带雨具";
        }
        if (w.contains("雪")) {
            return base + "，注意防滑保暖";
        }
        if (w.contains("雾") || w.contains("霾")) {
            return base + "，建议注意出行能见度";
        }
        return base;
    }
}


