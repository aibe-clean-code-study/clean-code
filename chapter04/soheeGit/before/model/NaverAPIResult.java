package org.example.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverAPIResult {
    private List<NaverAPIResultItem> items;  // 여러 개의 책 항목을 담는 리스트

    public List<NaverAPIResultItem> getItems() {
        return items;
    }

    public void setItems(List<NaverAPIResultItem> items) {
        this.items = items;
    }
}
