package org.example.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverAPIResult {
    private List<NaverAPIResultItem> items;

    public List<NaverAPIResultItem> getItems() {
        return items;
    }

    public void setItems(List<NaverAPIResultItem> items) {
        this.items = items;
    }
}
