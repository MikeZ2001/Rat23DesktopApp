package com.example.ratatouille23desktopclient.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OFFAPISuggestion {

    @SerializedName("suggestions")
    private List<String> suggestions;

    public List<String> getSuggestions() {
        return suggestions;
    }
}
