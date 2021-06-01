package com.example.blogAPI.blogAPI.models;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class BlogEntryResponses {

    @Data
    public static class BlogEntry {
        private int id;
        private String title;
        private String entryText;

        public BlogEntry() {
        }

        public BlogEntry(int id, String title, String entryText) {
            this.id = id;
            this.title = title;
            this.entryText = entryText;
        }
    }

    @Data
    public static class BlogEntries {
        private List<BlogEntry> blogEntries = new LinkedList<>();

        public void add(BlogEntry entry) {
            blogEntries.add(entry);
        }
    }

}
