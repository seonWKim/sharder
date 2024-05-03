package com.sharder.api;

import static com.sharder.api.ApiPath.DELETE_V1;
import static com.sharder.api.ApiPath.INSERT_V1;
import static com.sharder.api.ApiPath.SELECT_V1;
import static com.sharder.api.ApiPath.UPDATE_V1;

import org.springframework.stereotype.Component;

import com.sharder.SimpleQueryService;

import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.annotation.Blocking;
import com.linecorp.armeria.server.annotation.Delete;
import com.linecorp.armeria.server.annotation.Post;
import com.linecorp.armeria.server.annotation.Put;

@Blocking
@Component
public class SimpleQueryRestController {

    private final SimpleQueryService simpleQueryService;

    public SimpleQueryRestController(SimpleQueryService simpleQueryService) {
        this.simpleQueryService = simpleQueryService;
    }

    @Post(SELECT_V1)
    public HttpResponse select(SimpleQueryRequest request) {
        return HttpResponse.of(200);
    }

    @Post(INSERT_V1)
    public HttpResponse insert(SimpleQueryRequest request) {
        return HttpResponse.of(200);
    }

    @Put(UPDATE_V1)
    public HttpResponse update(SimpleQueryRequest request) {
        return HttpResponse.of(200);
    }

    @Delete(DELETE_V1)
    public HttpResponse delete(SimpleQueryRequest request) {
        return HttpResponse.of(200);
    }
}
