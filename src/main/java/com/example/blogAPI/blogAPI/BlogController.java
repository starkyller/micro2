package com.example.blogAPI.blogAPI;

import com.example.blogAPI.blogAPI.models.BlogEntryResponses;
import com.example.blogentrymanager.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("entries")
public class BlogController {
    @Value(value = "${BLOG_ENTRY_MANAGER_GRPC_HOST}")
    private String host;

    @Value(value = "${BLOG_ENTRY_MANAGER_GRPC_PORT}")
    private int port;

    private ManagedChannel channel;

    @PostConstruct
    void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public BlogEntryResponses.BlogEntries getAll() {
        BlogEntryServiceGrpc.BlogEntryServiceBlockingStub client = BlogEntryServiceGrpc.newBlockingStub(channel);

        GetBlogEntriesRequest request = GetBlogEntriesRequest.newBuilder().build();

        GetBlogEntriesResponse grpc_response = client.getBlogEntries(request);

        BlogEntryResponses.BlogEntries response = new BlogEntryResponses.BlogEntries();

        grpc_response.getEntriesList().forEach( entry -> response.add(
                new BlogEntryResponses.BlogEntry(
                        entry.getId(),
                        entry.getTitle(),
                        entry.getEntryText()
                )
        ));

        return response;
    }

    @GetMapping("{blogEntryId}")
    @ResponseStatus(HttpStatus.OK)
    public BlogEntryResponses.BlogEntry getById(@PathVariable String blogEntryId) {
        BlogEntryServiceGrpc.BlogEntryServiceBlockingStub client = BlogEntryServiceGrpc.newBlockingStub(channel);

        GetBlogEntryByIdRequest request = GetBlogEntryByIdRequest
                .newBuilder()
                .setId(blogEntryId)
                .build();
        BlogEntryResponses.BlogEntry entry = new BlogEntryResponses.BlogEntry();

        try {
            GetBlogEntryByIdResponse grpc_response = client.getBlogEntryById(request);



            entry.setId(grpc_response.getEntry().getId());
            entry.setTitle(grpc_response.getEntry().getTitle());
            entry.setEntryText(grpc_response.getEntry().getEntryText());

            return entry;
        }catch (Exception ex)  {
            return entry;
        }


    }
}


